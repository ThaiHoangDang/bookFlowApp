package com.rmit.bookflowapp.repository;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.rmit.bookflowapp.Model.Book;
import com.rmit.bookflowapp.Model.User;

import java.util.ArrayList;
import java.util.List;

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

    public void addToFollow(String userId, String target) {
        userCollection.document(userId).update("following", FieldValue.arrayUnion(target));
    }

    public void
    removeFromFollow(String userId, String target) {
        userCollection.document(userId).update("following", FieldValue.arrayRemove(target));
    }

    public Task<List<User>> getUsersByIds(List<String> userIds) {
        // Fetch documents for the specified bookIds
        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
        for (String uid : userIds) {
            tasks.add(userCollection.document(uid).get());
        }

        // Combine tasks to get a single task for all documents
        Task<List<DocumentSnapshot>> combinedTask = Tasks.whenAllSuccess(tasks);

        return combinedTask.continueWith(task -> {
            if (task.isSuccessful()) {
                List<DocumentSnapshot> snapshots = task.getResult();
                List<User> users = new ArrayList<>();

                for (DocumentSnapshot snapshot : snapshots) {
                    if (snapshot.exists()) {
                        User u = snapshot.toObject(User.class);
                        u.setId(snapshot.getId());
                        users.add(u);
                    }
                }

                return users;
            } else {
                Exception exception = task.getException();
                return null;
            }
        });
    }
}
