package com.rmit.bookflowapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.rmit.bookflowapp.Model.User;
import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.activity.MainActivity;
import com.rmit.bookflowapp.repository.UserRepository;
import com.rmit.bookflowapp.databinding.FragmentAuthenticationBinding;

import java.util.Objects;

public class AuthenticationFragment extends Fragment {
    private static final int RC_SIGN_IN = 1;
    private MainActivity activity;
    private FragmentAuthenticationBinding bind;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    public AuthenticationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail()
                .build();
        mAuth = FirebaseAuth.getInstance();
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        bind = FragmentAuthenticationBinding.inflate(inflater, container, false);
        View view = bind.getRoot();
//        activity.setBottomNavigationBarVisibility(false);
        bind.googleSignInButton.setOnClickListener(v -> signInWithGoogle());
        return view;
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
            } catch (Exception e) {
                Log.d("AUTHENTICATION", "BUON");
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        mAuth.signInWithCredential(GoogleAuthProvider.getCredential(acct.getIdToken(), null))
                .addOnCompleteListener(requireActivity(), task -> {
                    try {
                        if (task.isSuccessful()) {
                            if (firebaseUser != null) {
                                UserRepository.getInstance().getUserById(firebaseUser.getUid())
                                        .addOnCompleteListener(getUserTask -> {
                                            if (getUserTask.isSuccessful()) {
                                                User currentUser = getUserTask.getResult();
                                                if (currentUser == null) {
                                                    User newUser = new User();
                                                    newUser.setName(firebaseUser.getDisplayName());
                                                    newUser.setEmail(firebaseUser.getEmail());
                                                    UserRepository.getInstance().addUser(firebaseUser.getUid(), newUser);
                                                }
                                                activity.navController.navigate(R.id.homeFragment);
                                            } else {
                                                Exception exception = getUserTask.getException();
                                                if (exception != null) {
                                                    Log.e("AUTHENTICATION", exception.getMessage(), exception);
                                                }
                                            }
                                        });
                            }
                        } else {
                            throw Objects.requireNonNull(task.getException());
                        }
                    } catch (Exception e) {
                        Toast.makeText(requireActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("AUTHENTICATION", "BUON", e);
                    }
                });
    }
}