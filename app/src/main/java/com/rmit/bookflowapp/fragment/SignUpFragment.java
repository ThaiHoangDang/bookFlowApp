package com.rmit.bookflowapp.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rmit.bookflowapp.Model.User;
import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.activity.MainActivity;
import com.rmit.bookflowapp.databinding.FragmentSignUpBinding;
import com.rmit.bookflowapp.repository.UserRepository;

import java.util.Objects;


public class SignUpFragment extends Fragment {
    private MainActivity activity;
    private FragmentSignUpBinding bind;
    private FirebaseAuth mAuth;

    public SignUpFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        bind = FragmentSignUpBinding.inflate(inflater, container, false);
        View view = bind.getRoot();

        bind.toAuthentication.setOnClickListener(v -> activity.navController.navigate(R.id.authenticationFragment));

        bind.signUpButton.setOnClickListener(v -> signUp());
        return view;
    }

    private void signUp() {
        String name = Objects.requireNonNull(bind.nameInput.getText()).toString().trim();
        String email = Objects.requireNonNull(bind.emailInput.getText()).toString().trim();
        String password = Objects.requireNonNull(bind.passwordInput.getText()).toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(requireActivity(), "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(requireActivity(), "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(requireActivity(), "Please enter your name", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        try {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                String userId = firebaseUser.getUid();
                                User newUser = new User();
                                newUser.setId(userId);
                                newUser.setName(name);
                                newUser.setEmail(email);
                                newUser.setRole(User.Role.USER);

                                UserRepository.getInstance().addUser(userId, newUser)
                                        .addOnCompleteListener(createUserTask -> {
                                            if (createUserTask.isSuccessful()) {
                                                Toast.makeText(requireActivity(), "Sign up successfully", Toast.LENGTH_SHORT).show();
                                                mAuth.signOut();
                                                activity.navController.navigate(R.id.authenticationFragment);
                                            } else {
                                                Log.e("CREATE", "Error creating user: " + createUserTask.getException());
                                            }
                                        });
                            }
                        } catch (Exception e) {
                            Log.e("CREATE", "Error: " + e.getMessage());
                        }

                    } else {
                        Log.e("CREATE", "Error creating user: " + task.getException().getMessage());
                    }
                });
    }
}