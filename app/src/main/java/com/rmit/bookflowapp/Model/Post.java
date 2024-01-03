package com.rmit.bookflowapp.Model;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public abstract class Post implements Comparable<Post>, Serializable {
    private String id;
    private String title;
    private String content;
    private String userId;
    private String bookId;
    private Timestamp timestamp;

    public Post() {
        // empty constructor needed for firebase
    }

    public Post(String id, String title, String content, String userId, String bookId, Timestamp timestamp) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.bookId = bookId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
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
