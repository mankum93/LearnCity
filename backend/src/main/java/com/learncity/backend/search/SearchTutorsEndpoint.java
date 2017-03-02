package com.learncity.backend.search;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;
import com.learncity.backend.account.create.TutorProfileVer1;
import com.learncity.backend.util.ArraysUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

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
public class SearchTutorsEndpoint {

    private static final int DEFAULT_LIST_LIMIT = 100;

    private static final Logger logger = Logger.getLogger(SearchTutorsEndpoint.class.getName());

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(TutorProfileVer1.class);
    }

    private QueryResultIterator<TutorProfileVer1> bufferQueryIterator;
    private QueryResultIterator<TutorProfileVer1> queryIterator;

    /**
     * List all entities as per the query.
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(name = "searchTutors",
            httpMethod = ApiMethod.HttpMethod.POST)
    public CollectionResponse<TutorProfileVer1> searchTutors(SearchTutorsQuery searchTutorsQuery, @Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {

        logger.info("Search Query: " + searchTutorsQuery);

        List<TutorProfileVer1> tutorProfileList = new ArrayList<TutorProfileVer1>(20);

        int noOfProfilesChecked = 0;
        int resultProfiles = 0;
        boolean stop = false;

        //Load the profiles initially
        queryIterator = loadTutorProfiles(cursor, 100);

        while(resultProfiles < 20){
            while(noOfProfilesChecked != 100){
                if(queryIterator.hasNext()){
                    TutorProfileVer1 profile;

                    profile = queryIterator.next();

                    //TODO: Take care of the trimming as it should not be required ideally
                    List<String> tutorTypes = Arrays.asList(ArraysUtil.trimArray(profile.getTutorTypes()));
                    List<String> disciplines = Arrays.asList(ArraysUtil.trimArray(profile.getDisciplines()));
                    for(String sub : searchTutorsQuery.getSubjects()){
                        if(disciplines.contains(sub)){
                            for(String type : searchTutorsQuery.getTutorTypes()){
                                if(tutorTypes.contains(type)){
                                    tutorProfileList.add(TutorProfileVer1.TutorProfileResponseView.normalize(searchTutorsQuery.getTutorProfileResponseView(), profile));
                                    resultProfiles++;
                                }
                            }
                        }
                    }
                    noOfProfilesChecked++;
                }
                else{
                    stop = true;
                    logger.info("No of Profiles checked(no more items): " + noOfProfilesChecked);
                    //There are no more items to iterate over - break away
                    break;
                }
                if(noOfProfilesChecked == 50){
                    logger.info("No of Profiles checked(=50): " + noOfProfilesChecked);
                    break;
                }
            }
            //If we are out without being able to check 50 profiles then its the end of the list
            if(noOfProfilesChecked < 50){
                logger.info("No of Profiles checked(<50): " + noOfProfilesChecked);
                //Return whatever we have
                break;
            }
            else if(noOfProfilesChecked == 100){
                logger.info("No of Profiles checked(=100): " + noOfProfilesChecked);
                //Reset the count
                noOfProfilesChecked = 0;
                queryIterator = bufferQueryIterator;
            }
            else if(stop == true){
                break;
            }
            //Load the next 100 profiles in advance if 20 profiles haven't found still
            if(resultProfiles < 20){
                bufferQueryIterator = loadTutorProfiles(queryIterator.getCursor().toWebSafeString(), 100);
                if(!bufferQueryIterator.hasNext()){
                    break;
                }
            }

        }
        logger.info("No of Profile results: " + resultProfiles);

        //Log the list
        logger.info("Profiles searched: " + tutorProfileList + "");
        return CollectionResponse.<TutorProfileVer1>builder().setItems(tutorProfileList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private QueryResultIterator<TutorProfileVer1> loadTutorProfiles(@Nullable String cursor, @Nullable Integer limit){

        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;

        Query<TutorProfileVer1> query = ofy().load()
                .type(TutorProfileVer1.class)
                .limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        return query.iterator();
    }

}
