package com.rmit.bookflowapp.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.rmit.bookflowapp.Model.Book;
import com.rmit.bookflowapp.Model.Lend;
import com.rmit.bookflowapp.Model.Post;
import com.rmit.bookflowapp.Model.Review;
import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.Model.User;
import com.rmit.bookflowapp.activity.MainActivity;
import com.rmit.bookflowapp.adapter.PostAdapter;
import com.rmit.bookflowapp.databinding.FragmentHomeBinding;
import com.rmit.bookflowapp.repository.BookRepository;
import com.rmit.bookflowapp.repository.PostRepository;
import com.rmit.bookflowapp.repository.UserRepository;
import com.rmit.bookflowapp.util.TranslateAnimationUtil;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private FragmentHomeBinding bind;
    private MainActivity activity;
    private PostAdapter postAdapter;
    private ArrayList<Post> posts = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        bind = FragmentHomeBinding.inflate(inflater, container, false);
        activity.setBottomNavigationBarVisibility(true);

        // set up posts list
        postAdapter = new PostAdapter(activity, posts);
        bind.postsListView.setAdapter(postAdapter);
        bind.postsListView.setLayoutManager(new LinearLayoutManager(activity));

        // Add scroll listener to RecyclerView
        bind.postsListView.setOnTouchListener(new TranslateAnimationUtil(activity, bind.linearlayout1));

        bind.pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                // add data reloading here :D

                Toast.makeText(activity, "Refreshing", Toast.LENGTH_SHORT).show();
                getData();
                bind.pullToRefresh.setRefreshing(false);
            }
        });

        bind.createPostBtn.setOnClickListener(v -> Navigation.findNavController(getView()).navigate(R.id.bookDetailFragment));

        getData();

        return bind.getRoot();
    }

    public void getData() {
        // get data from Firebase and pass to adapter
        PostRepository.getInstance().getPosts(2).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                posts.clear();

                for (DocumentSnapshot task1 : task.getResult().getDocuments()) {
                    Log.d(TAG, "onCreateView: " + task1.getData());

                    // finish two tasks before continue
                    Task<User> tempuser = UserRepository.getInstance().getUserById((String) task1.get("userId"));
                    Task<Book> tempbook = BookRepository.getInstance().getBookById((String) task1.get("bookId"));
                    Tasks.whenAllSuccess(tempuser, tempbook).addOnSuccessListener(task2 -> {
                        Log.d(TAG, "onCreateView: " + task2.get(0).toString());
                        Log.d(TAG, "onCreateView: " + task2.get(1).toString());
                    }).continueWith(task3 -> {
                        // once done with two tasks, task 3 is about creating objects with data taken from task 1 and 2
                        // if task 1 has field rating => it's Review object
                        // else if task 1 has field location => it's Lend object
                        if (task1.getData().get("rating") != null) {
                            Review post = new Review(
                                    task1.getId(),
                                    task1.get("title").toString(),
                                    task1.get("content").toString(),
                                    tempuser.getResult(),
                                    tempbook.getResult(),
                                    (Long) task1.get("timestamp"),
                                    ((Long) task1.get("rating")).intValue()
                            );
                            posts.add(post);
                        } else if (task1.getData().get("location") != null) {
                            Lend post = new Lend(
                                    task1.getId(),
                                    task1.get("title").toString(),
                                    task1.get("content").toString(),
                                    tempuser.getResult(),
                                    tempbook.getResult(),
                                    (Long) task1.get("timestamp"),
                                    new LatLng(
                                            (Long) ((List<?>) task1.get("location")).get(0),
                                            (Long) ((List<?>) task1.get("location")).get(1)
                                    ));
                            posts.add(post);
                        }
                        return null;
                    }).addOnCompleteListener(task4 -> {
                        // notify adapter when done
                        postAdapter.notifyDataSetChanged();
                    });
                }
//                postAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind = null;
    }
}