package com.example.cleanconnect.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cleanconnect.model.User;
import com.example.cleanconnect.repository.UserRepository;

public class UserProfileViewModel extends ViewModel {
    private UserRepository userRepository;

    private MutableLiveData<User> userProfile = new MutableLiveData<>();
    private final String uid;

    public UserProfileViewModel(String uid){
        this.userRepository = new UserRepository();
        this.uid = uid;
        loadUser();
    }
    public LiveData<User> getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(User user) {
        userProfile.setValue(user);
    }

    private void loadUser(){
        userRepository.getUser(uid).addOnSuccessListener(user -> {
            userProfile.setValue(user);
        });
    }
}
