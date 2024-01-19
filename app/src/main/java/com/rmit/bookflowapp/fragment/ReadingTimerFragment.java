package com.rmit.bookflowapp.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.rmit.bookflowapp.Model.Book;
import com.rmit.bookflowapp.Model.Review;
import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.activity.MainActivity;
import com.rmit.bookflowapp.databinding.FragmentNewReviewBinding;
import com.rmit.bookflowapp.databinding.FragmentReadingTimerBinding;
import com.rmit.bookflowapp.repository.PostRepository;
import com.rmit.bookflowapp.service.ReadingTimerService;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class ReadingTimerFragment extends Fragment {
    private static final String TAG = "ReadingTimerFragment";
    private FragmentReadingTimerBinding bind;
    private MainActivity activity;
    private Book book;
    private boolean timerRunning = false;
    private long elapsedTime = 0;

    public ReadingTimerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();

        // end fragment if no data found
        if (arguments == null) getParentFragmentManager().popBackStack();
        book = (Book) Objects.requireNonNull(arguments).getSerializable("BOOK_OBJECT");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        bind = FragmentReadingTimerBinding.inflate(inflater, container, false);
        activity.setBottomNavigationBarVisibility(false);

        // setup display of read book
        View bookToReview = LayoutInflater.from(requireContext()).inflate(R.layout.search_book_card, null);
        ((TextView) bookToReview.findViewById(R.id.searchTitleName)).setText(book.getTitle());
        ((TextView) bookToReview.findViewById(R.id.searchAuthorName)).setText(book.getAuthorString());
        Picasso.get().load(book.getImageUrl()).into((ImageView) bookToReview.findViewById(R.id.searchBookCover));
        bind.newReviewBookHolder.addView(bookToReview);


        bind.back.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        // Stop the timer service (when the user clicks the stop button)
        bind.triggerButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {

                if (! timerRunning) {
                    activity.startService(new Intent(getActivity(), ReadingTimerService.class));
                    bind.triggerButton.setText("Stop");
                } else {
                    activity.stopService(new Intent(getActivity(), ReadingTimerService.class));
                    bind.triggerButton.setText("Start");
                }

                timerRunning = ! timerRunning;
            }
        });

        // Register the receiver
        IntentFilter filter = new IntentFilter("TIMER_UPDATE");
        activity.registerReceiver(timerUpdateReceiver, filter);


        bind.readTimerSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reviewTitle = bind.reviewTitleText.getText().toString();
                String reviewContent = bind.reviewContentText.getText().toString();

                // check for missing input
                if (reviewTitle.length() == 0 || reviewContent.length() == 0) {
                    Toast.makeText(activity, "Missing input field!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // check for missing input
                if (elapsedTime == 0) {
                    Toast.makeText(activity, "You have to read the book :)", Toast.LENGTH_SHORT).show();
                    return;
                }

                // data holder
                Map<String, Object> readData = new HashMap<>();
                readData.put("bookId", book.getId());
                readData.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                readData.put("title", reviewTitle);
                readData.put("content", reviewContent);
                readData.put("readingTime", elapsedTime);
                readData.put("timestamp", System.currentTimeMillis() / 1000L);


                PostRepository.getInstance().addReview(readData).addOnSuccessListener(unused -> {
                    Toast.makeText(activity, "Hope you have a great time!", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                }).addOnFailureListener(e -> Toast.makeText(activity, "Upload failed!", Toast.LENGTH_SHORT).show());

            }
        });

        return bind.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Unregister the receiver when the fragment is destroyed
        activity.unregisterReceiver(timerUpdateReceiver);
        bind = null;
    }

    private BroadcastReceiver timerUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            elapsedTime = intent.getLongExtra("ELAPSED_TIME", 0);
            updateElapsedTimeUI(elapsedTime);
        }
    };

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void updateElapsedTimeUI(long elapsedTime) {
        long seconds = (elapsedTime / 1000) % 60;
        long minutes = (elapsedTime / (1000 * 60)) % 60;
        long hours = (elapsedTime / (1000 * 60 * 60));

        bind.timerSec.setText(String.format("%02d", seconds));
        bind.timerMin.setText(String.format("%02d", minutes));
        bind.timerHour.setText(String.format("%02d", hours));
    }

}