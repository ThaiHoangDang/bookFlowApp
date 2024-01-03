package com.example.cleanconnect.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cleanconnect.R;
import com.example.cleanconnect.databinding.FragmentLoginBinding;
import com.example.cleanconnect.repository.UserRepository;
import com.example.cleanconnect.ui.activity.MainActivity;
import com.example.cleanconnect.util.ValidationUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import java.util.Objects;

public class LoginFragment extends Fragment {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button signupButton;
    private MainActivity activity;
    private FragmentLoginBinding bind;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        bind = FragmentLoginBinding.inflate(inflater, container, false);
        View view = bind.getRoot();
        activity.setBottomNavigationBarVisibility(false);

        emailEditText = bind.editTextEmail;
        passwordEditText = bind.editTextPassword;
        loginButton = bind.buttonLogin;
        signupButton = bind.buttonSignUp;

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateInput()){
                    return;
                }
                String email = Objects.requireNonNull(emailEditText.getText()).toString().trim();
                String password = Objects.requireNonNull(passwordEditText.getText()).toString().trim();
                UserRepository userRepository = new UserRepository();
                userRepository.signIn(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(activity, "Logged in successfully", Toast.LENGTH_SHORT).show();
                            activity.setUserWithId(task.getResult().getUser().getUid());
                            activity.navController.navigate(R.id.mapFragment);
                        } else {
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show();                        }
                    }
                });
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.navController.navigate(R.id.action_loginFragment_to_signupFragment);
            }
        });

        return view;
    }

    private void initToolbar() {
        activity.setSupportActionBar(bind.toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private boolean validateInput(){
        String email = Objects.requireNonNull(emailEditText.getText()).toString().trim();
        String password = Objects.requireNonNull(passwordEditText.getText()).toString().trim();
        if (!ValidationUtils.isValidEmail(email)) {
            emailEditText.setError("Invalid email address");
            Toast.makeText(activity, "Invalid email address", Toast.LENGTH_SHORT).show();
            return false;
        } else if(!ValidationUtils.isValidPassword(password)){
            passwordEditText.setError("Invalid password");
            Toast.makeText(activity, "Invalid password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}



