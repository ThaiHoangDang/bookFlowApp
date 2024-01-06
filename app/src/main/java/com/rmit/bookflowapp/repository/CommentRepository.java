package com.rmit.bookflowapp.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.rmit.bookflowapp.Model.Comment;

import java.util.Map;

public class CommentRepository {
    private static final String COLLECTION_NAME = "comment";
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final CollectionReference collection = firestore.collection(COLLECTION_NAME);

    public CommentRepository() {
        // Constructor
    }

    public Task<DocumentReference> addComment(Comment comment) {
        return collection.add(comment);
    }

    public Task<Void> updateComment(Map<String, Object> field, String commentId) {
        return collection.document(commentId).update(field);
    }

    public Task<Void> deleteComment(String commentId) {
        return collection.document(commentId).delete();
    }

    public Task<Comment> getComment(String commentId) {
        return collection.document(commentId).get().continueWith(task -> {
            if (task.isSuccessful()) {
                return task.getResult().toObject(Comment.class);
            } else {
                throw task.getException();
            }
        });
    }
    public Task<QuerySnapshot> getAllComments() {
        return collection.get();
    }
}
