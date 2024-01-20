package com.rmit.bookflowapp.repository;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rmit.bookflowapp.Model.Book;
import com.rmit.bookflowapp.Model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public Task<DocumentReference> addBook(Book book) {
        return bookCollection.add(book);
    }

    public Task<Void> updateBook(Map<String, Object> field, String bookId) {
        return bookCollection.document(bookId).update(field);
    }

    public Task<Void> deleteBook(String bookId) {
        return bookCollection.document(bookId).delete();
    }

    public Task<List<Book>> getAllBooks() {
        return bookCollection.get()
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

    public Task<List<Book>> getBooksByIds(List<String> bookIds) {
        // Fetch documents for the specified bookIds
        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
        for (String bookId : bookIds) {
            tasks.add(bookCollection.document(bookId).get());
        }

        // Combine tasks to get a single task for all documents
        Task<List<DocumentSnapshot>> combinedTask = Tasks.whenAllSuccess(tasks);

        return combinedTask.continueWith(task -> {
            if (task.isSuccessful()) {
                List<DocumentSnapshot> snapshots = task.getResult();
                List<Book> books = new ArrayList<>();

                for (DocumentSnapshot snapshot : snapshots) {
                    if (snapshot.exists()) {
                        books.add(snapshot.toObject(Book.class));
                    }
                }

                return books;
            } else {
                Exception exception = task.getException();
                return null;
            }
        });
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

    public Task<List<Book>> getBookByGenreId(String id) {
        return bookCollection.whereArrayContains("genreId", id).get()
                .continueWith(task -> task.getResult().toObjects(Book.class));
    }

    public Task<Book> getBookById(String bid) {
        return bookCollection.document(bid).get()
                .continueWith(task -> {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        return documentSnapshot.toObject(Book.class);
                    } else {
                        return null;
                    }
                });
    }

    public Task<List<Book>> getBookByQuery(String query) {
        return bookCollection.get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        List<Book> allBooks = task.getResult().toObjects(Book.class);
                        List<Book> filteredBooks = new ArrayList<>();

                        for (Book book : allBooks) {
                            if (book.getTitle().toLowerCase().contains(query.toLowerCase())) {
                                filteredBooks.add(book);
                            } else {
                                List<String> authors = book.getAuthor();
                                for (String author : authors) {
                                    if (author.toLowerCase().contains(query.toLowerCase())) {
                                        filteredBooks.add(book);
                                        break;
                                    }
                                }
                            }
                        }

                        return filteredBooks;
                    } else {
                        Exception exception = task.getException();
                        return null;
                    }
                });
    }
}
