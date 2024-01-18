package com.rmit.bookflowapp.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.rmit.bookflowapp.Model.User;
import com.rmit.bookflowapp.activity.MainActivity;
import com.rmit.bookflowapp.databinding.FragmentUserProfileBinding;
import com.rmit.bookflowapp.repository.UserRepository;
import com.squareup.picasso.Picasso;

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

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        UserRepository.getInstance().getUserById(firebaseUser.getUid()).addOnCompleteListener(new OnCompleteListener<User>() {
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
        StorageReference fileReference = storageReference.child(Long.toString(System.currentTimeMillis()));

        fileReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUri) {
                                user.setImageId(downloadUri.toString());
                                UserRepository.getInstance().updateUser(user);
                                Picasso.get().load(downloadUri.toString()).into(binding.imageView);
                            }
                        });
                    }
                });
    }

}