package com.example.cleanconnect.interfaces;
import android.os.Bundle;

import androidx.annotation.Keep;

@Keep
public interface ClickCallBack {
    default void onSiteClick(Bundle bundle) {};

    default void onFilterApply(Double maxDistance, String sortCriteria) {};
}
