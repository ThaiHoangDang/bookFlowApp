package com.rmit.bookflowapp.repository;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
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
import java.util.UUID;

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

//    public Task<Post> getPost(String postId) {
//        return collection.document(postId).get().continueWith(task -> {
//            if (task.isSuccessful()) {
//                Post post = task.getResult().toObject(Post.class);
//                // Assuming setId() is a method in your Post class
//                post.setId(postId);
//                return post;
//            } else {
//                throw task.getException();
//            }
//        });
//    }

    public Task<Post> getPost(String postId) {
        return collection.document(postId).get().continueWithTask(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                String postBookId = document.getString("bookId");
                String postUserId = document.getString("userId");

                Task<Book> bookTask = BookRepository.getInstance().getBookById(postBookId);
                Task<User> userTask = UserRepository.getInstance().getUserById(postUserId);

                return Tasks.whenAll(bookTask, userTask).continueWithTask(task1 -> {
                    Post post = document.toObject(Post.class);
                    // Assuming setId() is a method in your Post class
                    post.setId(postId);
                    post.setBook(bookTask.getResult());
                    post.setUser(userTask.getResult());
                    return Tasks.forResult(post);
                });
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
                                    review.setId(document.getId());

                                    allBookReviews.add(review);
                                    return review;
                                }));

                reviewTasks.add(reviewTask);
            }

            return Tasks.whenAll(reviewTasks).continueWith(task -> allBookReviews);
        });
    }

    public Task<Void> addReview(Map<String, Object> review) {
        String id = UUID.randomUUID().toString();

        return collection.document(id).set(review);
    }

    public Task<Void> updateReview(String id, Map<String, Object> review) {
        return collection.document(id).set(review);
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

    public void addLikedUser(String postId, String userId) {
        // Use FieldValue.arrayUnion to add the bookId to the favorites list without duplicates
        collection.document(postId).update("likedUsers", FieldValue.arrayUnion(userId));
    }

    public void
    removeLikedUser(String postId, String userId) {
        // Use FieldValue.arrayRemove to remove the bookId from the favorites list
        collection.document(postId).update("likedUsers", FieldValue.arrayRemove(userId));
    }
}
