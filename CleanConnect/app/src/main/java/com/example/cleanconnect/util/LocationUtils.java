package com.example.cleanconnect.util;

import com.google.android.gms.maps.model.LatLng;

public class LocationUtils {

    private static final int EARTH_RADIUS_KM = 6371;

    public static double calculateHaversineDistance(LatLng location1, LatLng location2) {
        // Convert latitude and longitude from degrees to radians
        double radLat1 = Math.toRadians(location1.latitude);
        double radLon1 = Math.toRadians(location1.longitude);
        double radLat2 = Math.toRadians(location2.latitude);
        double radLon2 = Math.toRadians(location2.longitude);

        // Calculate differences
        double deltaLat = radLat2 - radLat1;
        double deltaLon = radLon2 - radLon1;

        // Haversine formula
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(radLat1) * Math.cos(radLat2) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Calculate distance
        return EARTH_RADIUS_KM * c;
    }
}

