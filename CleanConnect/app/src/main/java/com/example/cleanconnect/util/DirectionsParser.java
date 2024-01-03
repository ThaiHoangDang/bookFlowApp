package com.example.cleanconnect.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DirectionsParser {

    public static List<String> getPolylinesFromJson(String jsonResponse) {
        List<String> polylines = new ArrayList<>();

        try {
            // Create a JSONObject from the response string
            JSONObject jsonObject = new JSONObject(jsonResponse);

            // Check if the status is OK
            String status = jsonObject.getString("status");
            if (!"OK".equals(status)) {
                // Handle the error case
                return polylines;
            }

            // Get the routes array
            JSONArray routesArray = jsonObject.getJSONArray("routes");
            for (int i = 0; i < routesArray.length(); i++) {
                JSONObject routeObject = routesArray.getJSONObject(i);

                // Get the overview polyline
                JSONObject overviewPolyline = routeObject.getJSONObject("overview_polyline");
                String polyline = overviewPolyline.getString("points");

                polylines.add(polyline);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return polylines;
    }
}

