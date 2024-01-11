package com.rmit.bookflowapp.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.rmit.bookflowapp.Model.Chat;
import com.rmit.bookflowapp.Model.User;

import java.sql.Time;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MessageRepository {
    private static final String COLLECTION_NAME = "messages";
    private static MessageRepository instance;
    private final CollectionReference messageCollection;

    private MessageRepository() {
        messageCollection = FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static MessageRepository getInstance() {
        if (instance == null) {
            instance = new MessageRepository();
        }
        return instance;
    }

    public Task<Chat> getChatById(String id) {
        return messageCollection.document(id).get().continueWith(task -> {
            DocumentSnapshot documentSnapshot = task.getResult();
            if (documentSnapshot.exists()) {
                Chat chat = documentSnapshot.toObject(Chat.class);
                chat.setChatId(documentSnapshot.getId());
                return chat;
            }
            return null;
        });
    }

    public Task<Chat> getChatByUserIds(String uid1, String uid2) {
        return messageCollection
                .whereIn("userId", Arrays.asList(Arrays.asList(uid1,uid2), Arrays.asList(uid2,uid1)))
                .get()
                .continueWith(new Continuation<QuerySnapshot, Chat>() {
                    @Override
                    public Chat then(@NonNull Task<QuerySnapshot> task) throws Exception {
                        if (task.isSuccessful()) {
                            QuerySnapshot snapshots = task.getResult();
                            if (snapshots != null && !snapshots.isEmpty()) {
                                DocumentSnapshot dc = snapshots.getDocuments().get(0);
                                Chat chat = dc.toObject(Chat.class);
                                if (chat != null) {
                                    chat.setChatId(dc.getId());
                                    Log.d("Repo", "Chat Data: " + dc.getData().toString());
                                    return chat;
                                }
                            }
                        } else {
                            // Log an error message if the query was not successful
                            Log.e("Repo", "Error getting documents: ", task.getException());
                        }
                        // If no chat is found or an error occurs, return null or handle accordingly
                        return null;
                    }
                });
    }

    public Task<Chat> createNewChat(List<String> userId) {
        Chat chat = new Chat(null, userId, Collections.EMPTY_LIST, Timestamp.now());
        return messageCollection.add(chat).continueWithTask(ref -> {
            DocumentReference documentReference = ref.getResult();
            return getChatById(documentReference.getId());
        });
    }

}
