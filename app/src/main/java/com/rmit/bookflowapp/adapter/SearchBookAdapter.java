package com.rmit.bookflowapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rmit.bookflowapp.Model.Book;
import com.rmit.bookflowapp.R;

import java.util.ArrayList;
import java.util.List;

import com.rmit.bookflowapp.interfaces.ClickCallback;
import com.squareup.picasso.Picasso;

public class SearchBookAdapter extends RecyclerView.Adapter<SearchBookAdapter.ViewHolder> {
    private ClickCallback click;
    private Context context;
    private List<Book> books;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView searchBookCover;
        private RatingBar ratingBar;
        private TextView searchTitleName, searchAuthorName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            searchBookCover = itemView.findViewById(R.id.searchBookCover);
            ratingBar = itemView.findViewById(R.id.cardBookRating);
            searchTitleName = itemView.findViewById(R.id.searchTitleName);
            searchAuthorName = itemView.findViewById(R.id.searchAuthorName);
            itemView.setOnClickListener(v -> {
                onClick();
            });
        }

        public void onClick() {
            Bundle bundle = new Bundle();
            Book book = books.get(getAdapterPosition());
            bundle.putSerializable("BOOK_OBJECT", book);
            click.onSiteClick(bundle);
        }

    }

    public SearchBookAdapter(ClickCallback click, Context context, List<Book> books) {
        this.click = click;
        this.context = context;
        this.books = books;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View heroView = inflater.inflate(R.layout.search_book_card, parent, false);
        return new SearchBookAdapter.ViewHolder(heroView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchBookAdapter.ViewHolder holder, int position) {
        Book book = books.get(position);
        holder.searchTitleName.setText(book.getTitle());
        holder.searchAuthorName.setText(book.getAuthorString());
        Picasso.get().load(book.getImageUrl()).into(holder.searchBookCover);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setBooks(ArrayList<Book> books) {
        this.books = books;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return books.size();
    }
}