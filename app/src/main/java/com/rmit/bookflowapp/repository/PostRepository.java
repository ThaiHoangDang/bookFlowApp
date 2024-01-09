package com.rmit.bookflowapp.repository;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rmit.bookflowapp.Model.Book;
import com.rmit.bookflowapp.Model.Post;
import com.rmit.bookflowapp.Model.Review;
import com.rmit.bookflowapp.Model.User;

import java.util.ArrayList;
import java.util.List;
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

    public Task<QuerySnapshot> getReviewsOfBook(String bookId) {
        return collection
                .whereEqualTo("bookId", bookId)
//                .orderBy("rating")
                .get();
    }

    public Task<List<Review>> getReviewObjectsOfBook(String bookId) {
        List<Task<Review>> reviewTasks = new ArrayList<>();

        // get post for a particular book
        return collection.whereEqualTo("bookId", bookId).get().continueWithTask(queryDocumentSnapshots -> {
            List<Review> allBookReviews = new ArrayList<>();

            // get Book and User
            for (QueryDocumentSnapshot document : queryDocumentSnapshots.getResult()) {
                String postBookId = document.getString("bookId");
                String postUserId = document.getString("userId");

                Task<Review> reviewTask = BookRepository.getInstance().getBookById(postBookId)
                        .continueWithTask(bookTask -> UserRepository.getInstance().getUserById(postUserId)
                                .continueWith(userTask -> {
                                    Review review = document.toObject(Review.class);
                                    review.setBook(bookTask.getResult());
                                    review.setUser(userTask.getResult());

                                    allBookReviews.add(review);
                                    return review;
                                }));

                reviewTasks.add(reviewTask);
            }

            return Tasks.whenAll(reviewTasks).continueWith(task -> allBookReviews);
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
