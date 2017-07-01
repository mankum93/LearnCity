package com.learncity.learner.search;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatMultiAutoCompleteTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Switch;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.learncity.backend.tutorApi.model.SearchTutorsQuery;
import com.learncity.backend.tutorApi.model.TutorAccount;
import com.learncity.backend.tutorApi.model.TutorAccountResponseView;
import com.learncity.backend.tutorApi.model.TutorProfileResponseView;
import com.learncity.learncity.R;
import com.learncity.util.ArrayUtils;
import com.learncity.util.GoogleApiHelper;
import com.learncity.util.ListUtils;
import com.learncity.util.TutorProfileUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.learncity.generic.learner.account.Preferences.PREFS_TUTOR_SEARCH;
import static com.learncity.learner.search.SearchResultsActivity.SEARCHED_ACCOUNTS;
import static com.learncity.learner.search.SearchResultsActivity.SEARCH_QUERY_TODO;
import static com.learncity.util.GoogleApiHelper.DIALOG_ERROR;
import static com.learncity.util.GoogleApiHelper.REQUEST_RESOLVE_ERROR;
import static com.learncity.util.GoogleApiHelper.STATE_RESOLVING_ERROR;

/**
 * Created by DJ on 10/18/2016.
 */

public class SearchFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String TAG = "SearchActivity";
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0X00;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static final String IS_LOCATION_TOGGLED_ON = "isLocationSwitchToggledOn";

    private GoogleMap mMap;
    private SearchTutorsQuery mSearchQuery;
    private GoogleApiClient mGoogleApiClient;

    private ProgressDialog searchProgressDialog;
    private AlertDialog searchRetryAlertDialog;

    private Location mLastLocation;

    boolean mPermissionToAccessLocationGranted = false;

    private LocationRequest mLocationRequest;

    private boolean isLocationSwitchToggledOn;


    /** Messenger for communicating with the service. */
    private Messenger mService = null;

    /** Flag indicating whether we have called bind on the service. */
    private boolean mBound;

    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            mService = new Messenger(service);
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            mBound = false;
        }
    };
    private boolean cancelSearch;
    private MultiAutoCompleteTextView subjectsMultiAutoCompleteTextView;
    private QualificationMultiAutoCompleteTextView qualificationMultiAutoCompleteTextView;
    private Switch toggleLocationSwitch;
    private boolean mResolvingError;
    private SupportMapFragment mapFragment;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Checking if there has been a previous unfinsihed attempt for error resolution - in
        // which case let it complete
        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);

        // Preemptive initialization of GoogleApiClient
        initializeGoogleApiClient();

        // See if last time Location was toggled on.
        isLocationSwitchToggledOn = getContext().getSharedPreferences(PREFS_TUTOR_SEARCH, 0)
                .getBoolean(IS_LOCATION_TOGGLED_ON, false);

        Log.d("TAG", "Google Play services are not available");
        //TODO: Use Android Location Services

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        //Initialize the query
        mSearchQuery =  new SearchTutorsQuery();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_tutors, container, false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = new LocationSearchFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.map_search_fragment, mapFragment).commit();

        // Obtain the Multi Auto Complete Text Views------------------------------------------------------------------

        subjectsMultiAutoCompleteTextView = (SubjectMultiAutoCompleteTextView) view.findViewById(R.id.subject_multi_auto_complete_view);

        // Initialize the adapter with dummy data and set it up
        subjectsMultiAutoCompleteTextView.setAdapter(new SubjectSearchAdapter(getContext(),
                R.layout.search_by_subject_list_item_1,
                getResources().getStringArray(R.array.list_of_disciplines)));

        // NOTE: Don't forget to set the Tokenizer. The suggestions won't show without it.
        subjectsMultiAutoCompleteTextView.setTokenizer(new AppCompatMultiAutoCompleteTextView.CommaTokenizer());

        // Now, the Qualifications one,

        qualificationMultiAutoCompleteTextView = (QualificationMultiAutoCompleteTextView) view.findViewById(R.id.qualification_multi_auto_complete_view);

        //Initialize the adapter with dummy data and set it up
        qualificationMultiAutoCompleteTextView.setAdapter(new QualificationSearchAdapter(getContext(),
                R.layout.search_by_qualification_list_item_1,
                getResources().getStringArray(R.array.type_of_tutor)));

        // NOTE: Don't forget to set the Tokenizer. The suggestions won't show without it.
        qualificationMultiAutoCompleteTextView.setTokenizer(new AppCompatMultiAutoCompleteTextView.CommaTokenizer());

        //-------------------------------------------------------------------------------------------------------------------

        toggleLocationSwitch = (Switch) view.findViewById(R.id.location_toggle_switch);

        toggleLocationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    // Check for permission to access location for API level >=21
                    if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP){
                        askForLocationPermission();
                    }
                    else{
                        // Check if Google Play services is installed.
                        if(!mGoogleApiClient.isConnected()){
                            mGoogleApiClient.connect();
                        }
                    }

                    // When checked once, store it as a preference.
                    getContext().getSharedPreferences(PREFS_TUTOR_SEARCH, 0)
                            .edit()
                            .putBoolean(IS_LOCATION_TOGGLED_ON, true)
                            .apply();
                }
                else{
                    getContext().getSharedPreferences(PREFS_TUTOR_SEARCH, 0)
                            .edit()
                            .putBoolean(IS_LOCATION_TOGGLED_ON, false)
                            .apply();
                }
            }
        });

        // Toggle the switch to what it was last time
        toggleLocationSwitch.setChecked(isLocationSwitchToggledOn);

        //-------------------------------------------------------------------------------------------------------------------

        // Lets handle the click of the floating search button
        Button searchButton = (Button) view.findViewById(R.id.search_button);

        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                cancelSearch = false;
                showSearchProgressDialog();
                //Search the Tutor tutorRequestRecordList from the DB.
                //Build and retrieve the search query
                List<String> subjects = Arrays.asList(ArrayUtils.convertStringToArray(subjectsMultiAutoCompleteTextView.getText().toString(), ", "));
                mSearchQuery.setSubjects(subjects);
                Log.i(TAG, "Subjects: " + subjects);
                List<String> tutorTypes = Arrays.asList(ArrayUtils.convertStringToArray(qualificationMultiAutoCompleteTextView.getText().toString(), ", "));
                Log.i(TAG, "Tutor types: " + tutorTypes);
                mSearchQuery.setTutorTypes(tutorTypes);

                TutorAccountResponseView spec = new TutorAccountResponseView();

                TutorProfileResponseView tprvSpec = new TutorProfileResponseView();
                tprvSpec.setDisciplines(1)
                        .setTutorTypes(1)
                        .setMName(1)
                        .setRating(1)
                        .setMDisplayPicturePath(1);

                spec.setLocationInfo(1);
                spec.setProfile(tprvSpec);
                mSearchQuery.setAccountResponseView(spec);

                //Send the Search request
                Message search = Message.obtain(null, SearchService.SEARCH_TUTORS);
                //Bundle b = new Bundle();
                //b.putString(SearchService.SEARCH_QUERY_TODO, new Gson().toJson(mSearchQuery));
                //search.setData(b);
                Log.d(TAG, "Posting Search query on Search button press.");
                EventBus.getDefault().postSticky(mSearchQuery);
                try{
                    if(cancelSearch){
                        dismissSearchProgressDialog();
                        return;
                    }
                    mService.send(search);
                }
                catch(RemoteException re){

                }
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);

        super.onSaveInstanceState(outState);
    }

    private void initializeGoogleApiClient() {
        //If client is already initialized then no need for it again

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onSearch(SearchService.SearchEvent searchEvent){
        dismissSearchProgressDialog();
        if(searchEvent == null){
            return;
        }
        List<TutorAccount> accounts = searchEvent.getAccountsFromServer();

        if(mBound){
            if(accounts == null && searchEvent.getException() == null){
                Log.d(TAG, "No search results were found.");
                // There wasn't any search result
                Toast.makeText(getContext(), "No tutors found. Try with a different query.", Toast.LENGTH_SHORT).show();
            }
            else if(accounts != null){
                Log.d(TAG, "Searched tutorRequestRecordList received: " + accounts);
                if(cancelSearch){
                    return;
                }
                // Show these tutorRequestRecordList in a list view
                Intent i = new Intent(getContext(), SearchResultsActivity.class);
                i.putExtra(SEARCH_QUERY_TODO, ListUtils.convertListToString(mSearchQuery.getSubjects()));
                i.putExtra(SEARCHED_ACCOUNTS, TutorProfileUtils.refactorAccountsToArray(accounts));
                startActivity(i);
            }
            else{
                if(cancelSearch){
                    return;
                }
                IOException searchException = searchEvent.getException();
                if(searchException instanceof GoogleJsonResponseException){
                    int statusCode = ((GoogleJsonResponseException)(searchEvent.getException())).getStatusCode();
                    String msg = "There was some problem with the search";
                    if(statusCode == 400){
                        Log.d(TAG, "BadRequestException");
                        //Indicate to the user about this and take appropriate action
                        msg = "There was some problem with the search";
                    }
                    else if(statusCode == 401){
                        // Won't happen
                        Log.d(TAG, "UnauthorizedException");
                        msg = "There was some problem with the search";
                    }
                    else if(statusCode == 403){
                        Log.d(TAG, "ForbiddenException");
                        msg = "There was some problem with the search";
                    }
                    else if(statusCode == 404){
                        Log.d(TAG, "NotFoundException");
                        msg = "No tutors with that query. Try with a different query.";
                    }
                    else if(statusCode == 409){
                        Log.d(TAG, "ConflictException");
                        //TODO: Take apt. action
                    }
                    else if(statusCode == 500){
                        Log.d(TAG, "InternalServerErrorException");
                        msg = "Search service unavailable. Try again";
                    }
                    else if(statusCode == 503){
                        Log.d(TAG, "ServiceUnavailableException");
                        msg = "Search service unavailable. Try again";
                    }
                    showSearchAlertDialog(msg);
                }
            }
        }
    }

    private void showSearchAlertDialog(String msg){
        //If dialog already showing, no use invoking it again
        if(searchRetryAlertDialog == null){
            searchRetryAlertDialog = new AlertDialog.Builder(getContext())
                    .setMessage(msg)
                    .setCancelable(true)
                    .create();
        }
        if(!searchRetryAlertDialog.isShowing()){
            Log.d(TAG, "Showing the Search Alert dialog...");
            searchRetryAlertDialog.show();
        }

    }

    private void showSearchProgressDialog(){
        //If dialog already in progress, no use invoking it again
        if(searchProgressDialog == null){
            searchProgressDialog = new ProgressDialog(getContext());
            searchProgressDialog.setIndeterminate(true);
            searchProgressDialog.setCancelable(true);
            searchProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancelSearch = true;
                    Log.d(TAG, "Requesting Search cancellation on Search Progress dialog dismissal");
                    EventBus.getDefault().postSticky(new SearchService.CancelSearchEvent(SearchService.SEARCH_TUTORS));
                    // Try cancelling the search
                }
            });
        }
        if(!searchProgressDialog.isShowing()){
            Log.d(TAG, "Showing the search progress dialog...");
            searchProgressDialog.show();
        }
    }

    private void dismissSearchProgressDialog(){
        //If dialog already dismissed, no use dismissing it again
        if(searchProgressDialog != null){
            if(searchProgressDialog.isShowing()){
                Log.d(TAG, "Dismissing the search progress dialog...");
                searchProgressDialog.dismiss();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
    }
    @Override
    public void onPause() {
        Log.d(TAG, "Calling SearchActivity.onPause()");
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);

        // Bind to the service
        getContext().bindService(new Intent(getContext(), SearchService.class), mConnection,
                Context.BIND_AUTO_CREATE);

    }
    @Override
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }

        // Unbind from the service
        if (mBound) {
            getContext().unbindService(mConnection);
            mBound = false;
        }
        cancelSearch = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }
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

        Log.d(TAG, "Map is ready.");
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        setMarkerToCurrentLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mPermissionToAccessLocationGranted = true;
                    Log.d(TAG, "Permission to access fine location granted");

                    // Check if Google Play services is installed.
                    if(!mGoogleApiClient.isConnected()){
                        mGoogleApiClient.connect();
                    }

                } else {
                    mPermissionToAccessLocationGranted = false;
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

        if(toggleLocationSwitch.isChecked()){
            // Permission has been obtained to access fine location
            // And, Google Play services is updated & connected
            // And Location has been toggled to ON

            mapFragment.getMapAsync(this);
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
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_FINE_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else{
            //Permission is there. Make it count
            mPermissionToAccessLocationGranted = true;

            if(!mGoogleApiClient.isConnected()){
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e(TAG, "Connection failed" + connectionResult);
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (connectionResult.hasResolution()) {
            try {
                mResolvingError = true;
                connectionResult.startResolutionForResult(getActivity(), REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            showErrorDialog(connectionResult.getErrorCode());
            mResolvingError = true;
        }
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
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        setMarkerToCurrentLocation();
    }


    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        GoogleApiHelper.ErrorDialogFragmentWithCallback dialogFragment = new GoogleApiHelper.ErrorDialogFragmentWithCallback();

        dialogFragment.setOnDialogDismissedListener(new GoogleApiHelper.OnDialogDismissedListener() {
            @Override
            public void onDialogDismissed() {
                mResolvingError = false;
            }
        });

        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getActivity().getSupportFragmentManager(), "errordialog");
    }
}
