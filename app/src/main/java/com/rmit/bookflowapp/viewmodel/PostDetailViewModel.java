package com.rmit.bookflowapp.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.rmit.bookflowapp.Model.Comment;
import com.rmit.bookflowapp.Model.Review;
import com.rmit.bookflowapp.repository.CommentRepository;
import com.rmit.bookflowapp.repository.PostRepository;

import java.util.ArrayList;
import java.util.List;

public class PostDetailViewModel extends ViewModel {
    private final String TAG = "PostDetailViewModel";
    private final MutableLiveData<List<Comment>> postComments = new MutableLiveData<>();
    private final String postId;

    public PostDetailViewModel(String postId) {
        this.postId = postId;
        fetchPostComments();
    }

    public MutableLiveData<List<Comment>> getPostComments() {
        return postComments;
    }

    public void refreshPostComments() {
        fetchPostComments();
    }

    private void fetchPostComments() {
        CommentRepository.getInstance().getCommentObjectsOfPost(postId)
                .addOnSuccessListener(postComments -> {
                    this.postComments.setValue(postComments);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to fetch post comments", e);
                });
    }
}
