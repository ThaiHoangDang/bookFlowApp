package com.rmit.bookflowapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.rmit.bookflowapp.Model.Book;
import com.rmit.bookflowapp.Model.User;
import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.activity.MainActivity;
import com.rmit.bookflowapp.adapter.SearchBookAdapter;
import com.rmit.bookflowapp.adapter.UserItemAdapter;
import com.rmit.bookflowapp.databinding.FragmentFollowingBinding;
import com.rmit.bookflowapp.databinding.FragmentLikedBooksBinding;
import com.rmit.bookflowapp.interfaces.ClickCallback;
import com.rmit.bookflowapp.repository.BookRepository;
import com.rmit.bookflowapp.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;


public class FollowingFragment extends Fragment implements ClickCallback {
    private static final String TAG = "LikedBooksFragment";
    private FragmentFollowingBinding binding;
    private MainActivity activity;
    private UserItemAdapter itemAdapter;
    private String uid;
    private User user;
    private ArrayList<User> followingUsers = new ArrayList<>();

    public FollowingFragment() {
        // Required empty public constructor
    }

    public FollowingFragment(String uid) {
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
        binding = FragmentFollowingBinding.inflate(inflater,container,false);
        activity = (MainActivity) getActivity();
        UserRepository.getInstance().getUserById(uid).addOnCompleteListener(new OnCompleteListener<User>() {
            @Override
            public void onComplete(@NonNull Task<User> task) {
                user = task.getResult();
                    UserRepository.getInstance().getUsersByIds(user.getFollowing()).addOnCompleteListener(new OnCompleteListener<List<User>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<User>> task) {
                            itemAdapter.setUsers(task.getResult());
                        }
                    });
            }
        });
        itemAdapter = new UserItemAdapter(getContext(), followingUsers, this);
        binding.userBooksList.setAdapter(itemAdapter);
        binding.userBooksList.setLayoutManager(new LinearLayoutManager(activity));
        return binding.getRoot();
    }
    @Override
    public void onUserClick(Bundle bundle) {
        Navigation.findNavController(getView()).navigate(R.id.userProfileFragment, bundle);
    }
}