package com.learncity.backend.persistence;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

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
        name = "genericLearnerProfileApi",
        version = "v1",
        resource = "genericLearnerProfile",
        namespace = @ApiNamespace(
                ownerDomain = "persistence.backend.learncity.com",
                ownerName = "persistence.backend.learncity.com",
                packagePath = ""
        )
)
public class GenericLearnerProfileEndpoint {

    private static final Logger logger = Logger.getLogger(GenericLearnerProfileEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(GenericLearnerProfile.class);
    }

    /**
     * Returns the {@link GenericLearnerProfile} with the corresponding ID.
     *
     * @param mEmailID the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code GenericLearnerProfile} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "genericLearnerProfile/{mEmailID}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public GenericLearnerProfile get(@Named("mEmailID") String mEmailID) throws NotFoundException {
        logger.info("Getting GenericLearnerProfile with ID: " + mEmailID);
        GenericLearnerProfile genericLearnerProfile = ofy().load().type(GenericLearnerProfile.class).id(mEmailID).now();
        if (genericLearnerProfile == null) {
            throw new NotFoundException("Could not find GenericLearnerProfile with ID: " + mEmailID);
        }
        return genericLearnerProfile;
    }

    /**
     * Inserts a new {@code GenericLearnerProfile}.
     */
    @ApiMethod(
            name = "insert",
            path = "genericLearnerProfile",
            httpMethod = ApiMethod.HttpMethod.POST)
    public GenericLearnerProfile insert(GenericLearnerProfile genericLearnerProfile) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that genericLearnerProfile.mEmailID has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(genericLearnerProfile).now();
        logger.info("Created GenericLearnerProfile.");

        return ofy().load().entity(genericLearnerProfile).now();
    }

    /**
     * Updates an existing {@code GenericLearnerProfile}.
     *
     * @param mEmailID              the ID of the entity to be updated
     * @param genericLearnerProfile the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code mEmailID} does not correspond to an existing
     *                           {@code GenericLearnerProfile}
     */
    @ApiMethod(
            name = "update",
            path = "genericLearnerProfile/{mEmailID}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public GenericLearnerProfile update(@Named("mEmailID") String mEmailID, GenericLearnerProfile genericLearnerProfile) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(mEmailID);
        ofy().save().entity(genericLearnerProfile).now();
        logger.info("Updated GenericLearnerProfile: " + genericLearnerProfile);
        return ofy().load().entity(genericLearnerProfile).now();
    }

    /**
     * Deletes the specified {@code GenericLearnerProfile}.
     *
     * @param mEmailID the ID of the entity to delete
     * @throws NotFoundException if the {@code mEmailID} does not correspond to an existing
     *                           {@code GenericLearnerProfile}
     */
    @ApiMethod(
            name = "remove",
            path = "genericLearnerProfile/{mEmailID}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("mEmailID") String mEmailID) throws NotFoundException {
        checkExists(mEmailID);
        ofy().delete().type(GenericLearnerProfile.class).id(mEmailID).now();
        logger.info("Deleted GenericLearnerProfile with ID: " + mEmailID);
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
            path = "genericLearnerProfile",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<GenericLearnerProfile> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<GenericLearnerProfile> query = ofy().load().type(GenericLearnerProfile.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<GenericLearnerProfile> queryIterator = query.iterator();
        List<GenericLearnerProfile> genericLearnerProfileList = new ArrayList<GenericLearnerProfile>(limit);
        while (queryIterator.hasNext()) {
            genericLearnerProfileList.add(queryIterator.next());
        }
        return CollectionResponse.<GenericLearnerProfile>builder().setItems(genericLearnerProfileList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(String mEmailID) throws NotFoundException {
        try {
            ofy().load().type(GenericLearnerProfile.class).id(mEmailID).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find GenericLearnerProfile with ID: " + mEmailID);
        }
    }
}