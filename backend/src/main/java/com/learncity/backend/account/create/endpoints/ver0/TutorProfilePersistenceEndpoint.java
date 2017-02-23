package com.learncity.backend.account.create.endpoints.ver0;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;
import com.learncity.backend.account.create.ver0.TutorProfile;

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
        name = "tutorProfileApi",
        version = "v1",
        resource = "tutorProfile",
        namespace = @ApiNamespace(
                ownerDomain = "persistence.backend.learncity.com",
                ownerName = "persistence.backend.learncity.com",
                packagePath = ""
        )
)
public class TutorProfilePersistenceEndpoint {

    private static final Logger logger = Logger.getLogger(TutorProfilePersistenceEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(TutorProfile.class);
    }

    /**
     * Returns the {@link TutorProfile} with the corresponding ID.
     *
     * @param mEmailID the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code TutorProfile} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "tutorProfile/{mEmailID}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public TutorProfile get(@Named("mEmailID") String mEmailID) throws NotFoundException {
        logger.info("Getting TutorProfile with ID: " + mEmailID);
        TutorProfile tutorProfile = ofy().load().type(TutorProfile.class).id(mEmailID).now();
        if (tutorProfile == null) {
            throw new NotFoundException("Could not find TutorProfile with ID: " + mEmailID);
        }
        return tutorProfile;
    }

    /**
     * Inserts a new {@code TutorProfile}.
     */
    @ApiMethod(
            name = "insert",
            path = "tutorProfile",
            httpMethod = ApiMethod.HttpMethod.POST)
    public TutorProfile insert(TutorProfile tutorProfile) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that tutorProfile.mEmailID has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(tutorProfile).now();
        logger.info("Created TutorProfile.");

        return ofy().load().entity(tutorProfile).now();
    }

    /**
     * Updates an existing {@code TutorProfile}.
     *
     * @param mEmailID     the ID of the entity to be updated
     * @param tutorProfile the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code mEmailID} does not correspond to an existing
     *                           {@code TutorProfile}
     */
    @ApiMethod(
            name = "update",
            path = "tutorProfile/{mEmailID}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public TutorProfile update(@Named("mEmailID") String mEmailID, TutorProfile tutorProfile) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(mEmailID);
        ofy().save().entity(tutorProfile).now();
        logger.info("Updated TutorProfile: " + tutorProfile);
        return ofy().load().entity(tutorProfile).now();
    }

    /**
     * Deletes the specified {@code TutorProfile}.
     *
     * @param mEmailID the ID of the entity to delete
     * @throws NotFoundException if the {@code mEmailID} does not correspond to an existing
     *                           {@code TutorProfile}
     */
    @ApiMethod(
            name = "remove",
            path = "tutorProfile/{mEmailID}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("mEmailID") String mEmailID) throws NotFoundException {
        checkExists(mEmailID);
        ofy().delete().type(TutorProfile.class).id(mEmailID).now();
        logger.info("Deleted TutorProfile with ID: " + mEmailID);
    }

    /**
     * List all entities.
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "list",
            path = "tutorProfile",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<TutorProfile> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<TutorProfile> query = ofy().load().type(TutorProfile.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<TutorProfile> queryIterator = query.iterator();
        List<TutorProfile> tutorProfileList = new ArrayList<TutorProfile>(limit);
        while (queryIterator.hasNext()) {
            tutorProfileList.add(queryIterator.next());
        }
        return CollectionResponse.<TutorProfile>builder().setItems(tutorProfileList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(String mEmailID) throws NotFoundException {
        try {
            ofy().load().type(TutorProfile.class).id(mEmailID).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find TutorProfile with ID: " + mEmailID);
        }
    }
}