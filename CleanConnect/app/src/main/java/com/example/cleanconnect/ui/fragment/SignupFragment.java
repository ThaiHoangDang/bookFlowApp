package com.example.cleanconnect.ui.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.navigation.Navigation;

import com.example.cleanconnect.R;
import com.example.cleanconnect.databinding.FragmentLoginBinding;
import com.example.cleanconnect.databinding.FragmentSignupBinding;
import com.example.cleanconnect.model.User;
import com.example.cleanconnect.ui.activity.MainActivity;
import com.example.cleanconnect.util.ImageLoaderUtils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URL;
import java.util.Objects;


public class SignupFragment extends Fragment {

    private Uri imageUri;
    private ImageLoaderUtils loaderUtils;
    private MainActivity activity;
    private FragmentSignupBinding bind;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 1;

    public SignupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        loaderUtils = new ImageLoaderUtils();
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        bind = FragmentSignupBinding.inflate(inflater, container, false);
        View view = bind.getRoot();
        initToolbar();

        // Set click listeners
        bind.uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        bind.signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle signup button click
                // Retrieve user input from EditText fields
                String name = bind.nameEditText.getText().toString();
                String email = bind.emailEditText.getText().toString();
                String phone = bind.phoneEditText.getText().toString();
                String password = bind.passwordEditText.getText().toString();

                activity.setProgressVisibility(true);
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        FirebaseUser user = task.getResult().getUser();
                        if (user != null){
                            StorageReference imageRef = storageReference.child("images/" + user.getUid());
                            UploadTask uploadTask = imageRef.putFile(imageUri);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(activity,exception.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()){
                                                firestore.collection("users").document(user.getUid()).set(new User(user.getUid(),name, email, phone, task.getResult().toString(), "USER"))
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                activity.setProgressVisibility(false);
                                                                Toast.makeText(activity,"Created",Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
        });
        return view;
    }
    private void initToolbar() {
        activity.setSupportActionBar(bind.toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        bind.toolbar.setNavigationOnClickListener(v -> {
            activity.navController.navigateUp();
        });
        Objects.requireNonNull(bind.toolbar.getOverflowIcon()).setTint(requireContext().getResources().getColor(R.color.titleTextColor, null));

    }

    private void openImagePicker() {
        // Open image picker
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the selected image URI
            imageUri = data.getData();
            loaderUtils.loadImage(activity, bind.profileImageView, imageUri);
        }
    }
}
