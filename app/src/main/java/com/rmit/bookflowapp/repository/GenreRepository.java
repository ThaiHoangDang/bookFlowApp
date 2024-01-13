package com.rmit.bookflowapp.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rmit.bookflowapp.Model.Book;
import com.rmit.bookflowapp.Model.Genre;

import java.util.List;
import java.util.Map;

public class GenreRepository {
    private static final String COLLECTION_NAME = "genre";
    private static GenreRepository instance;
    private final CollectionReference collection;

    private GenreRepository() {
        collection = FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static GenreRepository getInstance() {
        if (instance == null) {
            instance = new GenreRepository();
        }
        return instance;
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

    public Task<List<Genre>> getGenreForLibraryFragment() {
        Query query = collection.limit(10);

        return query.get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        List<Genre> genres = task.getResult().toObjects(Genre.class);

                        // Set the id for each genre
                        for (int i = 0; i < genres.size(); i++) {
                            genres.get(i).setId(task.getResult().getDocuments().get(i).getId());
                        }

                        return genres;
                    } else {
                        Exception exception = task.getException();
                        return null;
                    }
                });
    }

    public Task<QuerySnapshot> getAllGenres() {
        return collection.get();
    }
}
