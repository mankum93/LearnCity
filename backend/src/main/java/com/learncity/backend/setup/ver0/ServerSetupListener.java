package com.learncity.backend.setup.ver0;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import com.learncity.backend.learner.account.create.LearnerProfileVer1;
import com.learncity.backend.tutor.account.create.TutorProfileVer1;
import com.learncity.backend.common.account.create.endpoints.ver1.LearnerProfileVer1PersistenceEndpoint;
import com.learncity.backend.common.account.create.endpoints.ver1.TutorProfileVer1PersistenceEndpoint;
import com.learncity.backend.util.ProfileUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import static com.learncity.backend.common.account.create.GenericLearnerProfileVer1.STATUS_LEARNER;
import static com.learncity.backend.common.account.create.GenericLearnerProfileVer1.STATUS_TUTOR;

/**
 * Created by DJ on 2/28/2017.
 */

public class ServerSetupListener implements ServletContextListener {


    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //Endpoint service
        final LearnerProfileVer1PersistenceEndpoint learnerEndpoint = new LearnerProfileVer1PersistenceEndpoint();
        final TutorProfileVer1PersistenceEndpoint tutorEndpoint = new TutorProfileVer1PersistenceEndpoint();

        //Retrieve the dummy profile data
        final LearnerProfileVer1[] learnerProfiles = (LearnerProfileVer1[]) ProfileUtils.getJSONToProfiles(STATUS_LEARNER);
        final TutorProfileVer1[] tutorProfiles = (TutorProfileVer1[]) ProfileUtils.getJSONToProfiles(STATUS_TUTOR);

        //Insert this data into the datastore
        ObjectifyService.run(new VoidWork() {
            @Override
            public void vrun() {
                for(LearnerProfileVer1 profile : learnerProfiles){
                    learnerEndpoint.insert(profile);
                }
                for(TutorProfileVer1 profile : tutorProfiles){
                    tutorEndpoint.insert(profile);
                }
            }
        });
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
