package com.rmit.bookflowapp.Ultilities;

import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;

public class Helper {
    // https://www.timestamp-converter.com/
    public static String convertTime(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString(); // no hh:mm:ss
        return date;
    }
}
