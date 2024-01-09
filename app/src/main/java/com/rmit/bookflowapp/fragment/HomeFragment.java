package com.rmit.bookflowapp.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        bind = FragmentHomeBinding.inflate(inflater, container, false);
        activity.setBottomNavigationBarVisibility(true);

        // set up action bar
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Home</font>"));
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);

        // set up posts list
        postAdapter = new PostAdapter(activity, posts);
        bind.postsListView.setAdapter(postAdapter);
        bind.postsListView.setLayoutManager(new LinearLayoutManager(activity));

        // Add scroll listener to RecyclerView
//        bind.postsListView.setOnTouchListener(new TranslateAnimationUtil(activity, bind.linearlayout1));

        bind.pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(activity, "Refreshed", Toast.LENGTH_SHORT).show();
                getData(query);
                bind.pullToRefresh.setRefreshing(false);
            }
        });

//        bind.createPostBtn.setOnClickListener(v -> Navigation.findNavController(getView()).navigate(R.id.bookDetailFragment));

        bind.filterAllBtn.setOnClickListener(v1 -> {
            getData("ALL");
            query = "ALL";
            bind.filterAllBtn.setBackgroundColor(getResources().getColor(R.color.neutral_100));
            bind.filterReviewBtn.setBackgroundColor(getResources().getColor(R.color.orange));
            bind.filterLendBtn.setBackgroundColor(getResources().getColor(R.color.orange));
        });
        bind.filterReviewBtn.setOnClickListener(v1 -> {
            getData("REVIEW");
            query = "REVIEW";
            bind.filterAllBtn.setBackgroundColor(getResources().getColor(R.color.orange));
            bind.filterReviewBtn.setBackgroundColor(getResources().getColor(R.color.neutral_100));
            bind.filterLendBtn.setBackgroundColor(getResources().getColor(R.color.orange));
        });
        bind.filterLendBtn.setOnClickListener(v1 -> {
            getData("LEND");
            query = "LEND";
            bind.filterAllBtn.setBackgroundColor(getResources().getColor(R.color.orange));
            bind.filterReviewBtn.setBackgroundColor(getResources().getColor(R.color.orange));
            bind.filterLendBtn.setBackgroundColor(getResources().getColor(R.color.neutral_100));
        });
        bind.sortAscBtn.setOnClickListener(v1 -> {
            sort = "ASC";
            bind.sortAscBtn.setBackgroundColor(getResources().getColor(R.color.neutral_100));
            bind.sortDescBtn.setBackgroundColor(getResources().getColor(R.color.orange));
            getData(query);
        });
        bind.sortDescBtn.setOnClickListener(v1 -> {
            sort = "DESC";
            bind.sortAscBtn.setBackgroundColor(getResources().getColor(R.color.orange));
            bind.sortDescBtn.setBackgroundColor(getResources().getColor(R.color.neutral_100));
            getData(query);
        });

        query = "ALL";
        sort = "ASC";
        getData("ALL"); // by default, first load will query all posts, but the next pull down refresh will be based on String query
        bind.filterAllBtn.setBackgroundColor(getResources().getColor(R.color.neutral_100));
        bind.sortAscBtn.setBackgroundColor(getResources().getColor(R.color.neutral_100));

        return bind.getRoot();
    }

//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        if (savedInstanceState != null) {
//            Toast.makeText(activity, savedInstanceState.getString("query"), Toast.LENGTH_SHORT).show();
////            bind.searchView.setQuery(savedInstanceState.getString("query"), true);
//            bind.postsListView.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable("listState"));
//        } else {
//            Toast.makeText(activity, "null", Toast.LENGTH_SHORT).show();
//        }
//        activity.setBottomNavigationBarVisibility(true);
//    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
////        save search query
//        outState.putString("query", query);
//        outState.putParcelable("listState", bind.postsListView.getLayoutManager().onSaveInstanceState());
//        super.onSaveInstanceState(outState);
//    }

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu_for_home_fragment, menu);
        super.onCreateOptionsMenu(menu,inflater);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) searchItem.getActionView();

        searchView.setQueryHint("Search by title or content");

        // add listener to search bar
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            // filter when user press enter
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            // filter in real time
            @Override
            public boolean onQueryTextChange(String newText) {
                postAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {

            // refresh the recycler view
            int visible = bind.filterForm.getVisibility();
            if (visible == View.GONE) {
                bind.filterForm.setVisibility(View.VISIBLE);
                TranslateAnimationUtil.fadeOutViewStatic(bind.pullToRefresh);
                TranslateAnimationUtil.fadeInViewStatic(bind.filterForm);
            } else {
                bind.filterForm.setVisibility(View.GONE);
                TranslateAnimationUtil.fadeOutViewStatic(bind.filterForm);
                TranslateAnimationUtil.fadeInViewStatic(bind.pullToRefresh);
            }
            int visible2 = bind.sortForm.getVisibility();
            if (visible2 == View.GONE) {
                bind.sortForm.setVisibility(View.VISIBLE);
                TranslateAnimationUtil.fadeOutViewStatic(bind.pullToRefresh);
                TranslateAnimationUtil.fadeInViewStatic(bind.sortForm);
            } else {
                bind.sortForm.setVisibility(View.GONE);
                TranslateAnimationUtil.fadeOutViewStatic(bind.sortForm);
                TranslateAnimationUtil.fadeInViewStatic(bind.pullToRefresh);
            }

            return true;
        }
        else if (item.getItemId() == R.id.action_search) {

            // copy-pasted the same code from onCreateView method
            postAdapter = new PostAdapter(activity, posts);
            bind.postsListView.setAdapter(postAdapter);
            bind.postsListView.setLayoutManager(new LinearLayoutManager(activity));

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}