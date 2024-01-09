package com.rmit.bookflowapp.interfaces;

import android.os.Bundle;

public interface ClickCallback {
    default void onChatClick(Bundle bundle) {};
    default void onUserClick(Bundle bundle) {};
}
