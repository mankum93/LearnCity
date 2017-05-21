package com.learncity.backend.tutor.search;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiClass;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.ApiReference;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;
import com.learncity.backend.common.BaseConfigEndpoint;
import com.learncity.backend.common.account.create.Account;
import com.learncity.backend.common.account.create.GenericLearnerProfileVer1;
import com.learncity.backend.tutor.account.create.TutorAccount;
import com.learncity.backend.tutor.account.create.TutorProfileVer1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by DJ on 11/4/2016.
 */

@Api(
        name = "tutorApi",
        title = "Tutor API"
)
@ApiReference(BaseConfigEndpoint.class)
@ApiClass(
        resource = "searchTutorAccount"
)
public class SearchTutorsEndpoint {

    private static final int DEFAULT_LIST_LIMIT = 100;

    private static final Logger logger = Logger.getLogger(SearchTutorsEndpoint.class.getName());

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Account.class);
    }

    private QueryResultIterator < Account > bufferQueryIterator;
    private QueryResultIterator < Account > queryIterator;

    /**
     * List all entities as per the query.
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(name = "searchTutors",
            httpMethod = ApiMethod.HttpMethod.POST)
    public CollectionResponse < TutorAccount > searchTutors(SearchTutorsQuery searchTutorsQuery, @Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {

        logger.info("Search Query: " + searchTutorsQuery);

        List < TutorAccount > tutorAccounts = new ArrayList < TutorAccount > (50);

        long noOfAccountsChecked = 0;
        long totalNoOfAccountsSearched = 0;
        long resultAccounts = 0;
        boolean stop = false;
        boolean shouldSkip = false;

        //Load the profiles initially
        queryIterator = loadTutorAccounts(cursor, 100);

        // We aim to just return minimum 30 and max 100 profiles for a single query.
        while (resultAccounts <= 30 && queryIterator.hasNext()) {
            shouldSkip = false;
            TutorAccount account = (TutorAccount) queryIterator.next();

            TutorProfileVer1 profile = account.getProfile();
            logger.info("Profile's email ID: " + profile.getEmailID());
            logger.info("Profile's Status: " + profile.getCurrentStatus());
            List < String > tutorTypes = null;
            List < String > disciplines = null;
            if (profile == null) {
                // Severe problem. AN ACCOUNT EXISTS WITHOUT A VALID PROFILE!!!
                logger.severe("AN ACCOUNT EXISTS WITHOUT A VALID PROFILE!!!");
                shouldSkip = true;
            } else {
                String[] p;
                if ((p = profile.getTutorTypes()) == null) {
                    // This profile is useless without valid Tutor types. SKIP.
                    logger.warning("This Tutor Account exists without Tutor Types: \n" + profile.toString());
                    shouldSkip = true;
                } else {
                    tutorTypes = Arrays.asList(p);
                }
                String[] d;
                if ((d = profile.getDisciplines()) == null) {
                    // This profile is useless without valid Disciplines. SKIP.
                    logger.warning("This Tutor Account exists without any Disciplines to teach: \n" + profile.toString());
                    shouldSkip = true;
                } else {
                    disciplines = Arrays.asList(d);
                }
            }
            boolean done = false;
            if (!shouldSkip) {
                for (String sub: searchTutorsQuery.getSubjects()) {
                    if (disciplines.contains(sub)) {
                        for (String type: searchTutorsQuery.getTutorTypes()) {
                            if (tutorTypes.contains(type)) {
                                logger.info("HELLO");
                                account = TutorAccount.TutorAccountResponseView.normalize(searchTutorsQuery.getAccountResponseView(), account);
                                tutorAccounts.add(account);
                                if (account != null) {
                                    resultAccounts++;
                                }
                                done = true;
                                break;
                            }
                        }
                    }
                    if(done){
                        break;
                    }

                }
            }
            noOfAccountsChecked++;
            totalNoOfAccountsSearched++;

            // Have we gotten >= 30 results
            if (resultAccounts >= 30) {
                // TODO: Save the rest of results into Session for retrieval later.
                break;
            }

            // If we have checked 50 accounts, load more in advanced
            //                if(noOfAccountsChecked == 50){
            //                    bufferQueryIterator = loadTutorAccounts(queryIterator.getCursor().toWebSafeString(), 100);
            //                }
            // The profiles provided have been checked and we still haven't
            // gotten >=30 results.
            if (!queryIterator.hasNext()) {
                // But, if we did check 50 accounts, we must have loaded more
                // accounts. Time to check them.
                //                queryIterator = bufferQueryIterator;
                queryIterator = loadTutorAccounts(queryIterator.getCursor().toWebSafeString(), 100);
                noOfAccountsChecked = 0;
            }
        }

        logger.info("Total No of Accounts searched: " + totalNoOfAccountsSearched);
        logger.info("No of Profile results: " + resultAccounts);

        //Log the list
        logger.info("Profiles results: " + tutorAccounts + "");
        return CollectionResponse. < TutorAccount > builder().setItems(tutorAccounts).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private QueryResultIterator < Account > loadTutorAccounts(@Nullable String cursor, @Nullable Integer limit) {

        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;

        Query < Account > query = ofy().load()
                .type(Account.class)
                .filter("accountStatus", GenericLearnerProfileVer1.STATUS_TUTOR)
                .limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        return query.iterator();
    }

}