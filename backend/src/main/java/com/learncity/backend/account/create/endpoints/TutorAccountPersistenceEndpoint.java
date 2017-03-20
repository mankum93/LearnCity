package com.learncity.backend.account.create.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiClass;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;
import com.learncity.backend.account.create.Account;
import com.learncity.backend.account.create.TutorAccount;
import com.learncity.backend.account.create.TutorProfileVer1;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "tutorApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "tutor.backend.learncity.com",
                ownerName = "tutor.backend.learncity.com",
                packagePath = ""
        )
)
@ApiClass(
        resource = "createTutorAccount"
)
public class TutorAccountPersistenceEndpoint extends BaseLearnerEndpoint{

    private static final Logger logger = Logger.getLogger(TutorAccountPersistenceEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(TutorProfileVer1.class);
        ObjectifyService.register(TutorAccount.class);
    }

    /**
     * Returns the {@link TutorProfileVer1} with the corresponding ID.
     *
     * @param mEmailID the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code TutorProfileVer1} with the provided ID.
     */
    @ApiMethod(
            name = "getTutorProfile",
            path = "tutorProfileVer1/{mEmailID}",
            httpMethod = ApiMethod.HttpMethod.GET)
    @Override
    public TutorProfileVer1 get(@Named("mEmailID") String mEmailID) throws NotFoundException {
        return (TutorProfileVer1) super.get(mEmailID);
    }

    /**
     * Inserts a new {@code TutorProfileVer1}.
     */
    @ApiMethod(
            name = "insertTutorAccount",
            path = "tutorProfileVer1",
            httpMethod = ApiMethod.HttpMethod.POST)
    public TutorProfileVer1 insert(TutorProfileVer1 tutorProfileVer1) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that tutorProfileVer1.mEmailID has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.

        //Check if the AC already exists with this email ID
        Account acc = null;
        try{
            acc = checkIfAccountExists(tutorProfileVer1.getEmailID());
        }
        catch(NotFoundException nfe){
            // Account already not existing - Move with AC creation
        }
        if(acc != null){
            // Account already exists; can't insert/overwrite the existing one
            return null;
        }
        acc = new TutorAccount(tutorProfileVer1, null);

        logger.log(Level.INFO, tutorProfileVer1 + "");
        ofy().save().entity(acc).now();
        logger.info("Created GenericLearnerProfileVer1.");

        //Schedule an updateAccount with location info. with the given coordinates
        //scheduleLocationInfoUpdation(acc, acc.getProfile().getLastKnownGeoCoordinates());

        return (TutorProfileVer1) ofy().load().entity(acc).now().getProfile();
    }

    /**
     * Updates an existing {@code TutorProfileVer1}.
     *
     * @param mEmailID         the ID of the entity to be updated
     * @param tutorProfileVer1 the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code mEmailID} does not correspond to an existing
     *                           {@code TutorProfileVer1}
     */
    @ApiMethod(
            name = "updateTutorAccount",
            path = "tutorProfileVer1/{mEmailID}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public TutorProfileVer1 update(@Named("mEmailID") String mEmailID, TutorProfileVer1 tutorProfileVer1) throws NotFoundException {
        logger.info("Created Tutor Account");

        return (TutorProfileVer1) updateAccount(mEmailID, tutorProfileVer1);
    }

    /**
     * Updates an existing {@code Account} with firebase token.
     *
     * @param mEmailID Email ID of the user
     * @param firebaseToken the firebase token unique to the device for the App instance running on it
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code mEmailID} does not correspond to an existing
     *                           {@code TutorProfileVer1}
     */
    @ApiMethod(
            name = "updateWithFirebaseToken",
            path = "tutorProfileVer1/firebaseToken",
            httpMethod = ApiMethod.HttpMethod.PUT)
    @Override
    public TutorProfileVer1 updateWithFirebaseToken(@Named("mEmailID") String mEmailID, @Named("firebaseToken") String firebaseToken) throws NotFoundException {

        return (TutorProfileVer1) super.updateWithFirebaseToken(mEmailID, firebaseToken);
    }

    /**
     * Deletes the specified {@code TutorProfileVer1}.
     *
     * @param mEmailID the ID of the entity to delete
     * @throws NotFoundException if the {@code mEmailID} does not correspond to an existing
     *                           {@code TutorProfileVer1}
     */
    @ApiMethod(
            name = "removeTutorAccount",
            path = "account/{mEmailID}",
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
            name = "listTutorProfiles",
            path = "tutorProfileVer1",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<TutorProfileVer1> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Account> query = ofy().load().type(Account.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Account> queryIterator = query.iterator();
        List<TutorProfileVer1> tutorProfileVer1List = new ArrayList<TutorProfileVer1>(limit);
        while (queryIterator.hasNext()) {
            tutorProfileVer1List.add((TutorProfileVer1) queryIterator.next().getProfile());
        }
        return CollectionResponse.<TutorProfileVer1>builder().setItems(tutorProfileVer1List).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }
}