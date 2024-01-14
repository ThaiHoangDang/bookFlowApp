package com.rmit.bookflowapp.repository;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rmit.bookflowapp.Model.Comment;
import com.rmit.bookflowapp.Model.Review;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class CommentRepository {
    private String TAG = "Comment Repository";
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

    public Task<Void> addComment(Map<String, Object> comment) {
        return collection.document(Objects.requireNonNull(comment.get("id")).toString()).set(comment);
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

    public Task<List<Comment>> getCommentObjectsOfPost(String postId) {
        List<Task<Comment>> commentTasks = new ArrayList<>();


        // get comments for a particular post
        return collection.whereEqualTo("postId", postId).get().continueWithTask(queryDocumentSnapshots -> {
            List<Comment> allPostComments = new ArrayList<>();

            // get User
            for (QueryDocumentSnapshot document : queryDocumentSnapshots.getResult()) {
                String commentUserId = document.getString("userId");

                Task<Comment> commentTask = UserRepository.getInstance().getUserById(commentUserId)
                                .continueWith(userTask -> {
                                    Log.w(TAG, "DMNCN!");
                                    Comment comment = document.toObject(Comment.class);
                                    Log.w(TAG, "DMHCH!");
                                    comment.setUser(userTask.getResult());
                                    comment.setId(document.getId());
                                    Log.w(TAG, "DMQCQ!");
                                    allPostComments.add(comment);
                                    return comment;
                                });

                commentTasks.add(commentTask);
            }

            return Tasks.whenAll(commentTasks).continueWith(task -> allPostComments);
        });
    }

    public Task<QuerySnapshot> getAllComments() {
        return collection.get();
    }
}
