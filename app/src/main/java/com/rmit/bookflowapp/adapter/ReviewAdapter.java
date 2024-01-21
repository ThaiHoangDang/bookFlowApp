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

import com.google.rpc.Help;
import com.rmit.bookflowapp.Model.Review;
import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.Ultilities.Helper;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
        private Context context;
        private ArrayList<Review> reviews;

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView reviewOwner, reviewContent, reviewTitle, reviewDate;
            private RatingBar reviewRating;
            private ImageView reviewOwnerAvatar;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                reviewOwnerAvatar = itemView.findViewById(R.id.cardReviewOwnerAvatar);
                reviewOwner = itemView.findViewById(R.id.cardReviewOwner);
                reviewRating = itemView.findViewById(R.id.cardReviewRating);
                reviewTitle = itemView.findViewById(R.id.cardReviewTitle);
                reviewContent = itemView.findViewById(R.id.cardReviewContent);
                reviewDate = itemView.findViewById(R.id.cardReviewDate);
            }
        }

        public ReviewAdapter(Context context, ArrayList<Review> reviews) {
            this.context = context;
            this.reviews = reviews;
        }

        @NonNull
        @Override
        public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View itemView = inflater.inflate(R.layout.review_card, parent, false);
            return new ReviewAdapter.ViewHolder(itemView);
        }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviews.get(position);
        //verified
        if (review.getUser().isVerified()) {
            holder.reviewOwner.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_verified_24, 0);
            holder.reviewOwner.setCompoundDrawablePadding(20);
        } else {
            holder.reviewOwner.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            holder.reviewOwner.setCompoundDrawablePadding(0);
        }
        //holder.reviewOwnerAvatar
        holder.reviewOwner.setText(review.getUser().getName());
        holder.reviewRating.setRating(review.getRating());
        holder.reviewTitle.setText(review.getTitle());
        holder.reviewContent.setText(review.getContent());
        holder.reviewDate.setText(Helper.convertTime(review.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<Review> reviews) {
        this.reviews.clear();
        this.reviews.addAll(reviews);
        notifyDataSetChanged();
    }
}
