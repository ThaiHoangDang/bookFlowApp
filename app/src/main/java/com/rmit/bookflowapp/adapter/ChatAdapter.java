package com.rmit.bookflowapp.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.rmit.bookflowapp.Model.Chat;
import com.rmit.bookflowapp.Model.User;
import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.databinding.ItemChatBinding;
import com.rmit.bookflowapp.interfaces.ClickCallback;
import com.rmit.bookflowapp.util.TimeFormatter;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Optional;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private Context context;
    private List<Chat> chatList;
    private List<User> userList;
    private ClickCallback clickCallback;

    public ChatAdapter(Context context, List<Chat> chatList, List<User> userList, ClickCallback clickCallback) {
        this.context = context;
        this.chatList = chatList;
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
        // Get the current Chat and User objects
        Chat chat = chatList.get(position);
        Optional<User> user = userList.stream().filter(u -> chat.getUserId().contains(u.getId())).findFirst();
        if (user.isPresent()) {
            holder.item.usernameTextView.setText(user.get().getName());
            if (!user.get().getImageId().isEmpty()) {
                Picasso.get().load(user.get().getImageId()).into((ImageView) holder.item.userImageView);
            }
            // Display the first message in the messages list (customize as needed)
            if (chat.getMessages() != null && !chat.getMessages().isEmpty()) {
                Chat.Message firstMessage = chat.getMessages().get(chat.getMessages().size()-1);
                holder.item.timestampTextView.setText(TimeFormatter.formatTimeAgo(firstMessage.getTimestamp().toDate()));
                if (firstMessage.getSender().equals(user.get().getId())) {
                    if (!firstMessage.isRead()) {
                        holder.item.messageTextView.setTypeface(null, Typeface.BOLD);
                    }
                    holder.item.messageTextView.setText(firstMessage.getMessage());
                } else {
                    holder.item.messageTextView.setText("You: " + firstMessage.getMessage());
                }
            }
            if (user.get().isVerified()) {
                holder.item.usernameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_verified_24, 0);
                holder.item.usernameTextView.setCompoundDrawablePadding(20);
            } else {
                holder.item.usernameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                holder.item.usernameTextView.setCompoundDrawablePadding(0);
            }
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
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
            Chat chat = chatList.get(getAdapterPosition());
            Optional<User> user = userList.stream().filter(u -> chat.getUserId().contains(u.getId())).findFirst();
            bundle.putSerializable("CHAT_OBJECT", chat);
            bundle.putSerializable("CHAT_RECIPIENT", user.get());
            clickCallback.onChatClick(bundle);
        }
    }
}


