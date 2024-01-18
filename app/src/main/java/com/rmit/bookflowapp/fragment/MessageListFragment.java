package com.rmit.bookflowapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rmit.bookflowapp.Model.Chat;
import com.rmit.bookflowapp.Model.User;
import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.activity.MainActivity;
import com.rmit.bookflowapp.adapter.ChatAdapter;
import com.rmit.bookflowapp.databinding.FragmentMessageListBinding;
import com.rmit.bookflowapp.interfaces.ClickCallback;
import com.rmit.bookflowapp.repository.MessageRepository;
import com.rmit.bookflowapp.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MessageListFragment extends Fragment implements ClickCallback {

    private String TAG = "MessageList";
    private FragmentMessageListBinding binding;
    private MainActivity activity;
    private List<Chat> chats;
    private ChatAdapter adapter;
    private FirebaseFirestore firestore;
    private CollectionReference messagesCollection;
    private FirebaseUser currentUser;
    private List<User> recipientList;
    private UserRepository userRepository;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMessageListBinding.inflate(inflater, container, false);
        activity = (MainActivity) getActivity();
        activity.setBottomNavigationBarVisibility(true);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userRepository = UserRepository.getInstance();
        firestore = FirebaseFirestore.getInstance();
        messagesCollection = firestore.collection("messages");

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setHasFixedSize(true);

        chats = new ArrayList<>();
        recipientList = new ArrayList<>();
        adapter = new ChatAdapter(getContext(), chats, recipientList, this);
        binding.recyclerView.setAdapter(adapter);

        binding.createPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.navController.navigate(R.id.messageSearchFragment);
            }
        });

        binding.supportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                UserRepository.getInstance().getUserById("1JRBTnFxv8T08S5xpeHEodWFICI3").addOnCompleteListener(new OnCompleteListener<User>() {
                    @Override
                    public void onComplete(@NonNull Task<User> task) {
                        User user = task.getResult();
                        List<String> userId = new ArrayList<>();
                        userId.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        userId.add(user.getId());
                        MessageRepository.getInstance().getChatByUserIds(FirebaseAuth.getInstance().getCurrentUser().getUid(), user.getId()).addOnCompleteListener(new OnCompleteListener<Chat>() {
                            @Override
                            public void onComplete(@NonNull Task<Chat> task) {
                                if (task.getResult()!= null) {
                                    bundle.putSerializable("CHAT_OBJECT", task.getResult());
                                    bundle.putSerializable("CHAT_RECIPIENT", user);
                                    activity.navController.navigate(R.id.chatFragment, bundle);
                                    return;
                                }
                                MessageRepository.getInstance().createNewChat(userId).addOnCompleteListener(new OnCompleteListener<Chat>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Chat> task) {
                                        bundle.putSerializable("CHAT_OBJECT", task.getResult());
                                        bundle.putSerializable("CHAT_RECIPIENT", user);
                                        activity.navController.navigate(R.id.chatFragment, bundle);                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
        loadMessages();
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void loadMessages() {
        if (currentUser != null) {
            messagesCollection.whereArrayContains("userId", currentUser.getUid())
                    .orderBy("lastModified")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Chat chat = document.toObject(Chat.class);
                                    chat.setChatId(document.getId());
                                    String recipientId = chat.getUserId().get(chat.getUserId().indexOf(currentUser.getUid())==0?1:0);
                                        chats.add(chat);
                                        Collections.sort(chats);

                                    if (recipientList.stream().noneMatch(user -> user.getId().equals(recipientId))){
                                        userRepository.getUserById(recipientId).addOnSuccessListener(new OnSuccessListener<User>() {
                                            @Override
                                            public void onSuccess(User user) {
                                                recipientList.add(user);
                                                adapter.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                }
                                listenToChanges();
                            }
                            else {
                                Log.d(TAG, task.getException().getMessage());
                            }
                        }
                    });
        }
    }

    private void listenToChanges(){
        messagesCollection.whereArrayContains("userId", currentUser.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    // Handle errors
                    return;
                }

                for (DocumentChange dc : value.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            Chat addedChat = dc.getDocument().toObject(Chat.class);
                            addedChat.setChatId(dc.getDocument().getId());
                            if (!chats.stream().anyMatch(chat -> compareStringLists(addedChat.getUserId(), chat.getUserId()))){
                                chats.add(addedChat);
                            }
                            break;
                        case MODIFIED:
                            Chat changedChat = dc.getDocument().toObject(Chat.class);
                            changedChat.setChatId(dc.getDocument().getId());
                            chats.removeIf(chat -> compareStringLists(changedChat.getUserId(), chat.getUserId()));
                            chats.add(changedChat);
                            break;
                        case REMOVED:
                            Chat removedChat = dc.getDocument().toObject(Chat.class);
                            removedChat.setChatId(dc.getDocument().getId());
                            chats.removeIf(chat -> compareStringLists(removedChat.getUserId(), chat.getUserId()));
                            break;
                    }
                    Collections.sort(chats);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onChatClick(Bundle bundle) {
        activity.navController.navigate(R.id.chatFragment, bundle);
        activity.setBottomNavigationBarVisibility(false);
    }

    private boolean compareStringLists(List<String> l1, List<String> l2){
        List<String> sortedList1 = new ArrayList<>(l1);
        List<String> sortedList2 = new ArrayList<>(l2);
        Collections.sort(sortedList1);
        Collections.sort(sortedList2);
        return sortedList1.equals(sortedList2);
    }
}