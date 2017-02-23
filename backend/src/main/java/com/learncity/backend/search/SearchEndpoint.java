package com.learncity.backend.search;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;
import com.learncity.backend.account.create.ver0.TutorProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by DJ on 11/4/2016.
 */

@Api(
        name = "searchApi",
        version = "v1",
        title = "Search API",
        namespace = @ApiNamespace(
                ownerDomain = "learncity.com",
                ownerName = "Learncity",
                packagePath = ""
        )
)
public class SearchEndpoint {

    private static final Logger logger = Logger.getLogger(SearchEndpoint.class.getName());

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(TutorProfile.class);
    }

    /**
     * List all entities as per the query.
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(name = "searchTutors",
            httpMethod = ApiMethod.HttpMethod.POST)
    public CollectionResponse<TutorProfile> searchTutors(SearchQuery searchQuery) {

        Integer limit = searchQuery.getLimit();
        String cursor = searchQuery.getCursor();

        //Query the database for the profiles with matching subjects/qualifications
        List<TutorProfile> profiles = ofy().load().type(TutorProfile.class).filter("year >", 1999).list();

        limit = limit == null ? SearchQuery.DEFAULT_LIST_LIMIT : limit;
        Query<TutorProfile> query = ofy().load().type(TutorProfile.class).filter("XXXXX", 1234).limit(limit);
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

}
