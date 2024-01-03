package com.rmit.bookflowapp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rmit.bookflowapp.Model.Book;
import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.activity.MainActivity;
import com.rmit.bookflowapp.adapter.SearchBookAdapter;
import com.rmit.bookflowapp.databinding.FragmentLibraryBinding;
import com.rmit.bookflowapp.repository.BookRepository;
import com.rmit.bookflowapp.util.TranslateAnimationUtil;

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
                        bind.bestOfAllTime.addView(bookLayout);
                    }
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
        Toast.makeText(requireActivity(), query, Toast.LENGTH_SHORT).show();
    }
}