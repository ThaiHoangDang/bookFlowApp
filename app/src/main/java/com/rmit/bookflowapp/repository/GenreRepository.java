package com.rmit.bookflowapp.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.rmit.bookflowapp.Model.Genre;

import java.util.Map;

public class GenreRepository {
    private static final String COLLECTION_NAME = "genre";
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final CollectionReference collection = firestore.collection(COLLECTION_NAME);

    public GenreRepository() {
        // Constructor
    }

    public Task<DocumentReference> addGenre(Genre genre) {
        return collection.add(genre);
    }

    public Task<Void> updateGenre(Map<String, Object> field, String genreId) {
        return collection.document(genreId).update(field);
    }

    public Task<Void> deleteGenre(String genreId) {
        return collection.document(genreId).delete();
    }

    public Task<Genre> getGenre(String genreId) {
        return collection.document(genreId).get().continueWith(task -> {
            if (task.isSuccessful()) {
                return task.getResult().toObject(Genre.class);
            } else {
                throw task.getException();
            }
        });
    }
    public Task<QuerySnapshot> getAllGenres() {
        return collection.get();
    }
}
