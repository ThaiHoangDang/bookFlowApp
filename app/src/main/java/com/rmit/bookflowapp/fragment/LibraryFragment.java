package com.rmit.bookflowapp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rmit.bookflowapp.Model.Book;
import com.rmit.bookflowapp.Model.Genre;
import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.activity.MainActivity;
import com.rmit.bookflowapp.adapter.SearchBookAdapter;
import com.rmit.bookflowapp.databinding.FragmentLibraryBinding;
import com.rmit.bookflowapp.repository.BookRepository;
import com.rmit.bookflowapp.repository.GenreRepository;
import com.rmit.bookflowapp.util.TranslateAnimationUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class LibraryFragment extends Fragment {
    private static final String TAG = "LibraryFragment";
    private FragmentLibraryBinding bind;
    private MainActivity activity;

    private SearchBookAdapter searchBookAdapter;
    private List<Book> books = new ArrayList<>();

    private final Handler debounceHandler = new Handler();

    private final Runnable debounceRunnable = new Runnable() {
        @Override
        public void run() {
            if (bind != null) {
                String query = bind.searchView.getQuery().toString().trim();
                if (!query.isEmpty()) {
                    performSearch(query);
                } else {
                    books.clear();
                    searchBookAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    public LibraryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        bind = FragmentLibraryBinding.inflate(inflater, container, false);
        activity.setBottomNavigationBarVisibility(true);
        searchBookAdapter = new SearchBookAdapter(activity, books);

        bind.librarySearch.setVisibility(View.GONE);

        bind.imageView2.setOnClickListener(v -> {
            TranslateAnimationUtil.fadeOutViewStatic(bind.libraryMain);
            TranslateAnimationUtil.fadeInViewStatic(bind.librarySearch);
        });

        bind.cancelButton.setOnClickListener(v -> {
            TranslateAnimationUtil.fadeOutViewStatic(bind.librarySearch);
            bind.searchView.setQuery("", false);
            books.clear();
            searchBookAdapter.notifyDataSetChanged();
            TranslateAnimationUtil.fadeInViewStatic(bind.libraryMain);
        });

        bind.searchBody.setAdapter(searchBookAdapter);
        bind.searchBody.setLayoutManager(new LinearLayoutManager(activity));

        bind.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                debounceHandler.removeCallbacks(debounceRunnable);
                debounceHandler.postDelayed(debounceRunnable, 1000);
                return true;
            }
        });

//        Generate data for library fragment
        BookRepository.getInstance().getBookForLibraryFragment().addOnCompleteListener(getBooksTask -> {
            if (getBooksTask.isSuccessful()) {
                List<Book> bookList = getBooksTask.getResult();
                if (bookList != null) {
                    for (int i = 0; i < bookList.size() / 2; i++) {
                        Book currentBook = bookList.get(i);
                        View bookLayout = LayoutInflater.from(requireContext()).inflate(R.layout.book_card, null);
                        ((TextView) bookLayout.findViewById(R.id.cardBookName)).setText(currentBook.getTitle());
                        ((TextView) bookLayout.findViewById(R.id.cardBookAuthor)).setText(currentBook.getAuthorString());
                        ImageView bookImage = bookLayout.findViewById(R.id.cardBookImage);
                        String imageUrl = currentBook.getImageUrl();
                        Picasso.get().load(imageUrl).into(bookImage);
                        bind.bestOfAllTime.addView(bookLayout);
                    }

                    for (int i = bookList.size() / 2; i < bookList.size(); i++) {
                        Book currentBook = bookList.get(i);
                        View bookLayout = LayoutInflater.from(requireContext()).inflate(R.layout.book_card, null);
                        ((TextView) bookLayout.findViewById(R.id.cardBookName)).setText(currentBook.getTitle());
                        ((TextView) bookLayout.findViewById(R.id.cardBookAuthor)).setText(currentBook.getAuthorString());
                        ImageView bookImage = bookLayout.findViewById(R.id.cardBookImage);
                        String imageUrl = currentBook.getImageUrl();
                        Picasso.get().load(imageUrl).into(bookImage);
                        bind.trending.addView(bookLayout);
                    }
                }
            }
        });

        GenreRepository.getInstance().getGenreForLibraryFragment().addOnCompleteListener(getGenresTask -> {
            if (getGenresTask.isSuccessful()) {
                List<Genre> genreList = getGenresTask.getResult();
                for (int i = 0; i < genreList.size(); i++) {
                    Genre currentGenre = genreList.get(i);
                    View genreCardLayout = LayoutInflater.from(requireContext()).inflate(R.layout.genre_card, null);
                    ((TextView) genreCardLayout.findViewById(R.id.textView3)).setText(currentGenre.getName());
                    ImageView genreImage = genreCardLayout.findViewById(R.id.imageView3);
                    Picasso.get().load(currentGenre.getImageUrl()).into(genreImage);
                    bind.genre.addView(genreCardLayout);
                }
            }
        });

        return bind.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind = null;
    }

    private void performSearch(String query) {
        books.clear();
        BookRepository.getInstance().getBookByQuery(query).addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null) {
                            books.clear();
                            books.addAll(task.getResult());
                            searchBookAdapter.notifyDataSetChanged();
                        }
                    }
                }
        );
    }
}