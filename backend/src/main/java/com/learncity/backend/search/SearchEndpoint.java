package com.learncity.backend.search;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.googlecode.objectify.ObjectifyService;
import com.learncity.backend.persistence.Profile;
import com.learncity.backend.persistence.ProfileEndpoint;

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
                ownerDomain = "search.learncity.com",
                ownerName = "search.learncity.com",
                packagePath = ""
        )
)
public class SearchEndpoint {

    private static final Logger logger = Logger.getLogger(SearchEndpoint.class.getName());

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Profile.class);
    }

    @ApiMethod(name = "searchTutors")
    public SearchResponse searchTutors(SearchQuery query) {

        //TODO: Implement the search logic here
        //Query the database for the profiles with matching subjects/qualifications
        List<Profile> profiles = ofy().load().type(Profile.class).filter("year >", 1999).list();

        SearchResponse response = new SearchResponse();
        //TODO: Set the response i.e the profiles of tutors

        return response;
    }

}
