package com.example.cleanconnect.interfaces;

import android.os.Bundle;

import java.util.Date;

public interface CreateSiteClickCallback {
    default void onSetDate(Date date) {};
}
