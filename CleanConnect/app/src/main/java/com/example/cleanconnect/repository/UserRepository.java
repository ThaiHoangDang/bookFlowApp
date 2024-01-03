package com.example.cleanconnect.repository;

import com.example.cleanconnect.model.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QuerySnapshot;

public class UserRepository {
    private static final String COLLECTION_NAME = "users"; // Replace with your actual collection name
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final CollectionReference collection = firestore.collection(COLLECTION_NAME);

    public UserRepository() {
        // Constructor
    }

    public Task<DocumentReference> addUser(User user) {
        return collection.add(user);
    }

    public Task<Void> updateUser(User user) {
        return collection.document(user.getUid()).set(user);
    }

    public Task<Void> deleteUser(String userId) {
        return collection.document(userId).delete();
    }

    public Task<User> getUser(String userId) {
        return collection.document(userId).get().continueWith(task -> {
            if (task.isSuccessful()) {
                User user = task.getResult().toObject(User.class);
                user.setUid(userId);
                return user;
            } else {
                throw task.getException();
            }
        });
    }
    public Task<QuerySnapshot> getAllUsers() {
        return collection.get();
    }
    public Task<AuthResult> signIn(String email, String password){
        return firebaseAuth.signInWithEmailAndPassword(email, password);
    }
    public Task<Boolean> loggedIn(){
        if (firebaseAuth.getCurrentUser().isAnonymous()){
            return Tasks.forResult(true);
        }
        return Tasks.forResult(false);
    }
}
