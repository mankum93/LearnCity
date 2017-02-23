package com.learncity.backend.account.create;


import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Subclass;

/**
 * Created by DJ on 10/22/2016.
 */

@Entity
@Subclass(index = true)
public class LearnerProfileVer1 extends GenericLearnerProfileVer1 {

    //For serialization while storing to Db
    public LearnerProfileVer1(){
    }
}
