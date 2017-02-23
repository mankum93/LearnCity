package com.learncity.backend.account;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by DJ on 2/18/2017.
 */
@Entity
public class LearnerUserIDEntity<T> {
    @Id @Index String emailID;
    Key<T> entityKey;

    public LearnerUserIDEntity(String emailID, Key<T> key){
        if(emailID == null){
            throw new NullPointerException("Email ID can't be null to create an entity");
        }
        if(key == null){
            throw new NullPointerException("Key can't be null to create an entity");
        }
        this.emailID = emailID;
        this.entityKey = key;
    }
    //Default constructor for serialization
    private LearnerUserIDEntity(){

    }

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        if(emailID == null){
            throw new NullPointerException("Email ID can't be null to create an entity");
        }
        this.emailID = emailID;
    }

    public Key<T> getEntityKey() {
        return entityKey;
    }

    public void setEntityKey(Key<T> entityKey) {
        if(entityKey == null){
            throw new NullPointerException("Key can't be null to create an entity");
        }
        this.entityKey = entityKey;
    }

    //-----------------------------------------------------------------------------------------------------------------------

    public static boolean checkIfAccountExists(String mEmailID){
        return checkIfUserIDEntityExists(mEmailID) != null;
    }

    public static <T> void updateUserIDEntity(String emailID, Class<T> clazz) throws NotFoundException {
        Key<T> key = Key.create(clazz, emailID);
        updateUserIDEntity(emailID, key);
    }

    public static <T> void updateUserIDEntity(String emailID, Key<T> key) throws NotFoundException{
        if(emailID == null){
            throw new NullPointerException("Email ID can't be null to update an entity");
        }
        if(key == null){
            throw new NullPointerException("Key can't be null to update an entity");
        }
        LearnerUserIDEntity UserIDEntity = checkIfUserIDEntityExists(emailID);

        if(UserIDEntity != null){
            //Update the entity
            ofy().save().entity(UserIDEntity).now();
        }
        else{
            throw new NotFoundException(key);
        }
    }

    private static LearnerUserIDEntity checkIfUserIDEntityExists(String emailID){
        return ofy().load().type(LearnerUserIDEntity.class).id(emailID).now();
    }

    public static <T> void updateUserIDEntity(LearnerUserIDEntity<T> entity) throws NotFoundException{
        updateUserIDEntity(entity.getEmailID(), entity.getEntityKey());
    }

    public static <T> void createUserIDEntity(String emailID, Class<T> clazz){

        LearnerUserIDEntity<T> entity = new LearnerUserIDEntity<T>(emailID, Key.create(clazz, emailID));
        ofy().save().entity(entity).now();
    }

    public static <T> void createUserIDEntity(String emailID, Key<T> key){

        LearnerUserIDEntity<T> entity = new LearnerUserIDEntity<T>(emailID, key);
        ofy().save().entity(entity).now();
    }

    public static <T> void createUserIDEntity(LearnerUserIDEntity<T> entity){

        if(entity == null){
            throw new NullPointerException("Cannot create a null entity");
        }
        ofy().save().entity(entity).now();
    }

    public static LearnerUserIDEntity deleteUserIDEntity(String emailID){
        LearnerUserIDEntity entity = checkIfUserIDEntityExists(emailID);
        if(entity == null){
            return null;
        }
        else{
            //Delete it and return the deleted object
            ofy().delete().type(LearnerUserIDEntity.class).id(emailID).now();
        }
        return entity;
    }

    public static LearnerUserIDEntity deleteUserIDEntity(LearnerUserIDEntity entity){
        if(entity == null){
            throw new NullPointerException("Cannot delete a null entity");
        }
        return deleteUserIDEntity(entity.getEmailID());
    }

    public static LearnerUserIDEntity getUserIDEntity(String emailID){
        if(emailID == null){
            throw new NullPointerException("Email ID can't be null to create an entity");
        }
        return checkIfUserIDEntityExists(emailID);
    }
}
