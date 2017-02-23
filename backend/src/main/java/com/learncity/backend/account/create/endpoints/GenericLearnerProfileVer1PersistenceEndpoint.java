package com.learncity.backend.account.create.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;
import com.learncity.backend.account.create.GenericLearnerProfileVer1;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
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
        name = "genericLearnerProfileVer1Api",
        version = "v1",
        resource = "genericLearnerProfileVer1",
        namespace = @ApiNamespace(
                ownerDomain = "persistence.backend.learncity.com",
                ownerName = "persistence.backend.learncity.com",
                packagePath = ""
        )
)
public class GenericLearnerProfileVer1PersistenceEndpoint {

    private static final Logger logger = Logger.getLogger(GenericLearnerProfileVer1PersistenceEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(GenericLearnerProfileVer1.class);
    }

    /**
     * Returns the {@link GenericLearnerProfileVer1} with the corresponding ID.
     *
     * @param mEmailID the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code GenericLearnerProfileVer1} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "genericLearnerProfileVer1/{mEmailID}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public GenericLearnerProfileVer1 get(@Named("mEmailID") String mEmailID) throws NotFoundException {
        logger.info("Getting GenericLearnerProfileVer1 with ID: " + mEmailID);
        GenericLearnerProfileVer1 genericLearnerProfileVer1 = ofy().load().type(GenericLearnerProfileVer1.class).id(mEmailID).now();
        if (genericLearnerProfileVer1 == null) {
            throw new NotFoundException("Could not find GenericLearnerProfileVer1 with ID: " + mEmailID);
        }
        return genericLearnerProfileVer1;
    }

    /**
     * Inserts a new {@code GenericLearnerProfileVer1}.
     */
    @ApiMethod(
            name = "insert",
            path = "genericLearnerProfileVer1",
            httpMethod = ApiMethod.HttpMethod.POST)
    public GenericLearnerProfileVer1 insert(GenericLearnerProfileVer1 genericLearnerProfileVer1) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that genericLearnerProfileVer1.mEmailID has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        logger.log(Level.INFO, genericLearnerProfileVer1 + "");
        ofy().save().entity(genericLearnerProfileVer1).now();
        logger.info("Created GenericLearnerProfileVer1.");

        return ofy().load().entity(genericLearnerProfileVer1).now();
    }

    /**
     * Updates an existing {@code GenericLearnerProfileVer1}.
     *
     * @param mEmailID                  the ID of the entity to be updated
     * @param genericLearnerProfileVer1 the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code mEmailID} does not correspond to an existing
     *                           {@code GenericLearnerProfileVer1}
     */
    @ApiMethod(
            name = "update",
            path = "genericLearnerProfileVer1/{mEmailID}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public GenericLearnerProfileVer1 update(@Named("mEmailID") String mEmailID, GenericLearnerProfileVer1 genericLearnerProfileVer1) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(mEmailID);
        ofy().save().entity(genericLearnerProfileVer1).now();
        logger.info("Updated GenericLearnerProfileVer1: " + genericLearnerProfileVer1);
        return ofy().load().entity(genericLearnerProfileVer1).now();
    }

    /**
     * Deletes the specified {@code GenericLearnerProfileVer1}.
     *
     * @param mEmailID the ID of the entity to delete
     * @throws NotFoundException if the {@code mEmailID} does not correspond to an existing
     *                           {@code GenericLearnerProfileVer1}
     */
    @ApiMethod(
            name = "remove",
            path = "genericLearnerProfileVer1/{mEmailID}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("mEmailID") String mEmailID) throws NotFoundException {
        checkExists(mEmailID);
        ofy().delete().type(GenericLearnerProfileVer1.class).id(mEmailID).now();
        logger.info("Deleted GenericLearnerProfileVer1 with ID: " + mEmailID);
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
            path = "genericLearnerProfileVer1",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<GenericLearnerProfileVer1> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<GenericLearnerProfileVer1> query = ofy().load().type(GenericLearnerProfileVer1.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<GenericLearnerProfileVer1> queryIterator = query.iterator();
        List<GenericLearnerProfileVer1> genericLearnerProfileVer1List = new ArrayList<GenericLearnerProfileVer1>(limit);
        while (queryIterator.hasNext()) {
            genericLearnerProfileVer1List.add(queryIterator.next());
        }
        return CollectionResponse.<GenericLearnerProfileVer1>builder().setItems(genericLearnerProfileVer1List).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(String mEmailID) throws NotFoundException {
        try {
            ofy().load().type(GenericLearnerProfileVer1.class).id(mEmailID).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find GenericLearnerProfileVer1 with ID: " + mEmailID);
        }
    }
}