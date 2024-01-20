package com.rmit.bookflowapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.firebase.encoders.annotations.Encodable;
import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Chat implements Serializable, Comparable<Chat>, Parcelable {
    @Exclude
    private String chatId;
    private List<String> userId;
    private List<Message> messages;
    private Timestamp lastModified;

    protected Chat(Parcel in) {
        chatId = in.readString();
        userId = in.createStringArrayList();
        lastModified = in.readParcelable(Timestamp.class.getClassLoader());
    }

    public static final Creator<Chat> CREATOR = new Creator<Chat>() {
        @Override
        public Chat createFromParcel(Parcel in) {
            return new Chat(in);
        }

        @Override
        public Chat[] newArray(int size) {
            return new Chat[size];
        }
    };

    @Override
    public int compareTo(Chat o) {
        return o.getLastModified().compareTo(this.getLastModified());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(chatId);
        dest.writeStringList(userId);
        dest.writeParcelable(lastModified, flags);
    }

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
            return this.timestamp.compareTo(o.getTimestamp());
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
        Collections.sort(messages);
        return messages;
    }

    public void setMessages(List<Message> message) {
        this.messages = message;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "chatId='" + chatId + '\'' +
                ", userId=" + userId +
                ", messages=" + messages +
                ", lastModified=" + lastModified +
                '}';
    }
}
