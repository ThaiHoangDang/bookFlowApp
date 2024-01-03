package com.rmit.bookflowapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.databinding.ActivityMainBinding;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public ActivityMainBinding bind;
    private NavHostFragment navHostFragment;
    private FragmentManager fragmentManager;
    public NavController navController;
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bind = ActivityMainBinding.inflate(getLayoutInflater());
        View view = bind.getRoot();
        setContentView(view);

        firebaseAuth = FirebaseAuth.getInstance();
//        firebaseAuth.signOut();

        fragmentManager = getSupportFragmentManager();
        initNavigation();

    }

    private void initNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        navHostFragment = (NavHostFragment) fragmentManager.findFragmentById(R.id.nav_host_fragment);
        navController = Objects.requireNonNull(navHostFragment).getNavController();

        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    public void setBottomNavigationBarVisibility(boolean visibility) {
        if (visibility) {
            bottomNavigationView.setVisibility(View.VISIBLE);
        } else {
            bottomNavigationView.setVisibility(View.GONE);
        }
    }

    public void setProgressVisibility(boolean status){
        bind.overlay.setVisibility(status ? View.VISIBLE : View.GONE);
    }
}