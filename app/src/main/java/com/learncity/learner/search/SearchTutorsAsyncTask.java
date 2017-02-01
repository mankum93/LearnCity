package com.learncity.learner.search;

/**
 * Created by DJ on 11/5/2016.
 */

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

import com.learncity.search.searchApi.SearchApi;
import com.learncity.search.searchApi.model.SearchQuery;

class SearchTutorsAsyncTask extends AsyncTask<SearchQuery, Void, Void> {
    private static final String TAG = "SearchTutorsAsyncTask";

    //Client that is gonna talk to the endpoint
    private static SearchApi myApiService = null;


    @Override
    protected Void doInBackground(SearchQuery... params) {
        if(myApiService == null) {  // Only do this once
            SearchApi.Builder builder = new SearchApi.Builder(AndroidHttp.newCompatibleTransport(),
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
        SearchQuery query = params[0];

        //Now push the query to the server

        try{
            myApiService.searchTutors(query).execute();
        }
        catch(IOException e){
            Log.e(TAG, "IO Exception while performing the datastore transaction");
        }
        return null;

    }

    @Override
    protected void onPostExecute(Void result) {
        //Toast.makeText(context, result, Toast.LENGTH_LONG).show();
    }
}

