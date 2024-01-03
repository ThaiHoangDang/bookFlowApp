package com.rmit.bookflowapp.Model;

import java.sql.Timestamp;

public abstract class Post implements Comparable<Post> {
    private String content;
    private String userId;
    private Timestamp timestamp;

    public Post() {
        // empty constructor needed for firebase
    }

    public Post(String content, String userId, Timestamp timestamp) {
        this.content = content;
        this.userId = userId;
        this.timestamp = timestamp;
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
