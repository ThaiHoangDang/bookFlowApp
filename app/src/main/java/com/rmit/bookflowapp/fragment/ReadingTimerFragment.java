package com.rmit.bookflowapp.fragment;

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
    private static final long INTERVAL = 1000; // 1 second interval
    private long elapsedTime = 0;
    private boolean isTimerRunning = false;
    private Handler handler = new Handler();


    public ReadingTimerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        bind = FragmentReadingTimerBinding.inflate(inflater, container, false);
        activity.setBottomNavigationBarVisibility(true);

//        bind.back.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        // Stop the timer service (when the user clicks the stop button)
        bind.stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.stopService(new Intent(getActivity(), ReadingTimerService.class));
            }
        });

        // start timer service
        activity.startService(new Intent(getActivity(), ReadingTimerService.class));

        // Register the receiver
        IntentFilter filter = new IntentFilter("TIMER_UPDATE");
        activity.registerReceiver(timerUpdateReceiver, filter);

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
            long elapsedTime = intent.getLongExtra("ELAPSED_TIME", 0);
            updateElapsedTimeUI(elapsedTime);
        }
    };

    private void updateElapsedTimeUI(long elapsedTime) {
        String formattedTime = "Elapsed Time: " + elapsedTime / 1000 + " seconds";
        bind.elapsedTimeTextView.setText(formattedTime);
    }
}