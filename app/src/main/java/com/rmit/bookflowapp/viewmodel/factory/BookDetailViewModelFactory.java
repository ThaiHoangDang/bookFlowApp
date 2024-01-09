package com.rmit.bookflowapp.viewmodel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.rmit.bookflowapp.viewmodel.BookDetailViewModel;

public class BookDetailViewModelFactory implements ViewModelProvider.Factory {
    private final String bookId;

    public BookDetailViewModelFactory(String bookId) {
        this.bookId = bookId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(BookDetailViewModel.class)) {
            //noinspection unchecked
            return (T) new BookDetailViewModel(bookId);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
