package com.learncity.backend.account.create.endpoints;

import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

import com.googlecode.objectify.ObjectifyService;
import com.learncity.backend.account.create.Account;
import com.learncity.backend.account.create.GenericLearnerProfileVer1;
import com.learncity.backend.account.create.LatLng;

import com.learncity.backend.util.LocationUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by DJ on 3/9/2017.
 */

/**To house the common helper methods for the derived endpoints*/
public class BaseLearnerEndpoint {

    private static final Logger logger = Logger.getLogger(BaseLearnerEndpoint.class.getName());


    static {
        ObjectifyService.register(Account.class);
    }

    public GenericLearnerProfileVer1 get(String mEmailID) throws NotFoundException {
        logger.info("Getting Learner Account with ID: " + mEmailID);
        //Check if the AC already exists with this email ID
        Account acc = null;
        try{
            acc = checkIfAccountExists(mEmailID);
        }
        catch(NotFoundException nfe){
            // Account already not existing - Move with AC creation
            throw nfe;
        }

        return acc.getProfile();
    }

    public GenericLearnerProfileVer1 updateAccount(@Named("mEmailID") String mEmailID, GenericLearnerProfileVer1 learnerProfileVer1) throws NotFoundException {

        // First, we should check for an existing Email ID
        Account acc = null;
        try{
            acc = checkIfAccountExists(learnerProfileVer1.getEmailID());
        }
        catch(NotFoundException nfe){
            throw nfe;
        }
        // Account is not null at this point
        acc.setProfile(learnerProfileVer1);
        logger.log(Level.INFO, learnerProfileVer1 + "");
        ofy().save().entity(acc).now();

        return ofy().load().entity(acc).now().getProfile();
    }

    public void remove(String mEmailID) throws NotFoundException {
        checkIfAccountExists(mEmailID);

        ofy().delete().type(Account.class).id(mEmailID).now();
        logger.info("Deleted TutorProfileVer1 with ID: " + mEmailID);
    }

    void scheduleLocationInfoUpdation(final Account acc, final LatLng geoCoordinates){
        if(geoCoordinates == null){
            return;
        }
        //Using a deferred task
        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(TaskOptions.Builder.withPayload(new LocationTask(acc, geoCoordinates)).countdownMillis(800));
    }

    static class LocationTask implements DeferredTask {

        private static long request_OVER_QUERY_LIMIT_counter = 0;

        private Account acc;
        private LatLng geoCoordinates;
        private int retries = 0;
        public static boolean shouldRequestWithoutQuotaConstraint = false;

        public LocationTask(final Account acc, final LatLng geoCoordinates) {
            this.acc = acc;
            this.geoCoordinates = geoCoordinates;
        }

        @Override
        public void run() {
            String response;
            logger.info("shouldRequestWithoutQuotaConstraint: " + shouldRequestWithoutQuotaConstraint);
            if(shouldRequestWithoutQuotaConstraint){
                response = LocationUtil.getFormattedAddress(geoCoordinates, null, false);
            }
            else{
                response = LocationUtil.getFormattedAddress(geoCoordinates);
            }

            if((response = checkResponseCode(response)) != null){
                // Stash the address into the Datastore
                acc.setLocationInfo(new Account.LocationInfo(response));
                ofy().save().entity(acc).now();
            }
        }
        private String checkResponseCode(String response){
            if(response == null){
                return null;
            }
            if(response.equals("ZERO_RESULTS")){
                return null;
            }
            else if(response.equals("OVER_QUERY_LIMIT")){
                // We can re-request without any GET params except the LatLng
                if(retries ++ == 1){
                    return null;
                }
                request_OVER_QUERY_LIMIT_counter ++;
                // 10 requests haven't been serviced because of the Quota
                if(request_OVER_QUERY_LIMIT_counter >= 10){
                    shouldRequestWithoutQuotaConstraint = true;
                    return checkResponseCode(LocationUtil.getFormattedAddress(geoCoordinates, null, false));
                }
                else{
                    return checkResponseCode(LocationUtil.getFormattedAddress(geoCoordinates, null));
                }
            }
            else if(response.equals("REQUEST_DENIED")){
                return null;
            }
            else if(response.equals("INVALID_REQUEST")){
                return null;
            }
            else if(response.equals("UNKNOWN_ERROR")){
                return null;
            }
            else{
                return response;
            }

        }
    }

    Account checkIfAccountExists(String mEmailID) throws NotFoundException {
        Account acc = null;
        try {
            acc = ofy().load().type(Account.class).id(mEmailID).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Account with ID: " + mEmailID);
        }
        return acc;
    }
}
