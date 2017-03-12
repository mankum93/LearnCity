package com.learncity.learner.search.ver0;

/**
 * Created by DJ on 11/5/2016.
 */

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.learncity.backend.persistence.tutorProfileVer1Api.model.CollectionResponseTutorProfileVer1;
import com.learncity.backend.tutor.tutorApi.TutorApi;
import com.learncity.backend.tutor.tutorApi.model.SearchTutorsQuery;

import java.io.IOException;
import java.util.List;

class SearchTutorsAsyncTask extends AsyncTask<SearchTutorsQuery, Void, Void> {
    private static final String TAG = "SearchTutorsAsyncTask";

    //Client that is gonna talk to the endpoint
    private static TutorApi myApiService = null;
    private CollectionResponseTutorProfileVer1 response;


    @Override
    protected Void doInBackground(SearchTutorsQuery... params) {
        if(myApiService == null) {  // Only do this once
            TutorApi.Builder builder = new TutorApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
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

        //Now, get the search query object
        SearchTutorsQuery query = params[0];

        Log.i(TAG, "Search Query: " + query);
        //Now push the query to the server

        /*
        try{
            response = myApiService.searchTutors(query).execute();
        }
        catch(IOException e){
            Log.e(TAG, "IO Exception while performing the datastore transaction");
            e.printStackTrace();
            return null;
        }
        if(response == null){
            throw new RuntimeException("Response can't be null. Any possible exception has already been handled");
        }
        List<TutorProfileVer1> profiles = response.getItems();
        if(profiles != null){
            Log.d(TAG, "No of profiles searched: " + profiles.size() + "\n" + profiles);
        }
        */

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        //Toast.makeText(context, result, Toast.LENGTH_LONG).show();
    }
}

