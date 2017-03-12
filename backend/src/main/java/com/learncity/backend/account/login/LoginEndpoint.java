package com.learncity.backend.account.login;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.learncity.backend.account.LearnerUserIDEntity;
import com.learncity.backend.account.create.GenericLearnerProfileVer1;
import com.learncity.backend.account.create.LearnerProfileVer1;
import com.learncity.backend.account.create.TutorProfileVer1;

import java.util.logging.Logger;

import javax.annotation.Nonnull;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by DJ on 2/17/2017.
 */

@Api(
        name = "loginApi",
        version = "v1",
        title = "Login API",
        resource = "login",
        namespace = @ApiNamespace(
                ownerDomain = "learncity.com",
                ownerName = "Learncity",
                packagePath = ""
        )
)
public class LoginEndpoint {

    private static final Logger logger = Logger.getLogger(LoginEndpoint.class.getName());

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(TutorProfileVer1.class);
        ObjectifyService.register(GenericLearnerProfileVer1.class);
        ObjectifyService.register(LearnerProfileVer1.class);
    }

    /**
     * Returns a profile data if the user exists or 401 if not.
     * @return a response that encapsulates the profile response
     */
    @ApiMethod(name = "login",
            httpMethod = ApiMethod.HttpMethod.POST)
    public GenericLearnerProfileVer1 login(@Nonnull LoginDetails loginDetails)
    throws NotFoundException, UnauthorizedException{

        //Response object
        GenericLearnerProfileVer1 response;

        LearnerUserIDEntity entity;
        Key key;
        //Query the User ID entity for existing AC first
        if(LearnerUserIDEntity.checkIfAccountExists(loginDetails.getEmailID())){
            entity = LearnerUserIDEntity.getUserIDEntity(loginDetails.getEmailID());
        }
        else{
            //Account doesn't exist
            throw new NotFoundException(loginDetails.getEmailID());
        }
        //Now the Key for that Email ID
        key = entity.getEntityKey();

        //Now, search the entity for this key
        response = (GenericLearnerProfileVer1) ofy().load().key(key).now();

        //Check if the password matches
        if(!response.getPassword().equals(loginDetails.getPassword())){
            throw new UnauthorizedException("Password doesn't match against the User ID");
        }

        return response;
    }

}
