package com.example.cleanconnect.ui.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cleanconnect.R;
import com.example.cleanconnect.databinding.FragmentJoinedSitesBinding;
import com.example.cleanconnect.interfaces.ClickCallBack;
import com.example.cleanconnect.model.Site;
import com.example.cleanconnect.ui.activity.MainActivity;
import com.example.cleanconnect.ui.adapter.SiteHorizontalAdapter;
import com.example.cleanconnect.ui.dialog.SitesFilterDialog;
import com.example.cleanconnect.viewmodel.JoinedSitesViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class JoinedSitesFragment extends Fragment implements ClickCallBack {
    private FragmentJoinedSitesBinding bind;
    private JoinedSitesViewModel viewModel;
    private SiteHorizontalAdapter adapter;
    private MainActivity activity;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng userLocation;


    public JoinedSitesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        bind = FragmentJoinedSitesBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(JoinedSitesViewModel.class);
        viewModel.setUser(activity.getUser());
        initSiteView();
        return bind.getRoot();
    }

    private void initSiteView() {
        bind.siteRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        bind.siteRecyclerView.setHasFixedSize(true);

        bind.siteFilterClickable.setOnClickListener(v -> {
            SitesFilterDialog dialogFragment = new SitesFilterDialog(this);
            dialogFragment.show(activity.getSupportFragmentManager(), null);
        });
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                viewModel.setUserLocation(userLocation);
                adapter = new SiteHorizontalAdapter(JoinedSitesFragment.this, userLocation);
                bind.siteRecyclerView.setAdapter(adapter);
                viewModel.getAllSitesLiveData().observe(getViewLifecycleOwner(), new Observer<List<Site>>() {
                    @Override
                    public void onChanged(List<Site> sites) {
                        Log.d("sites", sites.toString());
                        adapter.setItems(sites);
                    }
                });
            }
        });
    }

    @Override
    public void onSiteClick(Bundle bundle) {
        Navigation.findNavController(getView()).navigate(R.id.siteDisplayFragment, bundle);
    }

    @Override
    public void onFilterApply(Double maxDistance, String sortCriteria) {
        viewModel.applyFilter(maxDistance, sortCriteria);
    }
}