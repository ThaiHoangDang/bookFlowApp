package com.example.cleanconnect.ui.fragment;

import static com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.cleanconnect.R;
import com.example.cleanconnect.databinding.FragmentMapBinding;
import com.example.cleanconnect.model.Site;
import com.example.cleanconnect.repository.SiteRepository;
import com.example.cleanconnect.ui.activity.MainActivity;
import com.example.cleanconnect.viewmodel.MapViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Objects;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private final String TAG = "MapFragment";
    private GoogleMap googleMap;
    private MainActivity activity;
    private AppBarLayout appBarLayout;
    private FragmentMapBinding bind;
    private MaterialToolbar materialToolbar;
    private MapViewModel mapViewModel;
    private FusedLocationProviderClient fusedLocationClient;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentMapBinding.inflate(inflater, container, false);
        activity = (MainActivity) getActivity();
        activity.setBottomNavigationBarVisibility(true);
        mapViewModel = new ViewModelProvider(requireActivity()).get(MapViewModel.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        initAppBar();

        return bind.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;  // Use the default InfoWindow layout
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate your custom layout
                View view = getLayoutInflater().inflate(R.layout.custom_info_window, null);

                // Customize the layout content based on the marker details
                TextView textTitle = view.findViewById(R.id.textTitle);
                textTitle.setText(marker.getTitle());

                // Set the marker as the tag of the "View more detail" button
                Button btnViewDetail = view.findViewById(R.id.btnViewDetail);
                btnViewDetail.setTag(marker);

                return view;
            }
        });
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                // Retrieve the ID from the marker's tag
                Site site = (Site) marker.getTag();
                Bundle bundle = new Bundle();
                bundle.putParcelable("SITE_OBJECT", site);
                activity.navController.navigate(R.id.siteDisplayFragment, bundle);
                }
            }
        );
        // Check if the app has location permissions
        if (activity.hasLocationPermission()) {
            // Permissions are already granted, enable location-related features
            enableMyLocation();
            enableZoomControls();
        }

        // Move camera to a default location (e.g., the first site)
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mapViewModel.getAllSitesLiveData().observe(getViewLifecycleOwner(), new Observer<List<Site>>() {
            @Override
            public void onChanged(List<Site> sites) {
                Log.d(TAG, "Changed");
                googleMap.clear();
                generateMarkers(sites);
            }
        });
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        Log.d(TAG, userLocation.toString());
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,10));
                    }
                });
    }

    private Marker addMarker(LatLng position, String title, String snippet) {
        BitmapDescriptor customMarkerIcon = getBitmapDescriptorFromVector(R.drawable.ic_marker);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(position)
                .icon(customMarkerIcon)
                .title(title)
                .snippet(snippet);
        return googleMap.addMarker(markerOptions);
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

    private void initAppBar() {
        appBarLayout = bind.getRoot().findViewById(R.id.toolbar_fragment);
        materialToolbar = bind.getRoot().findViewById(R.id.toolbar);

        activity.setSupportActionBar(materialToolbar);
        Objects.requireNonNull(materialToolbar.getOverflowIcon()).setTint(requireContext().getResources().getColor(R.color.titleTextColor, null));
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
    private void generateMarkers(List<Site> siteList) {
        if (googleMap != null) {
            for (Site site : siteList) {
                Marker marker = addMarker(new LatLng(site.getLatitude(), site.getLongitude()), site.getTitle(), site.getLocationDetails());
                marker.setTag(site);
            }
        }
    }
}
