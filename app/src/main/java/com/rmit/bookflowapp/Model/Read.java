package com.rmit.bookflowapp.Model;

import java.io.Serializable;
import java.util.List;

public class Read extends Post implements Serializable {
    private long readingTime;

    public Read() {
        // empty constructor needed for firebase
    }

    public Read(Long readingTime) {
        this.readingTime = readingTime;
    }

    public Read(String id, String title, String content, User user, Book book, Long timestamp, List<String> likedUsers, Long readingTime) {
        super(id, title, content, user, book, timestamp, likedUsers);
        this.readingTime = readingTime;
    }

    public long getElapsedTime() {
        return readingTime;
    }

    public void setElapsedTime(long readingTime) {
        this.readingTime = readingTime;
    }
}
