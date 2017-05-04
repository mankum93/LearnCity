package com.learncity.backend.common.messaging.framework.message.listeners;

/**
 * Created by DJ on 5/4/2017.
 */
public interface GenericFcmMessageListener<Message> {

    void onReceiveMessage(Message m);
}
