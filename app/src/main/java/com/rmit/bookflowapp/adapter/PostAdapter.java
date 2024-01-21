package com.rmit.bookflowapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.rmit.bookflowapp.Model.Lend;
import com.rmit.bookflowapp.Model.Post;
import com.rmit.bookflowapp.Model.Review;
import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.Ultilities.Helper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> implements Filterable {
    private Context context;
    private ArrayList<Post> posts;
    private ArrayList<Post> fullPosts;

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
        this.fullPosts = new ArrayList<>(posts);
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
            Picasso.get().load(post.getUser().getImageId()).into((ImageView) holder.itemView.findViewById(R.id.postAvatarImage));

            //verified
            if (post.getUser().isVerified()) {
                holder.postOwner.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_verified_24, 0);
                holder.postOwner.setCompoundDrawablePadding(20);
            } else {
                holder.postOwner.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                holder.postOwner.setCompoundDrawablePadding(0);
            }
            // stars
            holder.rating.setVisibility(View.VISIBLE);
            holder.postInfo.setVisibility(View.GONE);
            holder.rating.setRating(post.getRating());

            // image
            ImageView postImage = holder.itemView.findViewById(R.id.imageView4);
            Picasso.get().load(post.getBook().getImageUrl()).into(postImage);

            // click listener
            holder.itemView.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putSerializable("POST_OBJECT", post);
                Navigation.findNavController(v).navigate(R.id.postDetailFragment, bundle);
            });
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

    @Override
    public int getItemCount() {
        return posts.size();
    }

    // from implementing Filterable
    @Override
    public Filter getFilter() {
        return postsFilter;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFilteredList(ArrayList<Post> filteredList) {
        this.posts = filteredList;
        notifyDataSetChanged();
    }

    private Filter postsFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Post> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(fullPosts); // show all posts if no search query
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim(); // clean up search query

                for (Post post : fullPosts) {
                    if (post instanceof Review) {
                        Review review = (Review) post;
                        if (review.getTitle().toLowerCase().contains(filterPattern) || review.getContent().toLowerCase().contains(filterPattern)) {
                            filteredList.add(review); // if search query matches title or content, add to filtered list
                        }
                    } else if (post instanceof Lend) {
                        Lend lend = (Lend) post;
                        if (lend.getTitle().toLowerCase().contains(filterPattern) || lend.getContent().toLowerCase().contains(filterPattern)) {
                            filteredList.add(lend); // if search query matches title or content, add to filtered list
                        }
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            posts.clear();
            posts.addAll((ArrayList) filterResults.values);
            notifyDataSetChanged();
        }
    };
}
