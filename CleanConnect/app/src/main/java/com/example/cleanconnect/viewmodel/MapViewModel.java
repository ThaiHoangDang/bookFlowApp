package com.example.cleanconnect.viewmodel;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cleanconnect.model.Site;
import com.example.cleanconnect.repository.SiteRepository;
import com.example.cleanconnect.ui.fragment.MapFragment;
import com.example.cleanconnect.util.LocationUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MapViewModel extends ViewModel {

    private final String TAG = "MapViewModel";
    private final SiteRepository siteRepository;
    private final MutableLiveData<List<Site>> allSitesLiveData = new MutableLiveData<>();

    public MapViewModel() {
        this.siteRepository = new SiteRepository();
        loadAllSites(); // Load all sites initially
    }

    public LiveData<List<Site>> getAllSitesLiveData() {
        return allSitesLiveData;
    }

    private void loadAllSites() {
        siteRepository.getAllSites().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Site> allSites = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Site site = document.toObject(Site.class);
                    site.setSiteId(document.getId()); // Set the document ID manually
                    allSites.add(site);
                }
                allSitesLiveData.setValue(allSites);
            }
        });
    }

    public void filterSites(boolean filterBySomeCriteria) {
        List<Site> allSites = allSitesLiveData.getValue();
        if (allSites == null) {
            return;
        }

        List<Site> filteredSites = new ArrayList<>();
        for (Site site : allSites) {
            // Apply your filtering logic here
            if (filterBySomeCriteria) {
                // Add sites that meet the criteria
                filteredSites.add(site);
            }
        }

        // Update LiveData with filtered data
        allSitesLiveData.setValue(filteredSites);
    }
    public void filterSitesByDistance(double maxDistance, LatLng userLocation) {
        List<Site> allSites = allSitesLiveData.getValue();
        if (allSites == null) {
            return; // No data to filter
        }

        List<Site> filteredSites = new ArrayList<>();
        // Assuming you have the user's current location

        for (Site site : allSites) {
            // Calculate distance between user's location and site's location
            double distance = LocationUtils.calculateHaversineDistance(userLocation, new LatLng(site.getLatitude(), site.getLongitude()));

            // Check if the distance is within the user-specified maximum distance
            if (distance <= maxDistance) {
                filteredSites.add(site);
            }
        }
        Log.d(TAG, "filtered");
        // Update LiveData with filtered data
        allSitesLiveData.setValue(filteredSites);
    }

    public void refresh(){
        loadAllSites();
    }

}
