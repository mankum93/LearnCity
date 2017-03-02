package com.learncity.learner.search;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.learncity.learncity.R;
import com.learncity.searchApi.model.SearchTutorsQuery;
import com.learncity.searchApi.model.TutorProfileResponseView;
import com.learncity.util.ArraysUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by DJ on 10/18/2016.
 */

public class SearchActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String TAG = "SearchActivity";
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0X00;

    private GoogleMap mMap;
    private SearchTutorsQuery mSearchQuery;
    private GoogleApiClient mGoogleApiClient;

    private Location mLastLocation;

    boolean mPermissionToAcessLocationGranted = false;

    private LocationRequest mLocationRequest;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if(areGooglePlayServicesAvailable()){
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }
        }
        else {
            Log.d("TAG", "Google Play services are not available");
            //TODO: Use Android Location Services
        }

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        setContentView(R.layout.activity_search_tutors);

        //FOLLOWING CODE HAS BEEN REPLACED WITH INSERTION OF FRAGMENTS IN THE ACTIVITY LAYOUT FILE

        //Add fragment transactions for the three search parameters
        /*
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        SubjectSearchFragment subjectSearchFragment = new SubjectSearchFragment();
        QualificationSearchFragment qualificationSearchFragment = new QualificationSearchFragment();
        ft.add(R.id.search_activity_fragment_container, subjectSearchFragment, "subjectSearchFragment")
          .add(R.id.search_activity_fragment_container, qualificationSearchFragment, "qualificationSearchFragment");
        ft.commit();
        */

        //Initialize the query
        mSearchQuery =  new SearchTutorsQuery();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_search_fragment);
        mapFragment.getMapAsync(this);

        final SubjectSearchFragment subjectSearchFragment = (SubjectSearchFragment) getSupportFragmentManager().findFragmentById(R.id.subject_search_fragment);

        final QualificationSearchFragment qualificationSearchFragment = (QualificationSearchFragment) getSupportFragmentManager().findFragmentById(R.id.qualification_search_fragment);


        //Lets handle the click of the floating search button
        Button searchButton = (Button) findViewById(R.id.search_button);

        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Search the Tutor profiles from the DB.
                //Build and retrieve the search query
                List<String> subjects = Arrays.asList(ArraysUtil.convertStringToArray(subjectSearchFragment.getCustomMultiAutoCompleteTextView().getText().toString(), ", "));
                mSearchQuery.setSubjects(subjects);
                Log.i(TAG, "Subjects: " + subjects);
                List<String> tutorTypes = Arrays.asList(ArraysUtil.convertStringToArray(qualificationSearchFragment.getCustomMultiAutoCompleteTextView().getText().toString(), ", "));
                Log.i(TAG, "Tutor types: " + tutorTypes);
                mSearchQuery.setTutorTypes(tutorTypes);

                TutorProfileResponseView spec = new TutorProfileResponseView();
                spec.setGlobal(1);
                mSearchQuery.setTutorProfileResponseView(spec);
                new SearchTutorsAsyncTask().execute(mSearchQuery);
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        mGoogleApiClient.connect();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }
    @Override
    protected void onStop() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }
    private boolean areGooglePlayServicesAvailable() {
        int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (errorCode != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(errorCode, this, 0).show();
            return false;
        }
        return true;
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Ask for explicit permission to access fine location for above Lollipop
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP){
            askForLocationPermission();
            if(mPermissionToAcessLocationGranted){
                mMap.setMyLocationEnabled(true);
            }
        } else{
            mMap.setMyLocationEnabled(true);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mPermissionToAcessLocationGranted = true;
                    Log.d(TAG, "Permission to access fine location granted");


                } else {
                    mPermissionToAcessLocationGranted = false;
                    Log.d(TAG, "Permission to access fine location denied");
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "Connected to the Google Play Services");
        //Ask for explicit permission to access fine location for above Lollipop
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP){
            askForLocationPermission();
            if(mPermissionToAcessLocationGranted){
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
                setMarkerToCurrentLocation();

            }
        }
        else{
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            setMarkerToCurrentLocation();
        }
    }

    @SuppressWarnings({"MissingPermission"})
    private void setMarkerToCurrentLocation() {
        // Add a marker to the current location and move the camera
        if(mLastLocation != null){
            Log.d(TAG, "Current location latitude = "+ mLastLocation.getLatitude() +
                    "Current location latitude = " + mLastLocation.getLongitude());
            LatLng currentLocationMarker = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(currentLocationMarker).title("Current location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocationMarker, 10));
        }
        else{
            Log.d(TAG, "Last location is NULL");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    private void askForLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_FINE_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else{
            //Permission is there. Make it count
            mPermissionToAcessLocationGranted = true;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onLocationChanged(Location location) {
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP){
            askForLocationPermission();
            if(mPermissionToAcessLocationGranted){
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
                setMarkerToCurrentLocation();

            }
        }
        else{
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            setMarkerToCurrentLocation();
        }
    }
}
