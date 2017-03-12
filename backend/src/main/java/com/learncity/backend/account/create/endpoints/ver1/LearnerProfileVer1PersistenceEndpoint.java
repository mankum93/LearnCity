package com.learncity.backend.account.create.endpoints.ver1;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;
import com.learncity.backend.account.LearnerUserIDEntity;
import com.learncity.backend.account.create.LearnerProfileVer1;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "learnerProfileVer1Api",
        version = "v1",
        resource = "learnerProfileVer1",
        title = "LearnerProfileVer1 API",
        namespace = @ApiNamespace(
                ownerDomain = "create.account.backend.learncity.com",
                ownerName = "create.account.backend.learncity.com",
                packagePath = ""
        )
)
public class LearnerProfileVer1PersistenceEndpoint {

    private static final Logger logger = Logger.getLogger(LearnerProfileVer1PersistenceEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(LearnerProfileVer1.class);
        ObjectifyService.register(LearnerUserIDEntity.class);
    }

    /**
     * Returns the {@link LearnerProfileVer1} with the corresponding ID.
     *
     * @param mEmailID the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code LearnerProfileVer1} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "learnerProfileVer1/{mEmailID}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public LearnerProfileVer1 get(@Named("mEmailID") String mEmailID) throws NotFoundException {
        logger.info("Getting LearnerProfileVer1 with ID: " + mEmailID);
        LearnerProfileVer1 learnerProfileVer1 = ofy().load().type(LearnerProfileVer1.class).id(mEmailID).now();
        if (learnerProfileVer1 == null) {
            throw new NotFoundException("Could not find LearnerProfileVer1 with ID: " + mEmailID);
        }
        return learnerProfileVer1;
    }

    /**
     * Inserts a new {@code LearnerProfileVer1}.
     */
    @ApiMethod(
            name = "insert",
            path = "learnerProfileVer1",
            httpMethod = ApiMethod.HttpMethod.POST)
    public LearnerProfileVer1 insert(LearnerProfileVer1 learnerProfileVer1) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that learnerProfileVer1.mEmailID has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.

        //Check if the AC already exists with this email ID
        if(LearnerUserIDEntity.checkIfAccountExists(learnerProfileVer1.getEmailID())){
            return null;
        }
        else{
            //Create a User ID entity
            LearnerUserIDEntity.createUserIDEntity(learnerProfileVer1.getEmailID(), LearnerProfileVer1.class);
        }
        ofy().save().entity(learnerProfileVer1).now();
        logger.info("Created LearnerProfileVer1.");

        return ofy().load().entity(learnerProfileVer1).now();
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
            name = "updateAccount",
            path = "learnerProfileVer1/{mEmailID}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public LearnerProfileVer1 update(@Named("mEmailID") String mEmailID, LearnerProfileVer1 learnerProfileVer1) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.

        //First, we should check for an existing Email ID
        if(!LearnerUserIDEntity.checkIfAccountExists(mEmailID)){
            throw new NotFoundException("Could not find LearnerProfileVer1 with ID: " + mEmailID);
        }
        //TODO: Remove the below operation since we have already checked for Account through User ID list
        //checkExists(mEmailID);
        ofy().save().entity(learnerProfileVer1).now();
        logger.info("Updated LearnerProfileVer1: " + learnerProfileVer1);

        //Update the User ID entity before
        LearnerUserIDEntity.updateUserIDEntity(mEmailID, LearnerProfileVer1.class);

        return ofy().load().entity(learnerProfileVer1).now();
    }

    /**
     * Deletes the specified {@code LearnerProfileVer1}.
     *
     * @param mEmailID the ID of the entity to delete
     * @throws NotFoundException if the {@code mEmailID} does not correspond to an existing
     *                           {@code LearnerProfileVer1}
     */
    @ApiMethod(
            name = "remove",
            path = "learnerProfileVer1/{mEmailID}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("mEmailID") String mEmailID) throws NotFoundException {
        checkExists(mEmailID);

        //Remove the User ID entity before the profile entity deletion
        LearnerUserIDEntity.deleteUserIDEntity(mEmailID);

        ofy().delete().type(LearnerProfileVer1.class).id(mEmailID).now();
        logger.info("Deleted LearnerProfileVer1 with ID: " + mEmailID);
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "list",
            path = "learnerProfileVer1",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<LearnerProfileVer1> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<LearnerProfileVer1> query = ofy().load().type(LearnerProfileVer1.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<LearnerProfileVer1> queryIterator = query.iterator();
        List<LearnerProfileVer1> learnerProfileVer1List = new ArrayList<LearnerProfileVer1>(limit);
        while (queryIterator.hasNext()) {
            learnerProfileVer1List.add(queryIterator.next());
        }
        return CollectionResponse.<LearnerProfileVer1>builder().setItems(learnerProfileVer1List).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(String mEmailID) throws NotFoundException {
        try {
            ofy().load().type(LearnerProfileVer1.class).id(mEmailID).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find LearnerProfileVer1 with ID: " + mEmailID);
        }
    }
}