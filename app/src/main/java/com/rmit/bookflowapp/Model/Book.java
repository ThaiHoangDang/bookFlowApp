package com.rmit.bookflowapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

public class Book implements Serializable, Parcelable {
    private String id;
    private String title;
    private List<String> author;
    private List<String> genre;
    private String description;
    private String imageId;

    public Book() {
        // empty constructor needed for firebase
    }

    public Book(String id, String title, List<String> author, List<String> genre, String description, String imageId) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.description = description;
        this.imageId = imageId;
    }

    protected Book(Parcel in) {
        id = in.readString();
        title = in.readString();
        author = in.createStringArrayList();
        genre = in.createStringArrayList();
        description = in.readString();
        imageId = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

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

    public List<String> getAuthor() {
        return author;
    }

    public void setAuthor(List<String> author) {
        this.author = author;
    }

    public List<String> getGenre() {
        return genre;
    }

    public void setGenre(List<String> genre) {
        this.genre = genre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImageUrl() {
        return "https://firebasestorage.googleapis.com/v0/b/striking-water-408603.appspot.com/o/" + getImageId() + "?alt=media";
    }

    public String getAuthorString() {
        StringBuilder authorStringBuilder = new StringBuilder();
        for (int i = 0; i < author.size(); i++) {
            authorStringBuilder.append(author.get(i));
            if (i < author.size() - 1) {
                authorStringBuilder.append(", ");
            }
        }
        return authorStringBuilder.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeStringList(author);
        parcel.writeStringList(genre);
        parcel.writeString(description);
        parcel.writeString(imageId);
    }

    @Override
    public String toString() {
        return "Book{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", author=" + author +
                ", genre=" + genre +
                ", description='" + description + '\'' +
                ", imageId='" + imageId + '\'' +
                '}';
    }
}
