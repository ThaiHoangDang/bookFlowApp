package com.rmit.bookflowapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rmit.bookflowapp.Model.Chat;
import com.rmit.bookflowapp.Model.User;
import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.databinding.ItemMessageLeftBinding;
import com.rmit.bookflowapp.databinding.ItemMessageRightBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder>{
    public static  final int MSG_TYPE_LEFT = 0;
    public static  final int MSG_TYPE_RIGHT = 1;
    private Context context;
    private List<Chat.Message> messages;
    private FirebaseUser currentUser;
    private User recipient;

    public ChatMessageAdapter(Context context, List<Chat.Message> messages, User recipient) {
        this.context = context;
        this.messages = messages;
        this.recipient = recipient;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            ItemMessageRightBinding binding = ItemMessageRightBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding.getRoot());
        } else {
            ItemMessageLeftBinding binding = ItemMessageLeftBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding.getRoot());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat.Message message = messages.get(position);
        holder.show_message.setText(message.getMessage());

        if (position == messages.size()-1 && (getItemViewType(position) == MSG_TYPE_RIGHT)){
            holder.txt_seen.setVisibility(View.VISIBLE);
            if (message.isRead()){
                holder.txt_seen.setText("Seen");
            } else {
                holder.txt_seen.setText("Delivered");
            }
        } else {
            holder.txt_seen.setVisibility(View.GONE);
        }
        if (position > 0){
            if ((getItemViewType(position) == MSG_TYPE_LEFT) && (getItemViewType(position-1) == MSG_TYPE_LEFT)){
                holder.profile_image.setVisibility(View.INVISIBLE);
            } else {
                holder.profile_image.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (messages.get(position).getSender().equals(currentUser.getUid())){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message;
        public ImageView profile_image;
        public TextView txt_seen;

        public ViewHolder(View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.txt_seen);
        }
    }
}
