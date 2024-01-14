package com.rmit.bookflowapp.Model;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class Comment implements Comparable<Comment>, Serializable {
    private String id;
    private String userId;
    private String postId;
    private String content;
    private Long timestamp;

    public Comment() {
        // empty constructor needed for firebase
    }

    public Comment(String id, String userId, String postId, String content, Long timestamp) {
        this.id = id;
        this.userId = userId;
        this.postId = postId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(Comment comment) {
        // compare using timestamps
        // if the string are not equal
        if (this.timestamp.compareTo(comment.timestamp) != 0) {
            // we compare string values
            return this.timestamp.compareTo(comment.timestamp);
        }
        else {
            // we compare int values
            // if the strings are equal
            return this.content.compareTo(comment.content);
        }
    }
}
