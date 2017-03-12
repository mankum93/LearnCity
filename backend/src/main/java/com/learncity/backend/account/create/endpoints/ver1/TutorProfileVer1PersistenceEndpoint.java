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
import com.learncity.backend.account.create.TutorProfileVer1;

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
        name = "tutorProfileVer1Api",
        version = "v1",
        resource = "tutorProfileVer1",
        title = "TutorProfileVer1 API",
        namespace = @ApiNamespace(
                ownerDomain = "persistence.backend.learncity.com",
                ownerName = "persistence.backend.learncity.com",
                packagePath = ""
        )
)
public class TutorProfileVer1PersistenceEndpoint {

    private static final Logger logger = Logger.getLogger(TutorProfileVer1PersistenceEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(TutorProfileVer1.class);
        ObjectifyService.register(LearnerUserIDEntity.class);
    }

    /**
     * Returns the {@link TutorProfileVer1} with the corresponding ID.
     *
     * @param mEmailID the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code TutorProfileVer1} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "tutorProfileVer1/{mEmailID}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public TutorProfileVer1 get(@Named("mEmailID") String mEmailID) throws NotFoundException {
        logger.info("Getting TutorProfileVer1 with ID: " + mEmailID);
        TutorProfileVer1 tutorProfileVer1 = ofy().load().type(TutorProfileVer1.class).id(mEmailID).now();
        if (tutorProfileVer1 == null) {
            throw new NotFoundException("Could not find TutorProfileVer1 with ID: " + mEmailID);
        }
        return tutorProfileVer1;
    }

    /**
     * Inserts a new {@code TutorProfileVer1}.
     */
    @ApiMethod(
            name = "insert",
            path = "tutorProfileVer1",
            httpMethod = ApiMethod.HttpMethod.POST)
    public TutorProfileVer1 insert(TutorProfileVer1 tutorProfileVer1) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that tutorProfileVer1.mEmailID has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.

        if(LearnerUserIDEntity.checkIfAccountExists(tutorProfileVer1.getEmailID())){
            return null;
        }
        else{
            //Create a User ID entity
            LearnerUserIDEntity.createUserIDEntity(tutorProfileVer1.getEmailID(), TutorProfileVer1.class);
        }
        ofy().save().entity(tutorProfileVer1).now();
        logger.info("Created TutorProfileVer1.");

        return ofy().load().entity(tutorProfileVer1).now();
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
            name = "updateAccount",
            path = "tutorProfileVer1/{mEmailID}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public TutorProfileVer1 update(@Named("mEmailID") String mEmailID, TutorProfileVer1 tutorProfileVer1) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.

        //First, we should check for an existing Email ID
        if(!LearnerUserIDEntity.checkIfAccountExists(mEmailID)){
            throw new NotFoundException("Could not find LearnerProfileVer1 with ID: " + mEmailID);
        }
        //checkExists(mEmailID);
        ofy().save().entity(tutorProfileVer1).now();
        logger.info("Updated TutorProfileVer1: " + tutorProfileVer1);

        //Update the User ID entity before
        LearnerUserIDEntity.updateUserIDEntity(mEmailID, TutorProfileVer1.class);

        return ofy().load().entity(tutorProfileVer1).now();
    }

    /**
     * Deletes the specified {@code TutorProfileVer1}.
     *
     * @param mEmailID the ID of the entity to delete
     * @throws NotFoundException if the {@code mEmailID} does not correspond to an existing
     *                           {@code TutorProfileVer1}
     */
    @ApiMethod(
            name = "remove",
            path = "tutorProfileVer1/{mEmailID}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("mEmailID") String mEmailID) throws NotFoundException {
        checkExists(mEmailID);

        //Remove the User ID entity before the profile entity deletion
        LearnerUserIDEntity.deleteUserIDEntity(mEmailID);

        ofy().delete().type(TutorProfileVer1.class).id(mEmailID).now();
        logger.info("Deleted TutorProfileVer1 with ID: " + mEmailID);
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
            path = "tutorProfileVer1",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<TutorProfileVer1> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<TutorProfileVer1> query = ofy().load().type(TutorProfileVer1.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<TutorProfileVer1> queryIterator = query.iterator();
        List<TutorProfileVer1> tutorProfileVer1List = new ArrayList<TutorProfileVer1>(limit);
        while (queryIterator.hasNext()) {
            tutorProfileVer1List.add(queryIterator.next());
        }
        return CollectionResponse.<TutorProfileVer1>builder().setItems(tutorProfileVer1List).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(String mEmailID) throws NotFoundException {
        try {
            ofy().load().type(TutorProfileVer1.class).id(mEmailID).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find TutorProfileVer1 with ID: " + mEmailID);
        }
    }
}