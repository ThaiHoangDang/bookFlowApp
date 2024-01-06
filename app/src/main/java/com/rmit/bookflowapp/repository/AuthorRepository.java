package com.rmit.bookflowapp.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.rmit.bookflowapp.Model.Author;

import java.util.Map;

public class AuthorRepository {
    private static final String COLLECTION_NAME = "author";
    private static AuthorRepository instance;
    private final CollectionReference collection;

    private AuthorRepository() {
        collection = FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static AuthorRepository getInstance() {
        if (instance == null) {
            instance = new AuthorRepository();
        }
        return instance;
    }

    public Task<DocumentReference> addAuthor(Author author) {
        return collection.add(author);
    }

    public Task<Void> updateAuthor(Map<String, Object> field, String authorId) {
        return collection.document(authorId).update(field);
    }

    public Task<Void> deleteAuthor(String authorId) {
        return collection.document(authorId).delete();
    }

    public Task<Author> getAuthor(String authorId) {
        return collection.document(authorId).get().continueWith(task -> {
            if (task.isSuccessful()) {
                return task.getResult().toObject(Author.class);
            } else {
                throw task.getException();
            }
        });
    }

    public Task<QuerySnapshot> getAllAuthors() {
        return collection.get();
    }
}
