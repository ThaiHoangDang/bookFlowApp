package com.rmit.bookflowapp.Ultilities;

import android.content.Context;
import android.location.Geocoder;
import android.text.format.DateFormat;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.Locale;

public class Helper {
    // https://www.timestamp-converter.com/
    public static String convertTime(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("dd/MM/yyyy", cal).toString(); // no hh:mm:ss
        return date;
    }

    // use Geocoder to convert LatLng object to human-readable address
    public static String convertLatLng(Context context, LatLng latLng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String address = "";
        try {
            address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getAddressLine(0);
            // remove the country name
            String[] addressParts = address.split(",");
            address = addressParts[0].trim() + ", " + addressParts[1].trim();

            // found, return string with the address short form
            return address;
        } catch (Exception e) {
            e.printStackTrace();
        }

        // not found, return empty string
        return address;
    }
}
