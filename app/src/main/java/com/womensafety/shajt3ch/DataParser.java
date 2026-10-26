package com.womensafety.shajt3ch;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DataParser {
    public List<HashMap<String, String>> parse(String jsonData) {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            Log.d("Places", "parse");
            
            // Check if it's Nominatim API response (array of results)
            if (jsonData.trim().startsWith("[")) {
                return parseNominatimData(jsonData);
            }
            
            jsonObject = new JSONObject((String) jsonData);
            
            // Check if it's OpenStreetMap Overpass API response
            if (jsonObject.has("elements")) {
                return parseOverpassData(jsonObject);
            }
            
            // Check for Google API errors
            if (jsonObject.has("error_message")) {
                String errorMsg = jsonObject.getString("error_message");
                Log.e("Places", "API Error: " + errorMsg);
                return new ArrayList<>();
            }
            
            // Google Places API format
            if (jsonObject.has("status")) {
                String status = jsonObject.getString("status");
                Log.d("Places", "API Status: " + status);
                if (!status.equals("OK") && !status.equals("ZERO_RESULTS")) {
                    Log.e("Places", "API returned status: " + status);
                }
            }
            
            if (jsonObject.has("results")) {
                jsonArray = jsonObject.getJSONArray("results");
            }
        } catch (JSONException e) {
            Log.e("Places", "parse error: " + e.getMessage());
            e.printStackTrace();
        }
        return getPlaces(jsonArray);
    }
    
    private List<HashMap<String, String>> parseOverpassData(JSONObject jsonObject) {
        List<HashMap<String, String>> placesList = new ArrayList<>();
        try {
            if (!jsonObject.has("elements")) {
                Log.d("Places", "No elements in Overpass response");
                return placesList;
            }
            
            JSONArray elements = jsonObject.getJSONArray("elements");
            Log.d("Places", "Found " + elements.length() + " elements from Overpass");
            
            for (int i = 0; i < elements.length(); i++) {
                JSONObject element = elements.getJSONObject(i);
                HashMap<String, String> placeMap = new HashMap<>();
                
                // Get coordinates
                double lat = 0, lng = 0;
                if (element.has("lat") && element.has("lon")) {
                    // Node type
                    lat = element.getDouble("lat");
                    lng = element.getDouble("lon");
                } else if (element.has("center")) {
                    // Way type - use center
                    JSONObject center = element.getJSONObject("center");
                    lat = center.getDouble("lat");
                    lng = center.getDouble("lon");
                } else {
                    continue; // Skip if no coordinates
                }
                
                // Get name and details
                String name = "Hospital";
                String vicinity = "";
                if (element.has("tags")) {
                    JSONObject tags = element.getJSONObject("tags");
                    if (tags.has("name")) {
                        name = tags.getString("name");
                    } else if (tags.has("operator")) {
                        name = tags.getString("operator");
                    }
                    
                    // Build address
                    if (tags.has("addr:street")) {
                        vicinity = tags.getString("addr:street");
                    }
                    if (tags.has("addr:city") && vicinity.isEmpty()) {
                        vicinity = tags.getString("addr:city");
                    }
                }
                
                placeMap.put("place_name", name);
                placeMap.put("vicinity", vicinity.isEmpty() ? "Nearby" : vicinity);
                placeMap.put("lat", String.valueOf(lat));
                placeMap.put("lng", String.valueOf(lng));
                
                placesList.add(placeMap);
                Log.d("Places", "Added: " + name + " at " + lat + "," + lng);
            }
        } catch (JSONException e) {
            Log.e("Places", "Error parsing Overpass data: " + e.getMessage());
            e.printStackTrace();
        }
        return placesList;
    }
    
    private List<HashMap<String, String>> parseNominatimData(String jsonData) {
        List<HashMap<String, String>> placesList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            Log.d("Places", "Found " + jsonArray.length() + " results from Nominatim");
            
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject place = jsonArray.getJSONObject(i);
                HashMap<String, String> placeMap = new HashMap<>();
                
                // Get name - try different fields
                String name = place.optString("name", "");
                if (name.isEmpty()) {
                    name = place.optString("display_name", "Hospital");
                }
                
                // Extract just the name part (before comma) if it's a full address
                if (name.contains(",")) {
                    name = name.split(",")[0].trim();
                }
                
                // If still empty, use a default
                if (name.isEmpty()) {
                    name = "Hospital";
                }
                
                double lat = place.getDouble("lat");
                double lon = place.getDouble("lon");
                
                placeMap.put("place_name", name);
                placeMap.put("vicinity", place.optString("display_name", ""));
                placeMap.put("lat", String.valueOf(lat));
                placeMap.put("lng", String.valueOf(lon));
                
                placesList.add(placeMap);
                Log.d("Places", "Added: " + name + " at " + lat + "," + lon);
            }
            Log.d("Places", "Total places parsed: " + placesList.size());
        } catch (JSONException e) {
            Log.e("Places", "Error parsing Nominatim data: " + e.getMessage());
            e.printStackTrace();
        }
        return placesList;
    }

    private List<HashMap<String, String>> getPlaces(JSONArray jsonArray) {
        List<HashMap<String, String>> placesList = new ArrayList<>();
        if (jsonArray == null) return placesList;

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                HashMap<String, String> placeMap = getPlace((JSONObject) jsonArray.get(i));
                if (placeMap != null) {
                    placesList.add(placeMap);
                }
            } catch (JSONException e) {
                // Skip invalid place
            }
        }
        return placesList;
    }

    private HashMap<String, String> getPlace(JSONObject googlePlaceJson) {
        HashMap<String, String> googlePlaceMap = new HashMap<String, String>();
        String placeName = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";

        Log.d("getPlace", "Entered");

        try {
            if (!googlePlaceJson.isNull("name")) {
                placeName = googlePlaceJson.getString("name");
            }
            if (!googlePlaceJson.isNull("vicinity")) {
                vicinity = googlePlaceJson.getString("vicinity");
            }
            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference = googlePlaceJson.getString("reference");
            googlePlaceMap.put("place_name", placeName);
            googlePlaceMap.put("vicinity", vicinity);
            googlePlaceMap.put("lat", latitude);
            googlePlaceMap.put("lng", longitude);
            googlePlaceMap.put("reference", reference);
            Log.d("getPlace", "Putting Places");
        } catch (JSONException e) {
            Log.d("getPlace", "Error");
            e.printStackTrace();
        }
        return googlePlaceMap;
    }
}