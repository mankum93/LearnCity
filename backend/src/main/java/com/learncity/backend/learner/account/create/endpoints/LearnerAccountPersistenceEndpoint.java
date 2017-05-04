package com.learncity.backend.learner.account.create.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiClass;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.ApiReference;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;
import com.learncity.backend.common.BaseConfigEndpoint;
import com.learncity.backend.common.account.create.Account;
import com.learncity.backend.common.account.create.endpoints.BaseLearnerEndpoint;
import com.learncity.backend.learner.account.create.LearnerAccount;
import com.learncity.backend.learner.account.create.LearnerProfileVer1;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;


import static com.googlecode.objectify.ObjectifyService.ofy;


@Api(
        name = "learnerApi",
        title = "Learner API"
)
@ApiReference(BaseConfigEndpoint.class)
@ApiClass(
        resource = "createLearnerAccount"
)
public class LearnerAccountPersistenceEndpoint extends BaseLearnerEndpoint {

    private static final Logger logger = Logger.getLogger(LearnerAccountPersistenceEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(LearnerProfileVer1.class);
        ObjectifyService.register(LearnerAccount.class);
    }

    /**
     * Returns the {@link LearnerProfileVer1} with the corresponding ID.
     *
     * @param mEmailID the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code LearnerProfileVer1} with the provided ID.
     */
    @ApiMethod(
            name = "getLearnerProfile",
            path = "learnerProfileVer1/{mEmailID}",
            httpMethod = ApiMethod.HttpMethod.GET)
    @Override
    public LearnerProfileVer1 get(@Named("mEmailID") String mEmailID) throws NotFoundException {
        return (LearnerProfileVer1) super.get(mEmailID);
    }

    /**
     * Inserts a new {@code LearnerProfileVer1}.
     */
    @ApiMethod(
            name = "insertLearnerAccount",
            path = "learnerProfileVer1",
            httpMethod = ApiMethod.HttpMethod.POST)
    public LearnerProfileVer1 insert(LearnerProfileVer1 learnerProfileVer1) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that learnerProfileVer1.mEmailID has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.

        //Check if the AC already exists with this email ID
        Account acc = null;
        try{
            acc = checkIfAccountExists(learnerProfileVer1.getEmailID());
        }
        catch(NotFoundException nfe){
            // Account already not existing - Move with AC creation
        }
        if(acc != null){
            // Account already exists; can't insert/overwrite the existing one
            return null;
        }
        acc = new LearnerAccount(learnerProfileVer1, null);
        logger.log(Level.INFO, learnerProfileVer1 + "");
        ofy().save().entity(acc).now();
        logger.info("Created Learner Account");

        //Schedule an updateAccount with location info. with the given coordinates
        //scheduleLocationInfoUpdation(acc, acc.getProfile().getLastKnownGeoCoordinates());

        return (LearnerProfileVer1) ofy().load().entity(acc).now().getProfile();
    }

    /**
     * Updates an existing {@code LearnerProfileVer1}.
     *
     * @param mEmailID           the ID of the entity to be updated
     * @param learnerProfileVer1 the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code mEmailID} does not correspond to an existing
     *                           {@code LearnerProfileVer1}
     */
    @ApiMethod(
            name = "updateLearnerAccount",
            path = "learnerProfileVer1/{mEmailID}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public LearnerProfileVer1 update(@Named("mEmailID") String mEmailID, LearnerProfileVer1 learnerProfileVer1) throws NotFoundException {

        logger.info("Updated Learner Account");

        return (LearnerProfileVer1) updateAccount(mEmailID, learnerProfileVer1);
    }

    /**
     * Updates an existing {@code Account} with firebase token.
     *
     * @param mEmailID Email ID of the user
     * @param firebaseToken the firebase token unique to the device for the App instance running on it
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code mEmailID} does not correspond to an existing
     *                           {@code LearnerProfileVer1}
     */
    @ApiMethod(
            name = "updateWithFirebaseToken",
            path = "learnerProfileVer1/firebaseToken",
            httpMethod = ApiMethod.HttpMethod.PUT)
    @Override
    public LearnerProfileVer1 updateWithFirebaseToken(@Named("mEmailID") String mEmailID, @Named("firebaseToken") String firebaseToken) throws NotFoundException {
        return (LearnerProfileVer1) super.updateWithFirebaseToken(mEmailID, firebaseToken);
    }

    /**
     * Deletes the specified {@code TutorProfileVer1}.
     *
     * @param mEmailID the ID of the entity to delete
     * @throws NotFoundException if the {@code mEmailID} does not correspond to an existing
     *                           {@code TutorProfileVer1}
     */
    @ApiMethod(
            name = "removeLearnerAccount",
            path = "Account/{mEmailID}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    @Override
    public void remove(@Named("mEmailID") String mEmailID) throws NotFoundException {
        super.remove(mEmailID);
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "listLearnerProfiles",
            path = "learnerProfileVer1",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<LearnerProfileVer1> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Account> query = ofy().load().type(Account.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Account> queryIterator = query.iterator();
        List<LearnerProfileVer1> learnerProfileVer1List = new ArrayList<LearnerProfileVer1>(limit);
        while (queryIterator.hasNext()) {
            learnerProfileVer1List.add((LearnerProfileVer1) queryIterator.next().getProfile());
        }
        return CollectionResponse.<LearnerProfileVer1>builder().setItems(learnerProfileVer1List).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }


}