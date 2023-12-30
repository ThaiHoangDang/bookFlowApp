package com.rmit.bookflowapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rmit.bookflowapp.Model.Post;
import com.rmit.bookflowapp.R;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Post> posts;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView postOwner, postInfo, postContent, postDate;
        private Button postLikeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postOwner = itemView.findViewById(R.id.postOwner);
            postInfo = itemView.findViewById(R.id.postInfo);
            postContent = itemView.findViewById(R.id.postContent);
            postDate = itemView.findViewById(R.id.postDate);
            postLikeButton = itemView.findViewById(R.id.postLikeBtn);
        }
    }

    public PostAdapter(Context context, ArrayList<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View heroView = inflater.inflate(R.layout.post_card, parent, false);
        return new PostAdapter.ViewHolder(heroView);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
        Post post = posts.get(position);

        holder.postOwner.setText(post.getUserId());
        holder.postInfo.setText("Wrote a review for Truyen Kieu");
        holder.postContent.setText(post.getContent());
        holder.postDate.setText("31/12/2023");
        holder.postLikeButton.setText("32");
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFilteredList(ArrayList<Post> filteredList) {
        this.posts = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
