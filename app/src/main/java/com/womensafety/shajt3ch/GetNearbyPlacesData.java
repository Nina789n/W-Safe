package com.womensafety.shajt3ch;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

interface RouteCallback {
    void onNearestLocationFound(LatLng location, String placeType);
}



public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {

    String googlePlacesData;
    GoogleMap mMap;
    String url;
    String placeType;
    RouteCallback callback;
    private static int totalMarkersAdded = 0;

    @Override
    protected String doInBackground(Object... params) {
        try {
            Log.d("GetNearbyPlacesData", "doInBackground entered");
            mMap = (GoogleMap) params[0];
            url = (String) params[1];
            placeType = (String) params[2];
            if (params.length > 3) {
                callback = (RouteCallback) params[3];
            }
            DownloadUrl downloadUrl = new DownloadUrl();
            googlePlacesData = downloadUrl.readUrl(url);
            Log.d("GooglePlacesReadTask", "doInBackground Exit");
        } catch (Exception e) {
            Log.d("GooglePlacesReadTask", e.toString());
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result == null || result.isEmpty()) {
            android.util.Log.e("GetNearbyPlacesData", "No result from API for " + placeType);
            return;
        }
        
        // Check for billing error
        if (result.contains("Billing") || result.contains("billing")) {
            android.util.Log.e("GetNearbyPlacesData", "Billing error detected");
            return;
        }
        
        android.util.Log.d("GetNearbyPlacesData", "Got result for " + placeType);
        
        DataParser dataParser = new DataParser();
        List<HashMap<String, String>> nearbyPlacesList = dataParser.parse(result);
        
        if (nearbyPlacesList != null && !nearbyPlacesList.isEmpty()) {
            android.util.Log.d("GetNearbyPlacesData", "Found " + nearbyPlacesList.size() + " places");
            int added = ShowNearbyPlaces(nearbyPlacesList);
            android.util.Log.d("GetNearbyPlacesData", "Added " + added + " markers to map");
        } else {
            android.util.Log.e("GetNearbyPlacesData", "No places found for " + placeType);
        }
    }

    private int ShowNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {
        if (nearbyPlacesList == null || nearbyPlacesList.isEmpty() || mMap == null) {
            android.util.Log.e("ShowNearbyPlaces", "Cannot show places - empty list or null map");
            return 0;
        }
        
        int count = 0;
        for (HashMap<String, String> googlePlace : nearbyPlacesList) {
            try {
                double lat = Double.parseDouble(googlePlace.get("lat"));
                double lng = Double.parseDouble(googlePlace.get("lng"));
                String placeName = googlePlace.get("place_name");
                String vicinity = googlePlace.get("vicinity");
                
                if (placeName == null || placeName.equals("-NA-")) continue;
                
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(lat, lng));
                
                if (placeType.equals("police")) {
                    markerOptions.title("🚔 " + placeName);
                    markerOptions.snippet("Police Station - " + (vicinity != null ? vicinity : ""));
                } else if (placeType.equals("hospital")) {
                    markerOptions.title("🏥 " + placeName);
                    markerOptions.snippet("Hospital - " + (vicinity != null ? vicinity : ""));
                } else {
                    markerOptions.title(placeName);
                    markerOptions.snippet(vicinity != null ? vicinity : "");
                }
                
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(getMarkerColor(placeType)));
                Marker marker = mMap.addMarker(markerOptions);
                if (marker != null) {
                    count++;
                    android.util.Log.d("ShowNearbyPlaces", "Added marker: " + placeName + " at " + lat + "," + lng);
                } else {
                    android.util.Log.e("ShowNearbyPlaces", "Failed to add marker for: " + placeName);
                }
            } catch (Exception e) {
                android.util.Log.e("ShowNearbyPlaces", "Error adding marker: " + e.getMessage());
            }
        }
        android.util.Log.d("ShowNearbyPlaces", "Total markers added: " + count);
        return count;
    }
    
    private float getMarkerColor(String placeType) {
        switch (placeType) {
            case "police":
                return BitmapDescriptorFactory.HUE_BLUE;
            case "hospital":
                return BitmapDescriptorFactory.HUE_GREEN;
            default:
                return BitmapDescriptorFactory.HUE_RED;
        }
    }
}