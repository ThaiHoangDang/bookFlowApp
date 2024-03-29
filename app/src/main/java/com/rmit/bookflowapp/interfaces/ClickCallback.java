package com.rmit.bookflowapp.interfaces;

import android.os.Bundle;

import androidx.annotation.Keep;


@Keep
public interface ClickCallback {
    default void onSiteClick(Bundle bundle) {};
    default void onFilterApply(Double maxDistance, String sortCriteria) {};
    default void onChatClick(Bundle bundle) {};
    default void onUserClick(Bundle bundle) {};
}
