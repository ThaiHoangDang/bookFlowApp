package com.example.cleanconnect.ui.fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cleanconnect.R;
import com.example.cleanconnect.databinding.FragmentCreateSiteBinding;
import com.example.cleanconnect.interfaces.CreateSiteClickCallback;
import com.example.cleanconnect.model.Site;
import com.example.cleanconnect.model.User;
import com.example.cleanconnect.ui.activity.MainActivity;
import com.example.cleanconnect.util.ImageLoaderUtils;
import com.example.cleanconnect.util.RandomStringGenerator;
import com.example.cleanconnect.viewmodel.AllSitesViewModel;
import com.example.cleanconnect.viewmodel.MapViewModel;
import com.example.cleanconnect.viewmodel.OwnedSitesViewModel;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.LocationBias;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class CreateSiteFragment extends Fragment implements CreateSiteClickCallback {

    private MainActivity activity;
    private Uri imageUri;
    private FragmentCreateSiteBinding binding;
    private Place selectedPlace;
    private Date selectedDate;
    private FirebaseFirestore firestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageLoaderUtils loaderUtils;
    private SimpleDateFormat dateFormat;
    private FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreateSiteBinding.inflate(inflater, container, false);
        activity = (MainActivity) getActivity();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        loaderUtils = new ImageLoaderUtils();
        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        Places.initialize(requireContext(), "AIzaSyD-aUMnOMDlC46iPLKPlf5kGTsO6_ZjyWU");
        PlacesClient placesClient = Places.createClient(requireContext());
        // Set up the AutoCompleteTextView
        initAppBar();
        initAutoCompleteTextView();
        initCreateView();
        return binding.getRoot();
    }

    private void initAutoCompleteTextView() {
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // Handle the selected place, which includes LatLng
                selectedPlace = place;
            }

            @Override
            public void onError(@NonNull Status status) {
                // Handle error
                Log.e("ok", "onError: " + status);
            }
        });
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    // Create a LatLng object from the user's current location
                    RectangularBounds bounds = RectangularBounds.newInstance(
                            new LatLng(location.getLatitude(), location.getLongitude()), // Southwest corner
                            new LatLng(location.getLatitude(), location.getLongitude())  // Northeast corner
                    );

                    // Set the location bias to the user's current location
                    autocompleteFragment.setLocationBias(bounds);
                }
            }
        });
    }

    private void initCreateView() {
        binding.buttonPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePickerFragment = new DatePickerFragment(CreateSiteFragment.this);
                datePickerFragment.show(activity.getSupportFragmentManager(), "datePicker");
            }
        });
        binding.buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });
        binding.buttonCreateSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateInput()) {
                    return;
                }
                activity.setProgressVisibility(true);
                User user = activity.getUser();
                StorageReference imageRef = storageReference.child("sites/" + RandomStringGenerator.generateRandomString(10));
                UploadTask uploadTask = imageRef.putFile(imageUri);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(activity, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    firestore.collection("sites").add(new Site(
                                                    "",
                                                    user.getUid(),
                                                    binding.editTextTitle.getText().toString(),
                                                    binding.editTextLocationDetails.getText().toString(),
                                                    dateFormat.format(selectedDate),
                                                    Collections.emptyList(),
                                                    0,
                                                    selectedPlace.getLatLng().latitude,
                                                    selectedPlace.getLatLng().longitude,
                                                    selectedPlace.getAddress(),
                                                    task.getResult().toString()
                                            ))
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    OwnedSitesViewModel ownedSitesViewModel = new ViewModelProvider(requireActivity()).get(OwnedSitesViewModel.class);
                                                    ownedSitesViewModel.refresh();
                                                    AllSitesViewModel allSitesViewModel = new ViewModelProvider(requireActivity()).get(AllSitesViewModel.class);
                                                    allSitesViewModel.refresh();
                                                    MapViewModel mapViewModel = new ViewModelProvider(requireActivity()).get(MapViewModel.class);
                                                    mapViewModel.refresh();
                                                    activity.setProgressVisibility(false);
                                                    activity.navController.navigateUp();
                                                    Toast.makeText(getContext(), "Site Created", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    activity.setProgressVisibility(false);
                                                    activity.navController.navigateUp();
                                                    Toast.makeText(getContext(), "Site created failed. Please try again", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void onSetDate(Date date) {
        selectedDate = date;
        binding.buttonPickDate.setText(dateFormat.format(date));
        Toast.makeText(getContext(), selectedDate.toString(), Toast.LENGTH_SHORT).show();
    }

    private void initAppBar() {
        activity.setSupportActionBar(binding.animToolbar);

        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        binding.animToolbar.setTitle("Create Site");


        binding.animToolbar.setNavigationOnClickListener(v -> {
            Log.d("ok", "clicked");
            activity.navController.navigateUp();
        });

        Objects.requireNonNull(binding.animToolbar.getOverflowIcon()).setTint(requireContext().getResources().getColor(R.color.titleTextColor, null));
    }

    private boolean validateInput() {
        if (selectedPlace == null) {
            Toast.makeText(getContext(), "Please specify an address", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.editTextTitle.getText().toString().isEmpty()) {
            binding.editTextTitle.setError("Required");
            return false;
        }
        if (binding.editTextLocationDetails.getText().toString().isEmpty()) {
            binding.editTextLocationDetails.setError("Required");
            return false;
        }
        if (selectedDate == null) {
            binding.buttonPickDate.setError("Required");
            return false;
        }
        if (imageUri == null) {
            Toast.makeText(getContext(), "Please upload an image", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void openImagePicker() {
        // Open image picker
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the selected image URI
            imageUri = data.getData();
            loaderUtils.loadImage(activity, binding.imageView2, imageUri);
        }
    }
}
