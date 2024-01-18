package com.rmit.bookflowapp.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rmit.bookflowapp.Model.Book;
import com.rmit.bookflowapp.Model.User;
import com.rmit.bookflowapp.activity.MainActivity;
import com.rmit.bookflowapp.databinding.FragmentUserProfileBinding;
import com.rmit.bookflowapp.repository.UserRepository;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class UserProfileFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private StorageReference storageReference;
    private static final String TAG = "UserProfileFragment";
    private FragmentUserProfileBinding binding;
    private MainActivity activity;
    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false);
        activity = (MainActivity) getActivity();
        activity.setBottomNavigationBarVisibility(true);

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.navController.navigateUp();
            }
        });

        binding.changeProfilePicture.setOnClickListener(v -> {
            openFileChooser();
        });

        storageReference = FirebaseStorage.getInstance().getReference("profile_pictures");

        Bundle arguments = getArguments();

        // end fragment if no data found
        if (arguments == null) getParentFragmentManager().popBackStack();
        String uid = arguments.getString("USER_ID");

        UserRepository.getInstance().getUserById(uid).addOnCompleteListener(new OnCompleteListener<User>() {
            @Override
            public void onComplete(@NonNull Task<User> task) {
                user = task.getResult();
                initView();
            }
        });
        return binding.getRoot();
    }

    public void initView() {
        if (user.getImageId() != null && !user.getImageId().isEmpty()) {
            Picasso.get().load(user.getImageId()).into(binding.imageView);
            Log.d(TAG, user.getImageId());
        }
        binding.textViewDisplayName.setText(user.getName());
        binding.textViewEmail.setText(user.getEmail());
        binding.textViewRole.setText(user.getRole());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            uploadImage(imageUri);
        }
    }

    private void uploadImage(Uri imageUri) {
        try {
            InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);
            ExifInterface exif = new ExifInterface(inputStream);

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            Bitmap rotatedBitmap = rotateBitmap(bitmap, orientation);

            StorageReference fileReference = storageReference.child(Long.toString(System.currentTimeMillis()));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            fileReference.putBytes(data)
                    .addOnSuccessListener(taskSnapshot -> {
                        fileReference.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            user.setImageId(downloadUri.toString());
                            UserRepository.getInstance().updateUser(user);
                            Picasso.get().load(downloadUri.toString()).into(binding.imageView);
                        });
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(270);
                break;
            default:
                return bitmap;
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

}