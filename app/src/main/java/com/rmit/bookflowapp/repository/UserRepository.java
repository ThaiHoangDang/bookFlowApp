package com.rmit.bookflowapp.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.rmit.bookflowapp.Model.User;

public class UserRepository {
    private static final String COLLECTION_NAME = "user";
    private static UserRepository instance;
    private final CollectionReference userCollection;

    private UserRepository() {
        userCollection = FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public Task<Void> addUser(String userId, User user) {
        return userCollection.document(userId).set(user);
    }

    public Task<Void> updateUser(User user) {
        return userCollection.document(user.getId()).set(user);
    }

    public Task<Void> deleteUser(String userId) {
        return userCollection.document(userId).delete();
    }

    public Task<QuerySnapshot> getAllUsers() {
        return userCollection.get();
    }

    public Task<User> getUserById(String uid) {
        return userCollection.document(uid).get()
                .continueWith(task -> {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        return documentSnapshot.toObject(User.class);
                    } else {
                        return null;
                    }
                });
    }

    public void addToFavorites(String userId, String bookId) {
        // Use FieldValue.arrayUnion to add the bookId to the favorites list without duplicates
        userCollection.document(userId).update("favoriteBooks", FieldValue.arrayUnion(bookId));
    }

    public void
    removeFromFavorites(String userId, String bookId) {
        // Use FieldValue.arrayRemove to remove the bookId from the favorites list
        userCollection.document(userId).update("favoriteBooks", FieldValue.arrayRemove(bookId));
    }

}
