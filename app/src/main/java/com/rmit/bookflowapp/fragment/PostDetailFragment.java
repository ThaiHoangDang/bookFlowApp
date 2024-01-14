package com.rmit.bookflowapp.fragment;

import android.media.Rating;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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
import com.rmit.bookflowapp.adapter.CommentAdapter;
import com.rmit.bookflowapp.adapter.ReviewAdapter;
import com.rmit.bookflowapp.adapter.SearchBookAdapter;
import com.rmit.bookflowapp.databinding.FragmentGenreBinding;
import com.rmit.bookflowapp.databinding.FragmentPostDetailBinding;
import com.rmit.bookflowapp.repository.BookRepository;
import com.rmit.bookflowapp.repository.CommentRepository;
import com.rmit.bookflowapp.viewmodel.BookDetailViewModel;
import com.rmit.bookflowapp.viewmodel.PostDetailViewModel;
import com.rmit.bookflowapp.viewmodel.factory.BookDetailViewModelFactory;
import com.rmit.bookflowapp.viewmodel.factory.PostDetailViewModelFactory;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;


public class PostDetailFragment extends Fragment {
    private static final String TAG = "PostDetailFragment";
    private FragmentPostDetailBinding bind;
    private MainActivity activity;
    private Post post;
    private PostDetailViewModel viewModel;
    private CommentAdapter commentAdapter;

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

        assert post != null;
        PostDetailViewModelFactory factory = new PostDetailViewModelFactory(post.getId());
        viewModel = new ViewModelProvider(this, factory).get(PostDetailViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        bind = FragmentPostDetailBinding.inflate(inflater, container, false);
        activity.setBottomNavigationBarVisibility(false);

        // setup display
        setupView();

        bind.back.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        // add new comment
        bind.btnSend.setOnClickListener(v -> {

            // check for missing input
            if (bind.textSend.getText().length() == 0) {
                Toast.makeText(activity, "Missing input!", Toast.LENGTH_SHORT).show();
                return;
            }

            // data holder
            Map<String, Object> commentData = new HashMap<>();
            commentData.put("id", UUID.randomUUID().toString());
            commentData.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
            commentData.put("content", bind.textSend.getText().toString());
            commentData.put("postId", post.getId());
            commentData.put("timestamp", System.currentTimeMillis() / 1000L);

            CommentRepository.getInstance().addComment(commentData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    viewModel.refreshPostComments();
                    Toast.makeText(activity, "New comment added!", Toast.LENGTH_SHORT).show();
                    bind.textSend.clearFocus();
                    bind.textSend.setText("");
                }
            });
        });

        return bind.getRoot();
    }

    private void setupView() {
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

        commentAdapter = new CommentAdapter(activity, new ArrayList<>());
        bind.postDetailCommentsList.setAdapter(commentAdapter);
        bind.postDetailCommentsList.setLayoutManager(new LinearLayoutManager(activity));
        viewModel.getPostComments().observe(getViewLifecycleOwner(), comments -> commentAdapter.setItems(comments));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind = null;
    }

}