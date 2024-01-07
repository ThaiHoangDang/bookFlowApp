package com.rmit.bookflowapp.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.rmit.bookflowapp.Model.Post;

import java.util.Map;

public class PostRepository {
    private static final String COLLECTION_NAME = "post";

    private static PostRepository instance;
    private final CollectionReference collection;

    private PostRepository() {
        collection = FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static PostRepository getInstance() {
        if (instance == null) {
            instance = new PostRepository();
        }
        return instance;
    }

    public Task<DocumentReference> addPost(Post post) {
        return collection.add(post);
    }

    public Task<Void> updatePost(Map<String, Object> field, String postId) {
        return collection.document(postId).update(field);
    }

    public Task<Void> deletePost(String postId) {
        return collection.document(postId).delete();
    }

    public Task<Post> getPost(String postId) {
        return collection.document(postId).get().continueWith(task -> {
            if (task.isSuccessful()) {
                return task.getResult().toObject(Post.class);
            } else {
                throw task.getException();
            }
        });
    }

    public Task<QuerySnapshot> getAllPosts() {
        return collection.get();
    }

    // get only Reviews that has the field of "rating"
    public Task<QuerySnapshot> getAllReviewPosts() {
        return collection.orderBy("rating").get();
    }

    // get only Lends that has the field of "location"
    public Task<QuerySnapshot> getAllLendPosts() {
        return collection.orderBy("location").get();
    }

    // get posts limited by number
    public Task<QuerySnapshot> getPosts(int limit) {
        return collection.limit(limit).get();
    }
}
