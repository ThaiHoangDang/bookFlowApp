package com.example.cleanconnect.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cleanconnect.R;
import com.example.cleanconnect.model.Site;
import com.example.cleanconnect.model.User;
import com.example.cleanconnect.repository.SiteRepository;
import com.example.cleanconnect.util.LocationUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class JoinedSitesViewModel extends ViewModel {
    private final String TAG = "AllSitesViewModel";
    private final SiteRepository siteRepository;
    private final MutableLiveData<List<Site>> allSitesLiveData = new MutableLiveData<>();
    private String currentFilterCriteria = ""; // Default to no filter
    private Double maxDistance;
    private String currentSortCriteria = "";
    private LatLng userLocation;
    private User user;

    public JoinedSitesViewModel() {
        this.siteRepository = new SiteRepository();
        loadAllSites(); // Load all sites initially
    }

    public LiveData<List<Site>> getAllSitesLiveData() {
        return allSitesLiveData;
    }

    public void applyFilter(Double maxDistance, String sortCriteria) {
        this.maxDistance = maxDistance;
        currentSortCriteria = sortCriteria;
        loadFilteredSites();
    }

    private void loadAllSites() {
        siteRepository.getAllSites().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Site> allSites = new ArrayList<>();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Site site = document.toObject(Site.class);
                site.setSiteId(document.getId()); // Set the document ID manually
                allSites.add(site);
            }
            allSites = allSites
                    .stream().filter(site -> site.getJoinedUserIds().contains(user.getUid()))
                    .collect(Collectors.toList());
            filterAndSetSites(allSites);
            Log.d(TAG, "Loaded all sites");
        });
    }

    private void loadFilteredSites() {
        siteRepository.getAllSites().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Site> allSites = new ArrayList<>();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Site site = document.toObject(Site.class);
                site.setSiteId(document.getId()); // Set the document ID manually
                allSites.add(site);
            }
            filterAndSetSites(allSites);
            Log.d(TAG, "Loaded filtered sites");
        });
    }

    private void filterAndSetSites(List<Site> allSites) {
        // Apply filtering logic based on currentFilterCriteria
        List<Site> filteredSites = Collections.emptyList();
        if (currentSortCriteria.equals("Title")) {
            filteredSites = allSites.stream()
                    .sorted(Comparator.comparing(Site::getTitle))
                    .collect(Collectors.toList());
        } else if (currentSortCriteria.equals("Distance")) {
            filteredSites = allSites.stream()
                    .sorted(Comparator.comparing(site -> LocationUtils.calculateHaversineDistance(userLocation, new LatLng(site.getLatitude(), site.getLongitude()))))
                    .collect(Collectors.toList());
        } else {
            filteredSites = allSites;
        }
        if (maxDistance != null && maxDistance > 0){
            filteredSites = filteredSites.stream().filter(site -> LocationUtils.calculateHaversineDistance(userLocation, new LatLng(site.getLatitude(), site.getLongitude())) <= maxDistance )
                    .collect(Collectors.toList());
        }
        allSitesLiveData.setValue(filteredSites);
    }
    public void setUserLocation(LatLng userLocation) {
        this.userLocation = userLocation;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void refresh(){
        loadAllSites();
    }
}
