package com.rmit.bookflowapp.viewmodel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.rmit.bookflowapp.viewmodel.BookDetailViewModel;
import com.rmit.bookflowapp.viewmodel.PostDetailViewModel;

public class PostDetailViewModelFactory implements ViewModelProvider.Factory {
    private final String postId;

    public PostDetailViewModelFactory(String postId) {
        this.postId = postId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PostDetailViewModel.class)) {
            //noinspection unchecked
            return (T) new PostDetailViewModel(postId);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
