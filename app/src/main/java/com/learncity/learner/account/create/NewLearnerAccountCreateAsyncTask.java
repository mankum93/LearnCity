package com.learncity.learner.account.create;


import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.json.GenericJson;
import com.learncity.backend.persistence.genericLearnerProfileApi.GenericLearnerProfileApi;
import com.learncity.backend.persistence.genericLearnerProfileApi.model.GenericLearnerProfile;
import com.learncity.backend.persistence.tutorProfileApi.TutorProfileApi;
import com.learncity.backend.persistence.tutorProfileApi.model.Duration;
import com.learncity.backend.persistence.tutorProfileApi.model.EducationalQualification;
import com.learncity.backend.persistence.tutorProfileApi.model.Occupation;
import com.learncity.backend.persistence.tutorProfileApi.model.TutorProfile;
import com.learncity.generic.learner.account.profile.model.ver0.GenericLearnerProfileParcelable;
import com.learncity.tutor.account.profile.model.qualification.educational.EducationalQualificationParcelable;
import com.learncity.tutor.account.profile.model.qualification.educational.ver0.TutorProfileParcelable;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by DJ on 10/30/2016.
 */

public class NewLearnerAccountCreateAsyncTask extends AsyncTask<GenericLearnerProfileParcelable, Void, Void> {
    private static final String TAG = "NewAccountAsyncTask";

    private static AbstractGoogleJsonClient myApiService = null;

    public static void setApiService(GenericLearnerProfileParcelable profile) {
        AbstractGoogleJsonClient.Builder builder = selectBuilder(profile)
                // options for running against local devappserver
                // - 10.0.2.2 is localhost's IP address in Android emulator
                // - turn off compression when running against local devappserver
                .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                .setApplicationName("Learn City")
                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                    @Override
                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                        abstractGoogleClientRequest.setDisableGZipContent(true);
                    }
                });
        // end options for devappserver


        myApiService = builder.build();
    }
    private static AbstractGoogleJsonClient.Builder selectBuilder(GenericLearnerProfileParcelable profile){
        if(profile instanceof TutorProfileParcelable){
            return new TutorProfileApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null);
        }
        return new GenericLearnerProfileApi.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null);
    }

    @Override
    protected Void doInBackground(GenericLearnerProfileParcelable... params) {

        //Now, get the profile info./object that needs to be pushed to the datastore
        GenericLearnerProfileParcelable profile = params[0];

        if(myApiService == null) {  // Only do this once
            setApiService(profile);
        }

        //Populate the entity object with the profile info.
        GenericJson profileEntity = populateProfileEntity(profile);

        //Now push the info. to the database through the right service
        try{
            if(myApiService instanceof GenericLearnerProfileApi){
                ((GenericLearnerProfileApi)myApiService).insert((GenericLearnerProfile) profileEntity).execute();
            }
            else if(myApiService instanceof TutorProfileApi){
                ((TutorProfileApi)myApiService).insert((com.learncity.backend.persistence.tutorProfileApi.model.TutorProfile) profileEntity).execute();
            }

        }
        catch(IOException e){
            Log.e(TAG, "IO Exception while performing the datastore transaction");
            e.printStackTrace();
        }
        return null;

    }

    @Override
    protected void onPostExecute(Void result) {
        //Toast.makeText(context, result, Toast.LENGTH_LONG).show();
    }

    private GenericJson populateProfileEntity(GenericLearnerProfileParcelable profile){
        //Populate the entity object with the profile info.

        GenericJson profileEntity = null;

        if(profile instanceof TutorProfileParcelable){
            TutorProfileParcelable tutorProfile = (TutorProfileParcelable)profile;

            profileEntity = new com.learncity.backend.persistence.tutorProfileApi.model.TutorProfile();
            TutorProfile profileEntityTutor = (TutorProfile) profileEntity;

            profileEntityTutor.setName(tutorProfile.getName());
            profileEntityTutor.setEmailID(tutorProfile.getEmailID());
            profileEntityTutor.setPhoneNo(tutorProfile.getPhoneNo());
            profileEntityTutor.setPassword(tutorProfile.getPassword());
            profileEntityTutor.setCurrentStatus(tutorProfile.getCurrentStatus());

            EducationalQualification educationalQualificationEntities[] = new EducationalQualification[tutorProfile.getEducationalQualifications().length];
            int i=0;

            for(EducationalQualificationParcelable educationalQualification : tutorProfile.getEducationalQualifications()){
                EducationalQualification educationalQualificationEntity = new EducationalQualification();
                educationalQualificationEntity.setInstitution(educationalQualification.getInstitution());
                educationalQualificationEntity.setQualificationName(educationalQualification.getmQualificationName());
                educationalQualificationEntity.setYearOfPassing(educationalQualification.getYearOfPassing());
                Duration qualificationDuration = new Duration();
                qualificationDuration.setNoOfDays(educationalQualification.getDuration().getNoOfDays());
                qualificationDuration.setNoOfMonths(educationalQualification.getDuration().getNoOfMonths());
                qualificationDuration.setNoOfYears(educationalQualification.getDuration().getNoOfYears());
                educationalQualificationEntity.setDuration(qualificationDuration);

                educationalQualificationEntities[i++] = educationalQualificationEntity;

            }
            profileEntityTutor.setEducationalQualifications(Arrays.asList(educationalQualificationEntities));

            Occupation occupation = new Occupation();
            occupation.setCurrentDesignation(tutorProfile.getOccupation().getCurrentDesignation());
            occupation.setCurrentOrganization(tutorProfile.getOccupation().getCurrentOrganization());
            Duration occupationDuration = new Duration();
            occupationDuration.setNoOfDays(tutorProfile.getOccupation().getCurrentExperience().getNoOfDays());
            occupationDuration.setNoOfMonths(tutorProfile.getOccupation().getCurrentExperience().getNoOfMonths());
            occupationDuration.setNoOfYears(tutorProfile.getOccupation().getCurrentExperience().getNoOfYears());
            occupation.setCurrentExperience(occupationDuration);

        }
        else{
            profileEntity = new GenericLearnerProfile();
            GenericLearnerProfile profileLearnerEntity = (GenericLearnerProfile)profileEntity;

            profileLearnerEntity.setName(profile.getName());
            profileLearnerEntity.setEmailID(profile.getEmailID());
            profileLearnerEntity.setPhoneNo(profile.getPhoneNo());
            profileLearnerEntity.setPassword(profile.getPassword());
            profileLearnerEntity.setCurrentStatus(profile.getCurrentStatus());
        }

        return profileEntity;
    }

}
