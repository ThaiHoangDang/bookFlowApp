package com.rmit.bookflowapp.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeFormatter {

    public static String formatTimeAgo(Date inputDate) {
        long timeDifferenceMillis = System.currentTimeMillis() - inputDate.getTime();
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeDifferenceMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeDifferenceMillis);
        long hours = TimeUnit.MILLISECONDS.toHours(timeDifferenceMillis);
        long days = TimeUnit.MILLISECONDS.toDays(timeDifferenceMillis);

        if (seconds < 60) {
            return seconds + "s";
        } else if (minutes < 60) {
            return minutes + "m";
        } else if (hours < 24) {
            return hours + "h";
        } else {
            return days + "d";
        }
    }
}

