package com.example.faheemtechnerd.mapsampleapp;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.faheemtechnerd.mapsampleapp.models.MyMarker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import helpingclasses.DistanceCalculator;

import static helpingclasses.Constant.CONNECTION_FAILURE_RESOLUTION_REQUEST;
import static helpingclasses.Constant.FASTEST_INTERVAL;
import static helpingclasses.Constant.REQUEST_PERMISSION_LOCATION;
import static helpingclasses.Constant.UPDATE_INTERVAL;


public class MapsActivity extends FragmentActivity implements Button.OnClickListener,OnMapReadyCallback,LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private ArrayList<Marker> addedMarkerList;
    private ArrayList<MyMarker> myMarkers;
    private Location myCurrentLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        addedMarkerList = new ArrayList<Marker>();
        myMarkers       = new ArrayList<MyMarker>();
        prepareMarkersData();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ((Button) findViewById(R.id.calulateDistance)).setOnClickListener(this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();

        locationRequest = new LocationRequest();
        // Use high accuracy
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 5 seconds
        locationRequest.setInterval(UPDATE_INTERVAL);
        // Set the fastest update interval to 1 second
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

    }

    @Override
    protected void onResume() {
        super.onResume();
        googleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //putMarkersOnMap(myMarkers);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLastLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("", "Location services connection failed with code " + connectionResult.getErrorCode());
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        processNewLocation(location);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String permissions[],
            int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLastLocation();

                } else {
                    Toast.makeText(MapsActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }


    private void getLastLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_LOCATION);

            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location == null) {
            // Blank for a moment...
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
        else {
            processNewLocation(location);
        };
    }

    private void processNewLocation(Location location) {

        myCurrentLocation = location;
        LatLng latlng = new LatLng(location.getLatitude(),location.getLongitude());
        addMyPositionOnMAp(latlng);
        putMarkersOnMap(myMarkers);

    }

    private void addMyPositionOnMAp(LatLng latLngParam){

        mMap.addMarker(new MarkerOptions().position(latLngParam).title("This is Me"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngParam));

    }


    private void prepareMarkersData(){

        LatLng latLngs[] = {new LatLng(-20.164490, 57.501331),
                new LatLng(-20.166368, 57.500414),
                new LatLng(-20.167363, 57.506858),
                new LatLng(-20.158857, 57.502784),
                new LatLng(-20.157019, 57.508597),
                new LatLng(-20.153177, 57.504198)};

        for(int i = 0 ; i < latLngs.length ; i++ ) {

            MyMarker myMarker = new MyMarker(latLngs[i],"Position"+i);
            myMarkers.add(myMarker);
        }

    }

    private void putMarkersOnMap(ArrayList<MyMarker> markersArray){

        for(int i = 0 ; i < markersArray.size() ; i++ ) {

            Marker temp = createMarker(markersArray.get(i).getLatLng(),markersArray.get(i).getTitle(),"");
            addedMarkerList.add(temp);

        }

    }


    protected Marker createMarker(LatLng latLng, String title, String snippet) {

        return mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet)
                );
    }


    @Override
    public void onClick(View view) {

        if (view.getId()== R.id.calulateDistance){

            DistanceCalculator distanceCalculator = new DistanceCalculator();
            if (myCurrentLocation==null){

                Toast.makeText(MapsActivity.this, " Can't find your location", Toast.LENGTH_SHORT).show();
            }
            else {

                ArrayList<Marker> sortedMarkers   = distanceCalculator.calculateDistance(addedMarkerList,new LatLng(myCurrentLocation.getLatitude(),myCurrentLocation.getLongitude()));
                if (!sortedMarkers.isEmpty()){
                    Toast.makeText(MapsActivity.this, sortedMarkers.get(0).getTitle()+" is nearest location to you", Toast.LENGTH_LONG).show();
                }
                }

            }

        }

}
