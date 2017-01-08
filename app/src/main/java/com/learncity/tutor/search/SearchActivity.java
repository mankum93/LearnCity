package com.learncity.tutor.search;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.learncity.learner.search.QualificationSearchFragment;
import com.learncity.learner.search.SubjectSearchFragment;
import com.learncity.learncity.R;
import com.learncity.search.searchApi.model.SearchQuery;

/**
 * Created by DJ on 10/18/2016.
 */
public class SearchActivity extends AppCompatActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private SearchQuery mSearchQuery;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

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
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_search_fragment);
        mapFragment.getMapAsync(this);

        SubjectSearchFragment subjectSearchFragment = (SubjectSearchFragment) getSupportFragmentManager().findFragmentById(R.id.subject_search_fragment);
        subjectSearchFragment.setSubjectSearchQueryCallback(new SubjectSearchFragment.SubjectSearchQueryCallback() {
            @Override
            public void onSubjectSearchQuery(String subjectsSearchQuery) {
                mSearchQuery.setSubject(subjectsSearchQuery);
            }
        });
        QualificationSearchFragment qualificationSearchFragment = (QualificationSearchFragment) getSupportFragmentManager().findFragmentById(R.id.qualification_search_fragment);
        qualificationSearchFragment.setQualificationSearchQueryCallback(new QualificationSearchFragment.QualificationSearchQueryCallback() {
            @Override
            public void onQualificationSearchQuery(String qualificationsSearchQuery) {
                mSearchQuery.setQualification(qualificationsSearchQuery);
            }
        });

        //First get the reference to the root view
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        //Lets handle the click of the floating search button
        Button searchButton = (Button) viewGroup.findViewById(R.id.search_button);

        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Search the Tutor profiles from the DB.
                new SearchTutorsAsyncTask().execute(mSearchQuery);
            }
        });
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
