package com.rmit.bookflowapp.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.rmit.bookflowapp.Model.Comment;

import java.util.Map;

public class CommentRepository {
    private static final String COLLECTION_NAME = "comment";
    private static CommentRepository instance;
    private final CollectionReference collection;

    private CommentRepository() {
        collection = FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static CommentRepository getInstance() {
        if (instance == null) {
            instance = new CommentRepository();
        }
        return instance;
    }

    public Task<Void> addComment(Comment comment) {
        return collection.document(comment.getId()).set(comment);
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
