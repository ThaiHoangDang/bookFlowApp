package com.rmit.bookflowapp.fragment;

import android.annotation.SuppressLint;
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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rmit.bookflowapp.Model.Book;
import com.rmit.bookflowapp.Model.Chat;
import com.rmit.bookflowapp.Model.User;
import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.activity.MainActivity;
import com.rmit.bookflowapp.adapter.SearchBookAdapter;
import com.rmit.bookflowapp.databinding.FragmentUserProfileBinding;
import com.rmit.bookflowapp.interfaces.ClickCallback;
import com.rmit.bookflowapp.repository.BookRepository;
import com.rmit.bookflowapp.repository.MessageRepository;
import com.rmit.bookflowapp.repository.UserRepository;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserProfileFragment extends Fragment implements ClickCallback {
    private static final int PICK_IMAGE_REQUEST = 1;
    private StorageReference storageReference;
    private static final String TAG = "UserProfileFragment";
    private FragmentUserProfileBinding binding;
    private MainActivity activity;
    private SearchBookAdapter bookAdapter;
    private User user, currentUser;
    private ArrayList<Book> books = new ArrayList<>();
    private boolean followed = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false);
        activity = (MainActivity) getActivity();
        activity.setBottomNavigationBarVisibility(false);

        Bundle arguments = getArguments();

        // end fragment if no data found
        if (arguments == null) getParentFragmentManager().popBackStack();
        String uid = arguments.getString("USER_ID");

        // hide some components
        if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(uid)) {
            binding.changeProfilePicture.setVisibility(View.GONE);
        } else {
            binding.profileFollowChat.setVisibility(View.GONE);
        }

        UserRepository.getInstance().getUserById(uid).addOnCompleteListener(new OnCompleteListener<User>() {
            @Override
            public void onComplete(@NonNull Task<User> task) {
                user = task.getResult();
                initView();

                BookRepository.getInstance().getBooksByIds(user.getFavoriteBooks()).addOnSuccessListener(new OnSuccessListener<List<Book>>() {
                    @Override
                    public void onSuccess(List<Book> books) {
                        UserProfileFragment.this.books = new ArrayList<>(books);

                        // Notify the adapter about the data change
                        if (bookAdapter != null) bookAdapter.setBooks(UserProfileFragment.this.books);
                    }
                });
            }
        });

        UserRepository.getInstance().getUserById(FirebaseAuth.getInstance().getUid()).addOnCompleteListener(new OnCompleteListener<User>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<User> task) {
                currentUser = task.getResult();

                if (currentUser.getFollowing().contains(uid)) {
                    followed = true;
                    binding.profileFollowBtn.setText("Followed");
                }
            }
        });

//        if (user != null) initView();

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.navController.navigateUp();
            }
        });

        binding.changeProfilePicture.setOnClickListener(v -> {
            openFileChooser();
        });

        binding.profileChatBtn.setOnClickListener(v -> {
            Bundle bundle = new Bundle();

            List<String> userId = new ArrayList<>();
            userId.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
            userId.add(user.getId());

            MessageRepository.getInstance().getChatByUserIds(FirebaseAuth.getInstance().getCurrentUser().getUid(), user.getId()).addOnCompleteListener(task -> {
                if (task.getResult()!= null) {
                    bundle.putSerializable("CHAT_OBJECT", task.getResult());
                    Log.d("Adapter", task.getResult().toString());
                    bundle.putSerializable("CHAT_RECIPIENT", user);
                    activity.navController.navigate(R.id.chatFragment, bundle);
                    return;
                }
                MessageRepository.getInstance().createNewChat(userId).addOnCompleteListener(new OnCompleteListener<Chat>() {
                    @Override
                    public void onComplete(@NonNull Task<Chat> task) {
                        bundle.putSerializable("CHAT_OBJECT", task.getResult());
                        Log.d("AdapterFOund", task.getResult().toString());
                        bundle.putSerializable("CHAT_RECIPIENT", user);
                        activity.navController.navigate(R.id.chatFragment, bundle);
                    }
                });
            });
        });

        binding.profileFollowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFollowStatus();
                updateFollowButtonState();
            }
        });

        storageReference = FirebaseStorage.getInstance().getReference("profile_pictures");

        // set up books list
        bookAdapter = new SearchBookAdapter(UserProfileFragment.this, activity, books);
        binding.userBooksList.setAdapter(bookAdapter);
        binding.userBooksList.setLayoutManager(new LinearLayoutManager(activity));

        return binding.getRoot();
    }

    private void toggleFollowStatus() {
        followed = ! followed;

        if (followed) {
            UserRepository.getInstance().addToFollow(currentUser.getId(), user.getId());
        } else {
            UserRepository.getInstance().removeFromFollow(currentUser.getId(), user.getId());
        }
    }
    @SuppressLint("SetTextI18n")
    private void updateFollowButtonState() {
        if (followed) {
            binding.profileFollowBtn.setText("Followed");
        } else {
            binding.profileFollowBtn.setText("Follow");
        }
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

            // Load the original bitmap
            Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);

            // Resize the bitmap
            int maxWidth = 800; // Set your desired maximum width
            Bitmap resizedBitmap = resizeBitmap(originalBitmap, maxWidth);

            // Rotate the resized bitmap if needed
            Bitmap rotatedBitmap = rotateBitmap(resizedBitmap, orientation);

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

    private Bitmap resizeBitmap(Bitmap originalBitmap, int maxWidth) {
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();

        float aspectRatio = (float) width / height;

        int newWidth = maxWidth;
        int newHeight = (int) (maxWidth / aspectRatio);

        return Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);
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

    @Override
    public void onSiteClick(Bundle bundle) {
        Navigation.findNavController(getView()).navigate(R.id.bookDetailFragment, bundle);
    }
}