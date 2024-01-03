package com.rmit.bookflowapp.adapter;

import android.content.Context;
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

import java.util.List;

public class SearchBookAdapter extends RecyclerView.Adapter<SearchBookAdapter.ViewHolder> {
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
        }
    }

    public SearchBookAdapter(Context context, List<Book> books) {
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
    }

    @Override
    public int getItemCount() {
        return books.size();
    }
}