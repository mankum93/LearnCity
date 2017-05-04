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

    private QueryResultIterator<Account> bufferQueryIterator;
    private QueryResultIterator<Account> queryIterator;

    /**
     * List all entities as per the query.
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(name = "searchTutors",
            httpMethod = ApiMethod.HttpMethod.POST)
    public CollectionResponse<TutorAccount> searchTutors(SearchTutorsQuery searchTutorsQuery, @Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {

        logger.info("Search Query: " + searchTutorsQuery);

        List<TutorAccount> tutorAccounts = new ArrayList<TutorAccount>(50);

        int noOfAccountsChecked = 0;
        int resultAccounts = 0;
        boolean stop = false;

        //Load the profiles initially
        queryIterator = loadTutorAccounts(cursor, 100);

        while(resultAccounts < 20){
            while(noOfAccountsChecked != 100){
                if(queryIterator.hasNext()){
                    TutorAccount account = (TutorAccount) queryIterator.next();

                    List<String> tutorTypes = Arrays.asList(account.getProfile().getTutorTypes());
                    List<String> disciplines = Arrays.asList(account.getProfile().getDisciplines());
                    for(String sub : searchTutorsQuery.getSubjects()){
                        if(disciplines.contains(sub)){
                            for(String type : searchTutorsQuery.getTutorTypes()){
                                if(tutorTypes.contains(type)){
                                    account = TutorAccount.TutorAccountResponseView.normalize(searchTutorsQuery.getAccountResponseView(), account);
                                    tutorAccounts.add(account);
                                    if(account != null){
                                        resultAccounts++;
                                    }
                                }
                            }
                        }
                    }
                    noOfAccountsChecked++;
                }
                else{
                    stop = true;
                    logger.info("No of Profiles checked(no more items): " + noOfAccountsChecked);
                    //There are no more items to iterate over - break away
                    break;
                }
                if(noOfAccountsChecked == 50){
                    logger.info("No of Profiles checked(=50): " + noOfAccountsChecked);
                    break;
                }
            }
            //If we are out without being able to check 50 profiles then its the end of the list
            if(noOfAccountsChecked < 50){
                logger.info("No of Profiles checked(<50): " + noOfAccountsChecked);
                //Return whatever we have
                break;
            }
            else if(noOfAccountsChecked == 100){
                logger.info("No of Profiles checked(=100): " + noOfAccountsChecked);
                //Reset the count
                noOfAccountsChecked = 0;
                queryIterator = bufferQueryIterator;
            }
            else if(stop == true){
                break;
            }
            //Load the next 100 profiles in advance if 20 profiles haven't found still
            if(resultAccounts < 20){
                bufferQueryIterator = loadTutorAccounts(queryIterator.getCursor().toWebSafeString(), 100);
                if(!bufferQueryIterator.hasNext()){
                    break;
                }
            }

        }
        logger.info("No of Profile results: " + resultAccounts);

        //Log the list
        logger.info("Profiles searched: " + tutorAccounts + "");
        return CollectionResponse.<TutorAccount>builder().setItems(tutorAccounts).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private QueryResultIterator<Account> loadTutorAccounts(@Nullable String cursor, @Nullable Integer limit){

        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;

        Query<Account> query = ofy().load()
                .type(Account.class)
                .filter("accountStatus", GenericLearnerProfileVer1.STATUS_TUTOR)
                .limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        return query.iterator();
    }

}
