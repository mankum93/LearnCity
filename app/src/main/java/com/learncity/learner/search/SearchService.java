package com.learncity.learner.search;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import com.learncity.backend.tutor.tutorApi.TutorApi;
import com.learncity.backend.tutor.tutorApi.model.CollectionResponseTutorAccount;
import com.learncity.backend.tutor.tutorApi.model.SearchTutorsQuery;
import com.learncity.backend.tutor.tutorApi.model.TutorAccount;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by DJ on 3/3/2017.
 */

public class SearchService extends Service {

    public static final int SEARCH_TUTORS = 0x00;
    public static final int SEARCH_JOBS = 0x01;
    // A cancel search request through the Messenger would be useless as it has to be delivered ASAP
    // and not after the search has completed so this const. is useless
    public static final int CANCEL_SEARCH = 0x02;

    // Search query may not necessarily be Parcelable. Using the Messenger API makes it impossible
    // to send a search query as a Message component. This cont. is also fairly useless.
    public static final String SEARCH_QUERY = "com.learncity.learner.search.SEARCH_QUERY";

    private final HandlerThread thread = new HandlerThread("Search");

    {
        Log.d(TAG, "Starting the search handler thread during Service instance creation...");
        thread.start();
    }

    private final Messenger receiver = new Messenger(new SearchHandler(thread.getLooper()));

    private static final String TAG = SearchService.class.getSimpleName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return receiver.getBinder();
    }

    @Override
    public void onDestroy(){
        thread.quit();
    }

    private class SearchHandler extends Handler{

        // Since the requests are being queued in series and only 1 request shall
        // be serviced at a time. We can work with the same instance(by resetting it)
        SearchTutorsTask searchTutorsTask = new SearchTutorsTask();
        SearchTutorsQuery query;

        {
            Log.d(TAG, "Registering the SearchHandler with the EventBus...");
            EventBus.getDefault().register(this);
        }

        public SearchHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg){

            switch(msg.what){
                case SEARCH_TUTORS:
                    Log.d(TAG, "SearchHandler.handleMessage(): Search tutors requested.");
                    List<TutorAccount> list = null;
                    SearchEvent searchEvent;
                    try{
                        if(query == null){
                            Log.d(TAG, "SearchHandler.handleMessage(): Search tutors query is null. Ending search right here...");
                            searchEvent = new SearchEvent(list);
                            EventBus.getDefault().postSticky(searchEvent);
                            return;
                        }
                        list = searchTutorsTask.search(query);
                        searchEvent = new SearchEvent(list);
                    }

                    catch(IOException ioe){
                        Log.e(TAG, "IO Exception while performing the datastore transaction");
                        searchEvent = new SearchEvent(ioe);
                    }
                    finally{
                        searchTutorsTask.reset();
                    }
                    EventBus.getDefault().removeStickyEvent(query);
                    // Post the results
                    EventBus.getDefault().postSticky(searchEvent);

                    break;

            }
        }
        @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
        public void onReceiveSearchQuery(SearchTutorsQuery query){
            // TODO: Check the repeated calling of this method. This method is called only on the
            // Search button press. If you perform more than 1 search, this method starts to get
            // called incrementally repeatedly with each search.
            Log.d(TAG, "Search query received: " + query);
            this.query = query;
        }
        @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
        public void onCancelSearch(CancelSearchEvent event){
            Log.d(TAG, "Search cancellation requested");
            if(event.getSearchType() == SEARCH_TUTORS){
                searchTutorsTask.cancel();
            }
            EventBus.getDefault().removeStickyEvent(event);
        }
        @Override
        public void finalize() throws Throwable{
            Log.d(TAG, "Handler.finalize(): Unregistering the Eventbus now...");
            EventBus.getDefault().unregister(this);
            super.finalize();
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------
    private static class SearchTutorsTask implements Callable<List<TutorAccount>> {

        private static final String TAG = SearchTutorsTask.class.getSimpleName();

        //Client that is gonna talk to the endpoint
        private static TutorApi myApiService = null;
        private CollectionResponseTutorAccount response;
        private SearchTutorsQuery query;

        private boolean cancel = false;

        public SearchTutorsTask(){
            //Initialize the client
            initClient();
        }

        public synchronized List<TutorAccount> search(SearchTutorsQuery  query) throws IOException{
            cancel = false;
            this.query = query;
            return call();
        }

        public void reset(){
            response = null;
            query = null;
            cancel = false;
        }

        public synchronized void cancel(){
            cancel = true;
        }

        @Override
        public List<TutorAccount> call() throws IOException{
            if(myApiService == null){
                initClient();
            }

            Log.i(TAG, "Search Query: " + query);
            //Now push the query to the server

            if(cancel){
                cancel = false;
                return null;
            }
            response = myApiService.searchTutors(query).execute();

            List<TutorAccount> accounts = response.getItems();
            Log.d(TAG, "No of accounts searched: " + (accounts != null ? accounts.size() : 0) + "\n" + accounts + "\n");

            return accounts;
        }

        private void initClient(){
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
    }
    //-----------------------------------------------------------------------------------------------------------------------

    public static class SearchEvent{
        //In case there is a successful login
        private List<TutorAccount> accountsFromServer;
        //In case there is an unsuccessful attempt
        private IOException exception;

        public SearchEvent(List<TutorAccount> accountFromServer) {
            this.accountsFromServer = accountFromServer;
        }

        public SearchEvent(IOException exception) {
            this.exception = exception;
        }

        public List<TutorAccount> getAccountsFromServer() {
            return accountsFromServer;
        }

        public void setAccountsFromServer(List<TutorAccount> accountFromServer) {
            this.accountsFromServer = accountFromServer;
        }

        public IOException getException() {
            return exception;
        }

        public void setException(IOException exception) {
            this.exception = exception;
        }
    }
    //------------------------------------------------------------------------------------------------------------------------
    public static class CancelSearchEvent{
        private int searchType;
        public CancelSearchEvent(int searchType){
            this.searchType = searchType;
        }

        public int getSearchType() {
            return searchType;
        }
    }
}
