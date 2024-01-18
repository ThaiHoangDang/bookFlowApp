package com.rmit.bookflowapp.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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
    private String query = "ALL"; // "ALL", "REVIEW", "LEND"
    private String sort = "ASC"; // "ASC" or "DESC"

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

        bind.createPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getView()).navigate(R.id.readingTimerFragment);
            }
        });

        bind.pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData(query);
                bind.pullToRefresh.setRefreshing(false);
            }
        });

        bind.filterAllBtn.setOnClickListener(v1 -> {
            query = "ALL";
            getData(query);
            restoreFilterButtons(query);
        });
        bind.filterReviewBtn.setOnClickListener(v1 -> {
            query = "REVIEW";
            getData(query);
            restoreFilterButtons(query);
        });
        bind.filterLendBtn.setOnClickListener(v1 -> {
            query = "LEND";
            getData(query);
            restoreFilterButtons(query);
        });
        bind.sortAscBtn.setOnClickListener(v1 -> {
            sort = "ASC";
            getData(query);
            restoreSortButtons(sort);
        });
        bind.sortDescBtn.setOnClickListener(v1 -> {
            sort = "DESC";
            getData(query);
            restoreSortButtons(sort);
        });

        bind.filterBtn.setOnClickListener(v1 -> {
            if (bind.filterForm.getVisibility() == View.GONE) {
                bind.filterForm.setVisibility(View.VISIBLE);
                bind.sortForm.setVisibility(View.VISIBLE);
                bind.pullToRefresh.setVisibility(View.GONE);
            } else {
                bind.filterForm.setVisibility(View.GONE);
                bind.sortForm.setVisibility(View.GONE);
                bind.pullToRefresh.setVisibility(View.VISIBLE);
            }
        });

        bind.searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            // filter when user press enter
            @Override
            public boolean onQueryTextSubmit(String query) {
                postAdapter.setFilteredList(filter(posts, query));
                return false;
            }

            // filter in real time
            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText == null || newText.isEmpty() || newText.length() == 0) {
                    return false;
                }

                postAdapter.setFilteredList(filter(posts, newText));
                return false;
            }
        });

        return bind.getRoot();
    }

    @Override
    public void onPause() {
        super.onPause();
        bind.searchView.setQuery("", false); // Clear the search query
//        bind.searchView.setSaveEnabled(false); // disable searchView from saving query when fragment is paused
    }

    public ArrayList<Post> filter(ArrayList<Post> posts, String query) {
        ArrayList<Post> filteredPosts = new ArrayList<>();
        for (Post post : posts) {
            // searches for five fields: post title, post content, book title, book content, user name
            if (post.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                post.getContent().toLowerCase().contains(query.toLowerCase()) ||
                post.getBook().getTitle().toLowerCase().contains(query.toLowerCase()) ||
                post.getBook().getAuthorString().toLowerCase().contains(query.toLowerCase()) ||
                post.getUser().getName().toLowerCase().contains(query.toLowerCase())
            ) {
                filteredPosts.add(post);
            }
        }
        return filteredPosts;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {

            sort = savedInstanceState.getString("sort");
            query = savedInstanceState.getString("query");
            getData(query);
            restoreFilterButtons(query);
            restoreSortButtons(sort);
        } else {

            query = "ALL";
            sort = "ASC";
            getData("ALL");
            restoreFilterButtons(query);
            restoreSortButtons(sort);
        }
        activity.setBottomNavigationBarVisibility(true);
    }

    public void restoreFilterButtons(String query) {
        switch (query) {
            case "ALL":
                bind.filterAllBtn.setBackgroundColor(getResources().getColor(R.color.neutral_100));
                bind.filterReviewBtn.setBackgroundColor(getResources().getColor(R.color.orange));
                bind.filterLendBtn.setBackgroundColor(getResources().getColor(R.color.orange));
                break;
            case "REVIEW":
                bind.filterAllBtn.setBackgroundColor(getResources().getColor(R.color.orange));
                bind.filterReviewBtn.setBackgroundColor(getResources().getColor(R.color.neutral_100));
                bind.filterLendBtn.setBackgroundColor(getResources().getColor(R.color.orange));
                break;
            case "LEND":
                bind.filterAllBtn.setBackgroundColor(getResources().getColor(R.color.orange));
                bind.filterReviewBtn.setBackgroundColor(getResources().getColor(R.color.orange));
                bind.filterLendBtn.setBackgroundColor(getResources().getColor(R.color.neutral_100));
                break;
        }
    }

    public void restoreSortButtons(String sort) {
        switch (sort) {
            case "ASC":
                bind.sortAscBtn.setBackgroundColor(getResources().getColor(R.color.neutral_100));
                bind.sortDescBtn.setBackgroundColor(getResources().getColor(R.color.orange));
                break;
            case "DESC":
                bind.sortAscBtn.setBackgroundColor(getResources().getColor(R.color.orange));
                bind.sortDescBtn.setBackgroundColor(getResources().getColor(R.color.neutral_100));
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("query", query);
        outState.putString("sort", sort);
        super.onSaveInstanceState(outState);
    }

    public void getData(String query) {
        // get data from Firebase and pass to adapter
        switch (query) {
            case "ALL":
                PostRepository.getInstance().getAllPosts().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        getDataSuccess(task);
                    }
                });
                break;
            case "REVIEW":
                PostRepository.getInstance().getAllReviewPosts().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        getDataSuccess(task);
                    }
                });
                break;
            case "LEND":
                PostRepository.getInstance().getAllLendPosts().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        getDataSuccess(task);
                    }
                });
                break;
        }
    }

    public void getDataSuccess(Task<QuerySnapshot> task) {
        posts.clear();

        for (DocumentSnapshot task1 : task.getResult().getDocuments()) {
//            Log.d(TAG, "onCreateView: " + task1.getData());

            // finish two tasks before continue
            Task<User> tempuser = UserRepository.getInstance().getUserById((String) task1.get("userId"));
            Task<Book> tempbook = BookRepository.getInstance().getBookById((String) task1.get("bookId"));
            Tasks.whenAllSuccess(tempuser, tempbook).addOnSuccessListener(task2 -> {
//                Log.d(TAG, "onCreateView: " + task2.get(0).toString());
//                Log.d(TAG, "onCreateView: " + task2.get(1).toString());
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
                                    (Double) ((List<?>) task1.get("location")).get(0),
                                    (Double) ((List<?>) task1.get("location")).get(1)
                            ));
                    posts.add(post);
                }
                return null;
            }).addOnCompleteListener(task4 -> {
                // sort posts by timestamp
                if (sort.equals("ASC")) {
                    posts.sort((o1, o2) -> (int) (o1.getTimestamp() - o2.getTimestamp()));
                } else if (sort.equals("DESC")) {
                    posts.sort((o1, o2) -> (int) (o2.getTimestamp() - o1.getTimestamp()));
                }
                // notify adapter when done
                postAdapter.notifyDataSetChanged();
            });
        }
//        postAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        bind.searchView.setOnQueryTextListener(null);
        bind = null;
    }
}