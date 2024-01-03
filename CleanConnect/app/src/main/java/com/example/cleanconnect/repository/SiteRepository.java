package com.example.cleanconnect.repository;

import com.example.cleanconnect.model.Site;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.ref.Reference;
import java.util.Map;

public class SiteRepository {

    private static final String COLLECTION_NAME = "sites"; // Replace with your actual collection name
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final CollectionReference collection = firestore.collection(COLLECTION_NAME);

    public SiteRepository() {
        // Constructor
    }

    public Task<DocumentReference> addSite(Site site) {
        return collection.add(site);
    }

    public Task<Void> updateSite(Map<String, Object> field, String siteId) {
        return collection.document(siteId).update(field);
    }

    public Task<Void> deleteSite(String siteId) {
        return collection.document(siteId).delete();
    }

    public Task<Site> getSite(String siteId) {
        return collection.document(siteId).get().continueWith(task -> {
            if (task.isSuccessful()) {
                return task.getResult().toObject(Site.class);
            } else {
                throw task.getException();
            }
        });
    }
    public Task<QuerySnapshot> getAllSites() {
        return collection.get();
    }
}

