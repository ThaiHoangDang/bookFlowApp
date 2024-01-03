package com.example.cleanconnect.ui.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.cleanconnect.R;
import com.example.cleanconnect.databinding.FragmentSiteDisplayBinding;
import com.example.cleanconnect.model.Site;
import com.example.cleanconnect.model.User;
import com.example.cleanconnect.repository.SiteRepository;
import com.example.cleanconnect.repository.UserRepository;
import com.example.cleanconnect.ui.activity.MainActivity;
import com.example.cleanconnect.util.ImageLoaderUtils;
import com.example.cleanconnect.util.LocationUtils;
import com.example.cleanconnect.viewmodel.AllSitesViewModel;
import com.example.cleanconnect.viewmodel.JoinedSitesViewModel;
import com.example.cleanconnect.viewmodel.OwnedSitesViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SiteDisplayFragment extends Fragment {
    private final String TAG = "SiteDisplayFragment";
    private FragmentSiteDisplayBinding bind;
    private MainActivity activity;
    private ImageLoaderUtils loader;
    private UserRepository userRepository;

    public SiteDisplayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentSiteDisplayBinding.inflate(inflater, container, false);
        activity = (MainActivity) getActivity();
        userRepository = new UserRepository();
        loader = new ImageLoaderUtils();
        initAppBar();
        initSiteView();
        return bind.getRoot();
    }

    public void initSiteView(){
        Bundle arguments = getArguments();
        if (arguments != null) {
            Site site = arguments.getParcelable("SITE_OBJECT");
            Log.d(TAG, site.toString());
            Double distance = arguments.getDouble("SITE_DISTANCE");
            StringBuilder builder = new StringBuilder().append("Distance: ");
            DecimalFormat decimalFormat = new DecimalFormat("#.00");
            builder.append(decimalFormat.format(distance));
            builder.append(" km");
            bind.siteTitle.setText(site.getTitle());
            bind.distanceView.setText(builder.toString());
            bind.siteDescription.setText(site.getLocationDetails());
            bind.dateView.setText("Date: " + site.getDate());
            bind.amountView.setText("Collected: " + site.getAmountOfWasteCollected() + " kg");
            bind.addressView.setText(site.getAddress());
            loader.loadImage(bind.imageView, site.getImageUrl());
            bind.showRouteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("DEST", new LatLng(site.getLatitude(), site.getLongitude()));
                    activity.navController.navigate(R.id.routeMapFragment, bundle);
                }
            });
            if (activity.getUser().getUserType().equals("SUPER") || activity.getUser().getUid().equals(site.getOwnerId())){
                bind.pCardView.setVisibility(View.VISIBLE);
                bind.joinButton.setVisibility(View.GONE);
            }
            if (site.getJoinedUserIds().isEmpty()){
                setNothingVisibility(true);
            } else {
                List<String> participants = new ArrayList<>();

                List<Task<User>> userTasks = new ArrayList<>();

                for (String userId : site.getJoinedUserIds()) {
                    Task<User> userTask = userRepository.getUser(userId).addOnSuccessListener(new OnSuccessListener<User>() {
                        @Override
                        public void onSuccess(User user) {
                            String name = user.getDisplayName();
                            participants.add(name);
                        }
                    });

                    userTasks.add(userTask);
                }

                Tasks.whenAllSuccess(userTasks).addOnCompleteListener(new OnCompleteListener<List<Object>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Object>> task) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, participants);
                        bind.participantsView.setAdapter(adapter);
                    }
                });
            }
            if (!site.getJoinedUserIds().contains(activity.getUser().getUid()) && !site.getOwnerId().equals(activity.getUser().getUid())) {
                bind.joinButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.setProgressVisibility(true);
                        Map<String, Object> field = new HashMap<>();
                        site.getJoinedUserIds().add(activity.getUser().getUid());
                        field.put("joinedUserIds", site.getJoinedUserIds());
                        SiteRepository siteRepository = new SiteRepository();
                        siteRepository.updateSite(field, site.getSiteId()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                JoinedSitesViewModel joinedSitesViewModel = new ViewModelProvider(requireActivity()).get(JoinedSitesViewModel.class);
                                joinedSitesViewModel.setUser(activity.getUser());
                                joinedSitesViewModel.refresh();
                                AllSitesViewModel allSitesViewModel = new ViewModelProvider(requireActivity()).get(AllSitesViewModel.class);
                                allSitesViewModel.refresh();
                                activity.setProgressVisibility(false);
                                bind.joinButton.setEnabled(false);
                                bind.joinButton.setText("Joined");
                                Toast.makeText(getContext(), "Joined successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            } else {
                bind.joinButton.setEnabled(false);
                bind.joinButton.setText("Joined");
            }
        }
    }

    private void setNothingVisibility(boolean boo){
        if (boo){
            bind.scrollParticipants.setVisibility(View.GONE);
            bind.nothingView.setVisibility(View.VISIBLE);
        }
    }
    private void initAppBar() {
        activity.setSupportActionBar(bind.animToolbar);

        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        bind.animToolbar.setTitle("Site Details");


        bind.animToolbar.setNavigationOnClickListener(v -> {
            Log.d("ok", "clicked");
            activity.navController.navigateUp();
        });

        Objects.requireNonNull(bind.animToolbar.getOverflowIcon()).setTint(requireContext().getResources().getColor(R.color.titleTextColor, null));
    }
}