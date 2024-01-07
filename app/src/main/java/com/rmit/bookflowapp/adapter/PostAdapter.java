package com.rmit.bookflowapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rmit.bookflowapp.Model.Lend;
import com.rmit.bookflowapp.Model.Post;
import com.rmit.bookflowapp.Model.Review;
import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.Ultilities.Helper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Post> posts;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView postOwner, cardPostTitle, postInfo, postContent, postDate;
        private RatingBar rating;
        private Button postLikeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postOwner = itemView.findViewById(R.id.postOwner);
            cardPostTitle = itemView.findViewById(R.id.cardPostTitle);
            postInfo = itemView.findViewById(R.id.postInfo);
            rating = itemView.findViewById(R.id.rating);
            postContent = itemView.findViewById(R.id.postContent);
            postDate = itemView.findViewById(R.id.postDate);
//            postLikeButton = itemView.findViewById(R.id.postLikeBtn);
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
//        Post post = posts.get(position);

        // check post type is either Review or Lend and typecast to appropriate type
        if (posts.get(position) instanceof Review) {
            Review post = (Review) posts.get(position);

            // text
            holder.postOwner.setText(post.getUser().getName());
            holder.cardPostTitle.setText(post.getTitle());
            holder.postContent.setText(post.getContent());
            holder.postDate.setText(Helper.convertTime(post.getTimestamp()));

            // stars
            holder.rating.setVisibility(View.VISIBLE);
            holder.postInfo.setVisibility(View.GONE);
            holder.rating.setRating(post.getRating());

            // image
            ImageView postImage = holder.itemView.findViewById(R.id.imageView4);
            Picasso.get().load(post.getBook().getImageUrl()).into(postImage);
        } else if (posts.get(position) instanceof Lend) {
            Lend post = (Lend) posts.get(position);

            // text
            holder.postOwner.setText(post.getUser().getName());
            holder.cardPostTitle.setText(post.getTitle());
            holder.postContent.setText(post.getContent());
            holder.postDate.setText(Helper.convertTime(post.getTimestamp()));

            // location
            holder.rating.setVisibility(View.GONE);
            holder.postInfo.setVisibility(View.VISIBLE);
            holder.postInfo.setText(Helper.convertLatLng(context, post.getLocation()));

            // image
            ImageView postImage = holder.itemView.findViewById(R.id.imageView4);
            Picasso.get().load(post.getBook().getImageUrl()).into(postImage);
        }
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
