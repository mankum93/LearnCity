package com.learncity.backend.search;


import com.learncity.backend.account.create.LatLng;
import com.learncity.backend.account.create.TutorProfileVer1;

/**
 * Created by DJ on 11/4/2016.
 */

public class SearchTutorsQuery {

    private String[] subjects;
    private String[] tutorTypes;
    private LatLng location;

    //Response view
    private TutorProfileVer1.TutorProfileResponseView _0;

    public SearchTutorsQuery(){

    }
    @Override
    public String toString(){
        return new StringBuilder(subjects + "").append("\n")
                .append(tutorTypes + "").append("\n")
                .append(location + "")
                .toString();
    }

    public String[] getSubjects() {
        return subjects;
    }

    public void setSubjects(String[] mSubject) {
        this.subjects = mSubject;
    }

    public String[] getTutorTypes() {
        return tutorTypes;
    }

    public void setTutorTypes(String[] tutorTypes) {
        this.tutorTypes = tutorTypes;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public TutorProfileVer1.TutorProfileResponseView getTutorProfileResponseView() {
        return _0;
    }

    public void setTutorProfileResponseView(TutorProfileVer1.TutorProfileResponseView tutorProfileResponseView) {
        this._0 = tutorProfileResponseView;
    }
}
