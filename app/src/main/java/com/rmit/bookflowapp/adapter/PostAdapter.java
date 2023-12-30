package com.rmit.bookflowapp.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView postOwner, postDate, siteLocation;
        private RelativeLayout siteStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            siteName = itemView.findViewById(R.id.mySiteNameCard);
//            siteDate = itemView.findViewById(R.id.mySiteDateCard);
//            siteLocation = itemView.findViewById(R.id.mySiteLocationCard);
//            siteStatus = itemView.findViewById(R.id.siteStatusCard);
        }
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
