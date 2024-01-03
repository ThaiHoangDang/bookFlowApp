package com.example.cleanconnect.viewmodel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.cleanconnect.viewmodel.UserProfileViewModel;

// UserProfileViewModelFactory.java
public class UserProfileViewModelFactory implements ViewModelProvider.Factory {
    private final String userId;

    public UserProfileViewModelFactory(String userId) {
        this.userId = userId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserProfileViewModel.class)) {
            //noinspection unchecked
            return (T) new UserProfileViewModel(userId);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

