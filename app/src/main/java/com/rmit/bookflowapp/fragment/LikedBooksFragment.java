package com.rmit.bookflowapp.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.rmit.bookflowapp.Model.Book;
import com.rmit.bookflowapp.Model.User;
import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.activity.MainActivity;
import com.rmit.bookflowapp.adapter.SearchBookAdapter;
import com.rmit.bookflowapp.databinding.FragmentLikedBooksBinding;
import com.rmit.bookflowapp.interfaces.ClickCallback;
import com.rmit.bookflowapp.repository.BookRepository;
import com.rmit.bookflowapp.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;


public class LikedBooksFragment extends Fragment implements ClickCallback {
    private static final String TAG = "LikedBooksFragment";
    private FragmentLikedBooksBinding binding;
    private MainActivity activity;
    private SearchBookAdapter bookAdapter;
    private String uid;
    private User user;
    private ArrayList<Book> books = new ArrayList<>();

    public LikedBooksFragment() {
        // Required empty public constructor
    }

    public LikedBooksFragment(String uid) {
        // Required empty public constructor
        this.uid = uid;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLikedBooksBinding.inflate(inflater,container,false);
        activity = (MainActivity) getActivity();
        UserRepository.getInstance().getUserById(uid).addOnCompleteListener(new OnCompleteListener<User>() {
            @Override
            public void onComplete(@NonNull Task<User> task) {
                user = task.getResult();
                BookRepository.getInstance().getBooksByIds(user.getFavoriteBooks()).addOnSuccessListener(new OnSuccessListener<List<Book>>() {
                    @Override
                    public void onSuccess(List<Book> books) {
                        LikedBooksFragment.this.books = new ArrayList<>(books);

                        // Notify the adapter about the data change
                        if (bookAdapter != null) bookAdapter.setBooks(LikedBooksFragment.this.books);
                    }
                });
            }
        });
        bookAdapter = new SearchBookAdapter(this, activity, books);
        binding.userBooksList.setAdapter(bookAdapter);
        binding.userBooksList.setLayoutManager(new LinearLayoutManager(activity));
        return binding.getRoot();
    }
    @Override
    public void onSiteClick(Bundle bundle) {
        Navigation.findNavController(getView()).navigate(R.id.bookDetailFragment, bundle);
    }
}