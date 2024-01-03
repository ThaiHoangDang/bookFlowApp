package com.example.cleanconnect.ui.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cleanconnect.R;
import com.example.cleanconnect.databinding.FragmentMapBinding;
import com.example.cleanconnect.databinding.FragmentRouteMapBinding;
import com.example.cleanconnect.interfaces.TaskLoadedCallback;
import com.example.cleanconnect.model.Site;
import com.example.cleanconnect.ui.activity.MainActivity;
import com.example.cleanconnect.util.FetchURL;
import com.example.cleanconnect.viewmodel.MapViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.PolyUtil;

import java.util.List;
import java.util.Objects;

public class RouteMapFragment extends Fragment implements OnMapReadyCallback, TaskLoadedCallback {
    private final String TAG = "MapFragment";
    private GoogleMap googleMap;
    private MainActivity activity;
    private FragmentRouteMapBinding bind;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng dest;
    private Polyline polyline;

    public RouteMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        dest = arguments.getParcelable("DEST");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentRouteMapBinding.inflate(inflater, container, false);
        activity = (MainActivity) getActivity();
        activity.setBottomNavigationBarVisibility(true);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        initAppBar();
        return bind.getRoot();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        // Check if the app has location permissions
        if (activity.hasLocationPermission()) {
            // Permissions are already granted, enable location-related features
            enableMyLocation();
            enableZoomControls();
        }
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                new FetchURL(RouteMapFragment.this).execute(buildUrl(userLocation, dest));
                addMarker(dest);
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(dest);
                builder.include(userLocation);
                LatLngBounds bounds = builder.build();
                int width = getResources().getDisplayMetrics().widthPixels;
                int height = getResources().getDisplayMetrics().heightPixels;
                int padding = (int) (width * 0.3);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                googleMap.animateCamera(cameraUpdate);
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_route);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }
    private void addMarker(LatLng position) {
        BitmapDescriptor customMarkerIcon = getBitmapDescriptorFromVector(R.drawable.ic_marker);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(position)
                .icon(customMarkerIcon);
        Marker marker = googleMap.addMarker(markerOptions);
    }
    private BitmapDescriptor getBitmapDescriptorFromVector(int vectorDrawableResourceId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(requireContext(), vectorDrawableResourceId);
        if (vectorDrawable != null) {
            int width = vectorDrawable.getIntrinsicWidth();
            int height = vectorDrawable.getIntrinsicHeight();

            vectorDrawable.setBounds(0, 0, width, height);

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.draw(canvas);

            return BitmapDescriptorFactory.fromBitmap(bitmap);
        } else {
            return BitmapDescriptorFactory.defaultMarker();
        }
    }
    private void enableMyLocation() {
        if (googleMap != null) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            googleMap.setMyLocationEnabled(true);
            UiSettings uiSettings = googleMap.getUiSettings();
            uiSettings.setMyLocationButtonEnabled(true);
            uiSettings.setMyLocationButtonEnabled(true);
        }
    }

    private void enableZoomControls() {
        if (googleMap != null) {
            UiSettings uiSettings = googleMap.getUiSettings();
            uiSettings.setZoomControlsEnabled(true);
        }
    }

    private String buildUrl(LatLng ori, LatLng dest){
        StringBuilder builder = new StringBuilder();
        builder.append("https://maps.googleapis.com/maps/api/directions/json?");
        builder.append("origin="+ori.latitude+","+ori.longitude);
        builder.append("&destination="+dest.latitude+","+dest.longitude);
        builder.append("&mode=driving");
        builder.append("&key=AIzaSyD-aUMnOMDlC46iPLKPlf5kGTsO6_ZjyWU");
        Log.d(TAG, builder.toString());
        return builder.toString();
    }
    @Override
    public void onTaskDone(List<String> poly) {
        for (String polyline:
             poly) {
            List<LatLng> decodedPath = PolyUtil.decode(polyline);
            googleMap.addPolyline(new PolylineOptions().color(com.google.android.libraries.places.R.color.quantum_lightblue).width(15).addAll(decodedPath));
        }
    }
    private void initAppBar() {
        activity.setSupportActionBar(bind.animToolbar);

        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        bind.animToolbar.setTitle("Show route");


        bind.animToolbar.setNavigationOnClickListener(v -> {
            activity.navController.navigateUp();
        });

        Objects.requireNonNull(bind.animToolbar.getOverflowIcon()).setTint(requireContext().getResources().getColor(R.color.titleTextColor, null));
    }
}