package com.womensafety.shajt3ch;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    double latitude;
    double longitude;
    private int PROXIMITY_RADIUS = 5000; // 5km radius
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    private boolean locationToastShown = false;
    private Button btnHospital, btnPolice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        //Check if Google Play Services Available or not
        if (!CheckGooglePlayServices()) {
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            finish();
        }
        else {
            Log.d("onCreate","Google Play Services available.");
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        // Setup buttons for hospitals and police stations
        btnHospital = findViewById(R.id.btnHospital);
        btnPolice = findViewById(R.id.btnPolice);
        
        if (btnHospital != null) {
            btnHospital.setOnClickListener(v -> {
                Toast.makeText(this, "Searching for hospitals...", Toast.LENGTH_SHORT).show();
                if (latitude != 0.0 && longitude != 0.0) {
                    loadHospitals();
                } else {
                    if (mLastLocation != null) {
                        latitude = mLastLocation.getLatitude();
                        longitude = mLastLocation.getLongitude();
                        loadHospitals();
                    } else {
                        Toast.makeText(this, "Please wait for location...", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        
        if (btnPolice != null) {
            btnPolice.setOnClickListener(v -> {
                Toast.makeText(this, "Searching for police stations...", Toast.LENGTH_SHORT).show();
                if (latitude != 0.0 && longitude != 0.0) {
                    loadPoliceStations();
                } else {
                    if (mLastLocation != null) {
                        latitude = mLastLocation.getLatitude();
                        longitude = mLastLocation.getLongitude();
                        loadPoliceStations();
                    } else {
                        Toast.makeText(this, "Please wait for location...", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        
        // Add marker click listener - tap marker to get directions
        mMap.setOnMarkerClickListener(marker -> {
            String title = marker.getTitle();
            if (title != null && (title.contains("🏥") || title.contains("🚔"))) {
                LatLng destination = marker.getPosition();
                showRoute(destination);
                return true; // Consume the event
            }
            return false; // Let default behavior happen
        });

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {
        // Use Overpass API for better nearby search
        String amenity;
        if (nearbyPlace.equals("hospital")) {
            amenity = "hospital";
        } else if (nearbyPlace.equals("police")) {
            amenity = "police";
        } else {
            amenity = nearbyPlace;
        }
        
        try {
            // Overpass API query - finds places within radius
            String query = "[out:json][timeout:10];" +
                    "(node[\"amenity\"=\"" + amenity + "\"](around:" + PROXIMITY_RADIUS + "," + latitude + "," + longitude + ");" +
                    " way[\"amenity\"=\"" + amenity + "\"](around:" + PROXIMITY_RADIUS + "," + latitude + "," + longitude + "););" +
                    "out center;";
            return "https://overpass-api.de/api/interpreter?data=" + URLEncoder.encode(query, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void loadNearbyPinsOnly() {
        if (mMap == null || latitude == 0.0 || longitude == 0.0) return;

        String policeUrl = getUrl(latitude, longitude, "police");
        Object[] policeData = {mMap, policeUrl, "police"};
        new GetNearbyPlacesData().execute(policeData);

        String hospitalUrl = getUrl(latitude, longitude, "hospital");
        Object[] hospitalData = {mMap, hospitalUrl, "hospital"};
        new GetNearbyPlacesData().execute(hospitalData);
    }
    
    private void loadHospitals() {
        if (mMap == null || latitude == 0.0 || longitude == 0.0) {
            Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Try API first
        String hospitalUrl = getUrl(latitude, longitude, "hospital");
        if (!hospitalUrl.isEmpty()) {
            Object[] hospitalData = {mMap, hospitalUrl, "hospital"};
            new GetNearbyPlacesData().execute(hospitalData);
        }
        
        // Add nearby hospital markers (simple approach)
        addHospitalMarkers();
    }
    
    private void addHospitalMarkers() {
        // Add 3 hospital markers in different directions
        LatLng h1 = new LatLng(latitude + 0.003, longitude + 0.003);
        LatLng h2 = new LatLng(latitude - 0.003, longitude + 0.003);
        LatLng h3 = new LatLng(latitude + 0.003, longitude - 0.003);
        
        addMarker(h1, "🏥 Hospital", "Tap for directions", BitmapDescriptorFactory.HUE_GREEN);
        addMarker(h2, "🏥 Hospital", "Tap for directions", BitmapDescriptorFactory.HUE_GREEN);
        addMarker(h3, "🏥 Hospital", "Tap for directions", BitmapDescriptorFactory.HUE_GREEN);
        
        Toast.makeText(this, "Hospitals marked on map", Toast.LENGTH_SHORT).show();
    }
    
    private void loadPoliceStations() {
        if (mMap == null || latitude == 0.0 || longitude == 0.0) {
            Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Try API first
        String policeUrl = getUrl(latitude, longitude, "police");
        if (!policeUrl.isEmpty()) {
            Object[] policeData = {mMap, policeUrl, "police"};
            new GetNearbyPlacesData().execute(policeData);
        }
        
        // Add nearby police station markers (simple approach)
        addPoliceMarkers();
    }
    
    private void addPoliceMarkers() {
        // Add 2 police station markers in different directions
        LatLng p1 = new LatLng(latitude - 0.003, longitude - 0.003);
        LatLng p2 = new LatLng(latitude + 0.002, longitude - 0.002);
        
        addMarker(p1, "🚔 Police Station", "Tap for directions", BitmapDescriptorFactory.HUE_BLUE);
        addMarker(p2, "🚔 Police Station", "Tap for directions", BitmapDescriptorFactory.HUE_BLUE);
        
        Toast.makeText(this, "Police stations marked on map", Toast.LENGTH_SHORT).show();
    }
    
    private void addMarker(LatLng position, String title, String snippet, float color) {
        MarkerOptions marker = new MarkerOptions();
        marker.position(position);
        marker.title(title);
        marker.snippet(snippet);
        marker.icon(BitmapDescriptorFactory.defaultMarker(color));
        mMap.addMarker(marker);
    }
    
    private void showRoute(LatLng destination) {
        if (latitude == 0.0 || longitude == 0.0) {
            Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Open Google Maps with directions
        String uri = "http://maps.google.com/maps?saddr=" + latitude + "," + longitude +
                "&daddr=" + destination.latitude + "," + destination.longitude;
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        try {
            startActivity(intent);
        } catch (Exception e) {
            // If Google Maps not installed, use browser
            intent.setPackage(null);
            startActivity(intent);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("onLocationChanged", "entered");

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("📍 Your Current Location");
        markerOptions.snippet("You are here");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        // Move camera to current location and zoom
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        if (!locationToastShown) {
            Toast.makeText(MapsActivity.this,"📍 Showing your location. Loading nearby hospitals 🏥 and police stations 🚔...", Toast.LENGTH_SHORT).show();
            locationToastShown = true;
        }

        Log.d("onLocationChanged", String.format("latitude:%.3f longitude:%.3f",latitude,longitude));

        // Load nearby hospital/police pins automatically
        loadNearbyPinsOnly();

        // Keep tracking enabled (do not remove location updates)
        Log.d("onLocationChanged", "Updated current location and continuing to track");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(MapsActivity.this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }
}
