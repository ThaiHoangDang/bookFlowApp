package com.rmit.bookflowapp.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.rmit.bookflowapp.Model.Book;
import com.rmit.bookflowapp.Model.Genre;
import com.rmit.bookflowapp.Model.Post;
import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.activity.MainActivity;
import com.rmit.bookflowapp.adapter.PostAdapter;
import com.rmit.bookflowapp.adapter.SearchBookAdapter;
import com.rmit.bookflowapp.databinding.FragmentGenreBinding;
import com.rmit.bookflowapp.interfaces.ClickCallback;
import com.rmit.bookflowapp.repository.BookRepository;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class GenreFragment extends Fragment implements ClickCallback {
    private static final String TAG = "GenreFragment";
    private Genre genre;
    private FragmentGenreBinding bind;
    private MainActivity activity;
    private SearchBookAdapter bookAdapter;
    private ArrayList<Book> books = new ArrayList<>();

    public GenreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();

        // end fragment if no data found
        if (arguments == null) getParentFragmentManager().popBackStack();
        genre = (Genre) Objects.requireNonNull(arguments).getSerializable("GENRE_OBJECT");

        BookRepository.getInstance().getBookByGenreId(genre.getId()).addOnSuccessListener(new OnSuccessListener<List<Book>>() {
            @Override
            public void onSuccess(List<Book> books) {
                GenreFragment.this.books = new ArrayList<>(books);

                // Notify the adapter about the data change
                if (bookAdapter != null) bookAdapter.setBooks(GenreFragment.this.books);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        bind = FragmentGenreBinding.inflate(inflater, container, false);
        activity.setBottomNavigationBarVisibility(false);

        // display genre information
        bind.genreName.setText(genre.getName());
        bind.genreGenreDescription.setText(genre.getDescription());
        Picasso.get().load(genre.getImageUrl()).into(bind.genreBackground);

        // set up books list
        bookAdapter = new SearchBookAdapter(GenreFragment.this, activity, books);
        bind.genreBooksList.setAdapter(bookAdapter);
        bind.genreBooksList.setLayoutManager(new LinearLayoutManager(activity));

        bind.back.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return bind.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind = null;
    }

    @Override
    public void onSiteClick(Bundle bundle) {
        Navigation.findNavController(getView()).navigate(R.id.bookDetailFragment, bundle);
    }
}