package com.rmit.bookflowapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rmit.bookflowapp.Model.Comment;
import com.rmit.bookflowapp.Model.Review;
import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.Ultilities.Helper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Comment> comments;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView commentOwner, commentContent, commentTime;
        private ImageView commentOwnerAvatar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            commentOwner = itemView.findViewById(R.id.commentOwner);
            commentContent = itemView.findViewById(R.id.commentContent);
            commentTime = itemView.findViewById(R.id.commentTime);
            commentOwnerAvatar = itemView.findViewById(R.id.commentAvatarImage);
        }
    }

    public CommentAdapter(Context context, ArrayList<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.comment_card, parent, false);
        return new CommentAdapter.ViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = comments.get(position);

        holder.commentOwner.setText(comment.getUser().getName());
        holder.commentContent.setText(comment.getContent());
        holder.commentTime.setText(Helper.convertTime(comment.getTimestamp()));
        Picasso.get().load(comment.getUser().getImageId()).into((ImageView) holder.itemView.findViewById(R.id.commentAvatarImage));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<Comment> comments) {

        // Sort the comments based on timestamp
        Collections.sort(comments, new Comparator<Comment>() {
            @Override
            public int compare(Comment comment1, Comment comment2) {
                // Sorting in descending order, change to ascending order if needed
                return comment2.getTimestamp().compareTo(comment1.getTimestamp());
            }
        });

        this.comments.clear();
        this.comments.addAll(comments);
        notifyDataSetChanged();
    }
}