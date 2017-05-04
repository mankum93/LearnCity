package com.learncity.backend.tutor.search;


import com.learncity.backend.common.account.create.LatLng;
import com.learncity.backend.tutor.account.create.TutorAccount;

import java.util.Arrays;

/**
 * Created by DJ on 11/4/2016.
 */

public class SearchTutorsQuery {

    private String[] subjects;
    private String[] tutorTypes;
    private LatLng location;

    //Response view
    private TutorAccount.TutorAccountResponseView _0;

    public SearchTutorsQuery(){

    }
    @Override
    public String toString(){
        return new StringBuilder(subjects == null? "Subjects: " + null + "\n" : "Subjects: " + Arrays.toString(subjects) + "").append("\n")
                .append(tutorTypes == null? "Tutor types: " + null + "\n" : "Tutor types: " + Arrays.toString(tutorTypes) + "").append("\n")
                .append(location == null? "Location" + null + "\n" : "Location" + "{" + location.getLatitude() + "," + location.getLongitude() + "}" + "")
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

    public TutorAccount.TutorAccountResponseView getAccountResponseView() {
        return _0;
    }

    public void setAccountResponseView(TutorAccount.TutorAccountResponseView tutorProfileResponseView) {
        this._0 = tutorProfileResponseView;
    }
}
