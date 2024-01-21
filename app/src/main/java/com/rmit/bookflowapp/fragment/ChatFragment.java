package com.rmit.bookflowapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.rmit.bookflowapp.Model.Chat;
import com.rmit.bookflowapp.Model.User;
import com.rmit.bookflowapp.activity.CallActivity;
import com.rmit.bookflowapp.activity.MainActivity;
import com.rmit.bookflowapp.adapter.ChatMessageAdapter;
import com.rmit.bookflowapp.databinding.FragmentChatBinding;
import com.rmit.bookflowapp.repository.UserRepository;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    private String TAG = "Chat";
    private FragmentChatBinding binding;
    private MainActivity activity;
    private ChatMessageAdapter adapter;
    private Chat chat;
    private List<Chat.Message> messages;
    private FirebaseFirestore firestore;
    private CollectionReference messagesCollection;
    private FirebaseUser currentUser;
    private UserRepository userRepository;
    private User recipient;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        activity = (MainActivity) getActivity();
        activity.setBottomNavigationBarVisibility(false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        messagesCollection = firestore.collection("messages");

        LinearLayoutManager manager = new LinearLayoutManager(requireContext());
        manager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setHasFixedSize(true);
        initChatView();
        return binding.getRoot();
    }

    public void initChatView() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            chat = (Chat) arguments.getSerializable("CHAT_OBJECT");
            recipient = (User) arguments.getSerializable("CHAT_RECIPIENT");
            messages = chat.getMessages();
            markAsSeen();

            if (!recipient.getImageId().isEmpty()) {
                Picasso.get().load(recipient.getImageId()).into((ImageView) binding.avatarView);
            }

            binding.title.setText(recipient.getName());
            adapter = new ChatMessageAdapter(getContext(), messages, recipient);
            binding.recyclerView.setAdapter(adapter);

            binding.backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.navController.navigateUp();
                }
            });
            binding.textSend.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus && binding.recyclerView.getAdapter() != null && binding.recyclerView.getAdapter().getItemCount() > 0) {
                        // Scroll to the top position
                        LinearLayoutManager layoutManager = (LinearLayoutManager) binding.recyclerView.getLayoutManager();
                        layoutManager.scrollToPositionWithOffset(0, 0);
                    }
                }
            });
            binding.btnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (binding.textSend.getText().toString().isEmpty()) {
                        Toast.makeText(getContext(), "Cannot send empty message", Toast.LENGTH_SHORT);
                    } else {
                        String msg = binding.textSend.getText().toString();
                        sendMessage(msg);
                        binding.textSend.setText("");
                    }
                }
            });

            binding.video.setOnClickListener(v -> {
                UserRepository.getInstance()
                        .getUserById(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                User user = task.getResult();
                                Intent intent = new Intent(requireActivity(), CallActivity.class);
                                intent.putExtra("userID", chat.getUserId().get(0));
                                intent.putExtra("username", user.getName());
                                intent.putExtra("callID", chat.getChatId());
                                intent.putExtra("targetID", chat.getUserId().get(1));
                                intent.putExtra("targetName", "USER2");
                                startActivity(intent);
                            }
                        });

            });
            listenToChanges();
        } else {
            activity.navController.navigateUp();
            Toast.makeText(getContext(), "Failed to open chat", Toast.LENGTH_SHORT).show();
        }
    }

    private void markAsSeen() {
        if (!messages.isEmpty()) {
            if (messages.get(messages.size() - 1).getSender().equals(recipient.getId()) && !messages.get(messages.size() - 1).isRead()) {
                messages.get(messages.size() - 1).setRead(true);
                messagesCollection.document(chat.getChatId()).update("messages", messages);
            }
        }
    }

    private void sendMessage(String message) {
        List<Chat.Message> messages = new ArrayList<>(chat.getMessages());
        messages.add(new Chat.Message(currentUser.getUid(), message, Timestamp.now(), false));
        messagesCollection.document(chat.getChatId()).update("messages", messages, "lastModified", Timestamp.now());
    }

    private void listenToChanges() {
        messagesCollection.document(chat.getChatId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Chat newChat = value.toObject(Chat.class);
                chat.setMessages(newChat.getMessages());
                messages.clear();
                messages.addAll(newChat.getMessages());
                adapter.notifyDataSetChanged();
                binding.recyclerView.scrollToPosition(messages.size() - 1);
            }
        });
    }
}