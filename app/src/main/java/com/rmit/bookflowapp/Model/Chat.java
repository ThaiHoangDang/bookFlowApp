package com.rmit.bookflowapp.Model;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Chat implements Serializable {
    private String chatId;
    private List<String> userId;
    private List<Message> messages;
    private Timestamp lastModified;

    public static class Message implements Comparable<Message> {
        private String sender;
        private String message;
        private Timestamp timestamp;
        private boolean read;

        public Message(){
            sender = "";
            message = "";
            timestamp = new Timestamp(0,0);
            read = false;
        }
        public Message(String sender, String message, Timestamp timestamp, boolean read) {
            this.sender = sender;
            this.message = message;
            this.timestamp = timestamp;
            this.read = read;
        }

        public boolean isRead() {
            return read;
        }

        public void setRead(boolean read) {
            this.read = read;
        }

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Timestamp getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Timestamp timestamp) {
            this.timestamp = timestamp;
        }

        @Override
        public int compareTo(Message o) {
            return o.timestamp.compareTo(this.getTimestamp());
        }

        @Override
        public String toString() {
            return "Message{" +
                    "sender='" + sender + '\'' +
                    ", message='" + message + '\'' +
                    ", timestamp=" + timestamp +
                    ", read=" + read +
                    '}';
        }
    }
    public Chat(){
        messages = new ArrayList<>();
        userId = new ArrayList<>();
        lastModified = new Timestamp(0,0);
    }

    public Chat(String chatId, List<String> userId, List<Message> messages, Timestamp lastModified) {
        this.chatId = chatId;
        this.userId = userId;
        this.messages = messages;
        this.lastModified = lastModified;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public Timestamp getLastModified() {
        return lastModified;
    }

    public void setLastModified(Timestamp lastModified) {
        this.lastModified = lastModified;
    }

    public List<String> getUserId() {
        return userId;
    }

    public void setUserId(List<String> userId) {
        this.userId = userId;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> message) {
        this.messages = message;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "userId=" + userId +
                ", messages=" + messages +
                ", lastModified=" + lastModified +
                '}';
    }
}
