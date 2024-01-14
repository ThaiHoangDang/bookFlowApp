package com.rmit.bookflowapp.fragment;

import android.media.Rating;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.rmit.bookflowapp.Model.Book;
import com.rmit.bookflowapp.Model.Comment;
import com.rmit.bookflowapp.Model.Genre;
import com.rmit.bookflowapp.Model.Post;
import com.rmit.bookflowapp.Model.Review;
import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.Ultilities.Helper;
import com.rmit.bookflowapp.activity.MainActivity;
import com.rmit.bookflowapp.adapter.SearchBookAdapter;
import com.rmit.bookflowapp.databinding.FragmentGenreBinding;
import com.rmit.bookflowapp.databinding.FragmentPostDetailBinding;
import com.rmit.bookflowapp.repository.BookRepository;
import com.rmit.bookflowapp.repository.CommentRepository;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class PostDetailFragment extends Fragment {
    private static final String TAG = "PostDetailFragment";
    private FragmentPostDetailBinding bind;
    private MainActivity activity;
    private Post post;

    public PostDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();

        // end fragment if no data found
        if (arguments == null) getParentFragmentManager().popBackStack();
        post = (Post) Objects.requireNonNull(arguments).getSerializable("POST_OBJECT");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        bind = FragmentPostDetailBinding.inflate(inflater, container, false);
        activity.setBottomNavigationBarVisibility(false);

        // setup display
        bind.postOwner.setText(post.getUser().getName());
        if (post instanceof Review) bind.rating.setRating(((Review) post).getRating());
        bind.postTitle.setText(post.getTitle());
        bind.postContent.setText(post.getContent());
        bind.postDate.setText(Helper.convertTime(post.getTimestamp()));

        View bookLayout = LayoutInflater.from(requireContext()).inflate(R.layout.search_book_card, null);
        ((TextView) bookLayout.findViewById(R.id.searchTitleName)).setText(post.getBook().getTitle());
        ((TextView) bookLayout.findViewById(R.id.searchAuthorName)).setText(post.getBook().getAuthorString());
        Picasso.get().load(post.getBook().getImageUrl()).into((ImageView) bookLayout.findViewById(R.id.searchBookCover));
        bind.targetBook.addView(bookLayout);

        bind.back.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        bind.btnSend.setOnClickListener(v -> {

            // check for missing input
            if (bind.textSend.getText().length() == 0) {
                Toast.makeText(activity, "Missing input!", Toast.LENGTH_SHORT).show();
                return;
            }

            // setup comment for pushing
            Comment comment = new Comment();
            comment.setId(UUID.randomUUID().toString());
            comment.setUserId(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
            comment.setPostId(post.getId());
            comment.setContent(bind.textSend.getText().toString());
            comment.setTimestamp(System.currentTimeMillis() / 1000L);

            CommentRepository.getInstance().addComment(comment);
        });


        return bind.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind = null;
    }

}