package com.learncity.backend.common.messaging.framework.message.util.generator;

import com.learncity.backend.common.messaging.framework.message.util.MessageUtils;

/**
 * Created by DJ on 5/2/2017.
 */
public class UUIDType1MessageIdGenerator implements Generator<String, Void> {

    @Override
    public String next(Void v) {
        return MessageUtils.getUniqueMessageId();
    }
}
