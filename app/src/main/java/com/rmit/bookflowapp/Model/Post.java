package com.rmit.bookflowapp.Model;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class Post implements Comparable<Post>, Serializable {
    private String id;
    private String title;
    private String content;
    private User user;
    private Book book;
    private Long timestamp;

    public Post() {
        // empty constructor needed for firebase
    }

    public Post(String id, String title, String content, User user, Book book, Long timestamp) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.user = user;
        this.book = book;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(Post post) {
        // compare using timestamps
        // if the string are not equal
        if (this.timestamp.compareTo(post.timestamp) != 0) {
            // we compare string values
            return this.timestamp.compareTo(post.timestamp);
        }
        else {
            // we compare int values
            // if the strings are equal
            return this.content.compareTo(post.content);
        }
    }
}
