package com.rmit.bookflowapp.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.rmit.bookflowapp.Model.Book;

import java.util.List;

public class BookRepository {
    private static final String COLLECTION_NAME = "book";
    private static BookRepository instance;
    private final CollectionReference bookCollection;

    private BookRepository() {
        bookCollection = FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static BookRepository getInstance() {
        if (instance == null) {
            instance = new BookRepository();
        }
        return instance;
    }

    public Task<List<Book>> getBookForLibraryFragment() {
        Query query = bookCollection.limit(10);

        return query.get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        List<Book> books = task.getResult().toObjects(Book.class);
                        return books;
                    } else {
                        Exception exception = task.getException();
                        return null;
                    }
                });
    }

//    public Task<List<Book>> getBookByQuery(String query) {
//        Query searchQuery = bookCollection.
//    }
}
