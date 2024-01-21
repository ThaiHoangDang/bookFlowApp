package com.rmit.bookflowapp.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.rmit.bookflowapp.Model.Chat;
import com.rmit.bookflowapp.Model.User;
import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.activity.MainActivity;
import com.rmit.bookflowapp.adapter.ChatAdapter;
import com.rmit.bookflowapp.adapter.UserItemAdapter;
import com.rmit.bookflowapp.databinding.FragmentMessageListBinding;
import com.rmit.bookflowapp.databinding.FragmentMessageSearchBinding;
import com.rmit.bookflowapp.interfaces.ClickCallback;
import com.rmit.bookflowapp.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MessageSearchFragment extends Fragment implements ClickCallback {
    private String TAG = "MessageList";
    private FragmentMessageSearchBinding binding;
    private MainActivity activity;
    private UserItemAdapter adapter;
    private FirebaseFirestore firestore;
    private CollectionReference messagesCollection;
    private FirebaseUser currentUser;
    private List<User> userList;
    private List<User> recipientList;
    private UserRepository userRepository;

    public MessageSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMessageSearchBinding.inflate(inflater, container, false);
        activity = (MainActivity) getActivity();
        activity.setBottomNavigationBarVisibility(false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userRepository = UserRepository.getInstance();
        firestore = FirebaseFirestore.getInstance();
        messagesCollection = firestore.collection("messages");

        binding.userPreviewRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.userPreviewRecyclerView.setHasFixedSize(true);

        recipientList = new ArrayList<>();
        userList = new ArrayList<>();
        adapter = new UserItemAdapter(getContext(), recipientList, this);
        binding.userPreviewRecyclerView.setAdapter(adapter);

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.navController.navigateUp();
            }
        });

        loadUser();
        return binding.getRoot();
    }

    public void loadUser(){
        userRepository.getAllUsers().addOnCompleteListener(task -> {
            QuerySnapshot querySnapshots = task.getResult();
            for (DocumentSnapshot dc : querySnapshots.getDocuments()){
                if (!dc.getId().equals(currentUser.getUid())) {
                    User u = dc.toObject(User.class);
                    u.setId(dc.getId());
                    userList.add(u);
                }
            }
        });
        binding.filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = binding.searchView.getQuery().toString().toString();
                if (!query.isEmpty()){
                    recipientList.clear();
                    recipientList.addAll(userList.stream().filter(user -> user.getName().toLowerCase().contains(query.toLowerCase()) || user.getEmail().toLowerCase().contains(query.toLowerCase())).collect(Collectors.toList()));
                    adapter.notifyDataSetChanged();
                }
            }
        });

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()){
                    recipientList.clear();
                    recipientList.addAll(userList.stream().filter(user -> user.getName().toLowerCase().contains(query.trim().toLowerCase()) || user.getEmail().toLowerCase().contains(query.trim().toLowerCase())).collect(Collectors.toList()));
                    adapter.notifyDataSetChanged();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()){
                    recipientList.clear();
                    recipientList.addAll(userList.stream().filter(user -> user.getName().toLowerCase().contains(newText.trim().toLowerCase()) || user.getEmail().toLowerCase().contains(newText.trim().toLowerCase())).collect(Collectors.toList()));
                    adapter.notifyDataSetChanged();
                }
                return false;
            }
        });
    }

    @Override
    public void onUserClick(Bundle bundle) {
        activity.navController.popBackStack();
//        activity.navController.navigate(R.id.chatFragment, bundle);
        activity.navController.navigate(R.id.userProfileFragment, bundle);
    }
}