package com.learncity.backend.util;

import com.fasterxml.uuid.UUIDType;
import com.fasterxml.uuid.impl.NameBasedGenerator;
import com.googlecode.objectify.annotation.Ignore;
import com.learncity.backend.common.account.create.Account;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by DJ on 6/28/2017.
 */

public final class IdUtils {

    private static final Logger logger = Logger.getLogger(IdUtils.class.getSimpleName());

    // Completely thread safe, name based, UUID generator singleton instance
    @Ignore
    private static NameBasedGenerator nameBasedUUIDGenerator;

    static {
        try{
            nameBasedUUIDGenerator = new NameBasedGenerator(NameBasedGenerator.NAMESPACE_OID,
                    MessageDigest.getInstance("SHA-1"), UUIDType.NAME_BASED_SHA1);
        }
        catch(NoSuchAlgorithmException nse){
            logger.severe("Check the algorithm used for generation of MessageDigest from the list" +
                    "of valid ones.");
            nse.printStackTrace();
        }
    }

    public static String getType5UUID(String name) {

        if(nameBasedUUIDGenerator != null){
            return nameBasedUUIDGenerator.generate(name).toString();
        }
        else{
            // If the name based UUID generator couldn't be initialized(NoSuchAlgorithmException)
            // instead of throwing a Runtime Exception, return a 000... UUID to indicate that.
            return new UUID(0L, 0L).toString();
        }
    }
}
