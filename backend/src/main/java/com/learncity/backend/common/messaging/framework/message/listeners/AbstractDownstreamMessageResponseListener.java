package com.learncity.backend.common.messaging.framework.message.listeners;

import com.learncity.backend.common.messaging.framework.message.model.incoming.DownstreamMessageResponse;

import java.util.logging.Logger;

import static com.learncity.backend.common.messaging.framework.message.model.incoming.DownstreamMessageResponse.ErrorResponseCodes.*;
import static com.learncity.backend.common.messaging.framework.message.model.incoming.DownstreamMessageResponse.ErrorResponseCodes.CONNECTION_DRAINING;
import static com.learncity.backend.common.messaging.framework.message.model.incoming.DownstreamMessageResponse.ErrorResponseCodes.TOPICS_MESSAGE_RATE_EXCEEDED;

/**
 * Created by DJ on 5/4/2017.
 */
public abstract class AbstractDownstreamMessageResponseListener extends AbstractFcmMessageListener<DownstreamMessageResponse> {

    private static final Logger logger = Logger.getLogger(AbstractDownstreamMessageResponseListener.class.getSimpleName());

    @Override
    public void onReceiveMessage(DownstreamMessageResponse downstreamMessageResponse) {

        if(downstreamMessageResponse.getMessageType().equals("ack")){
            handleAckReceipt(downstreamMessageResponse);
        }
        else{
            handleNackReceipt(downstreamMessageResponse);
        }
    }

    /**
     * Handles an ACK message from FCM
     */
    protected abstract void handleAckReceipt(DownstreamMessageResponse downstreamMessageResponse);

    /**
     * Handles a NACK message from FCM
     */
    public void handleNackReceipt(DownstreamMessageResponse downstreamMessageResponse) {
        logger.info("Nack receipt received for a Downstream Message sent.");
        String errorCode = downstreamMessageResponse.getError();

        if (errorCode == null) {
            logger.info("Received null FCM Error Code");
            return;
        }

        switch (errorCode) {
            case INVALID_JSON:
                handleUnrecoverableFailure(downstreamMessageResponse);
                break;
            case BAD_REGISTRATION:
                handleUnrecoverableFailure(downstreamMessageResponse);
                break;
            case DEVICE_UNREGISTERED:
                handleUnrecoverableFailure(downstreamMessageResponse);
                break;
            case BAD_ACK:
                handleUnrecoverableFailure(downstreamMessageResponse);
                break;
            case SERVICE_UNAVAILABLE:
                handleServerFailure(downstreamMessageResponse);
                break;
            case INTERNAL_SERVER_ERROR:
                handleServerFailure(downstreamMessageResponse);
                break;
            case DEVICE_MESSAGE_RATE_EXCEEDED:
                handleUnrecoverableFailure(downstreamMessageResponse);
                break;
            case TOPICS_MESSAGE_RATE_EXCEEDED:
                handleUnrecoverableFailure(downstreamMessageResponse);
                break;
            case CONNECTION_DRAINING:
                handleConnectionDrainingFailure();
                break;
            default:
                logger.info("Received unknown FCM Error Code: " + errorCode);
        }
    }

    protected abstract void handleServerFailure(DownstreamMessageResponse downstreamMessageResponse);

    protected abstract void handleUnrecoverableFailure(DownstreamMessageResponse downstreamMessageResponse);

    protected abstract void handleConnectionDrainingFailure();
}
