package com.learncity.backend.util;

import com.learncity.backend.common.messaging.Message;
import com.learncity.backend.common.messaging.framework.message.model.outgoing.AbstractDownstreamMessage;

import static com.learncity.backend.common.messaging.Message.CHAT_TEXT_MESSAGE;
import static com.learncity.backend.common.messaging.Message.FCM_DATA_MESSAGE;
import static com.learncity.backend.common.messaging.Message.FCM_NOTIFICATION_MESSAGE;
import static com.learncity.backend.common.messaging.Message.TUTORING_REQUEST;

/**
 * Created by DJ on 6/28/2017.
 */

public final class MessagingUtils {

    public static AbstractDownstreamMessage obtainFCMMessage(Message message){

        AbstractDownstreamMessage messageToBeSent;

        switch(message.getMessageType()){

            case TUTORING_REQUEST:
                //message.getNotificationMessage().put("messageType", "TUTORING_REQUEST");
                messageToBeSent = message.getNotificationMessage();
                break;
            case CHAT_TEXT_MESSAGE:
                //message.getNotificationMessage().put("messageType", "CHAT_TEXT_MESSAGE");
                messageToBeSent = message.getNotificationMessage();
                break;
            case FCM_DATA_MESSAGE:
                messageToBeSent = message.getDataMessage();
                break;
            case FCM_NOTIFICATION_MESSAGE:
                messageToBeSent = message.getNotificationMessage();
                break;
            default:
                throw new IllegalStateException("Invalid message type.");

        }
        messageToBeSent.setDeliveryReceiptRequested(true);

        return messageToBeSent;
    }
}
