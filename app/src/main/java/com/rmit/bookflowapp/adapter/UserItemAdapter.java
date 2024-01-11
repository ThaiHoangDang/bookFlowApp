package com.rmit.bookflowapp.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.rmit.bookflowapp.Model.Chat;
import com.rmit.bookflowapp.Model.User;
import com.rmit.bookflowapp.databinding.ItemChatBinding;
import com.rmit.bookflowapp.interfaces.ClickCallback;
import com.rmit.bookflowapp.repository.MessageRepository;

import java.util.ArrayList;
import java.util.List;

public class UserItemAdapter  extends RecyclerView.Adapter<UserItemAdapter.ViewHolder>{

    private Context context;
    private List<User> userList;
    private ClickCallback clickCallback;

    public UserItemAdapter(Context context, List<User> userList, ClickCallback clickCallback) {
        this.context = context;
        this.userList = userList;
        this.clickCallback = clickCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChatBinding view = ItemChatBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.item.usernameTextView.setVisibility(View.VISIBLE);
        holder.item.usernameTextView.setText(user.getName());
        holder.item.messageTextView.setVisibility(View.GONE);
        holder.item.timestampTextView.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemChatBinding item;
        ViewHolder(ItemChatBinding item) {
            super(item.getRoot());
            this.item = item;
            itemView.setOnClickListener(v -> onClick());
        }
        public void onClick() {
            Bundle bundle = new Bundle();
            User user = userList.get(getAdapterPosition());
            List<String> userId = new ArrayList<>();
            userId.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
            userId.add(user.getId());
            MessageRepository.getInstance().getChatByUserIds(FirebaseAuth.getInstance().getCurrentUser().getUid(), user.getId()).addOnCompleteListener(new OnCompleteListener<Chat>() {
                @Override
                public void onComplete(@NonNull Task<Chat> task) {
                    if (task.getResult()!= null) {
                        bundle.putSerializable("CHAT_OBJECT", task.getResult());
                        Log.d("Adapter", task.getResult().toString());
                        bundle.putSerializable("CHAT_RECIPIENT", user);
                        clickCallback.onUserClick(bundle);
                        return;
                    }
                    MessageRepository.getInstance().createNewChat(userId).addOnCompleteListener(new OnCompleteListener<Chat>() {
                        @Override
                        public void onComplete(@NonNull Task<Chat> task) {
                            bundle.putSerializable("CHAT_OBJECT", task.getResult());
                            Log.d("AdapterFOund", task.getResult().toString());
                            bundle.putSerializable("CHAT_RECIPIENT", user);
                            clickCallback.onUserClick(bundle);
                        }
                    });
                }
            });
        }
    }
}
