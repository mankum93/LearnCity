package com.learncity.backend.common.messaging.framework.message.listeners;

import com.learncity.backend.common.messaging.framework.message.model.incoming.DownstreamMessageResponse;

import java.util.logging.Logger;

/**
 * Created by DJ on 5/4/2017.
 */
public class LoggedDownstreamMessageResponseListener extends AbstractDownstreamMessageResponseListener {


    private static final Logger logger = Logger.getLogger(LoggedDownstreamMessageResponseListener.class.getSimpleName());


    public void handleAckReceipt(DownstreamMessageResponse downstreamMessageResponse) {
        logger.info("Ack receipt received for a Downstream Message sent.");
    }

    public void handleServerFailure(DownstreamMessageResponse downstreamMessageResponse) {
        // TODO: Resend the message
        logger.info("Server error: " + downstreamMessageResponse.getError() + " -> " + downstreamMessageResponse.getErrorDescription());

    }

    public void handleUnrecoverableFailure(DownstreamMessageResponse downstreamMessageResponse) {
        // TODO: handle the unrecoverable failure
        logger.info(
                "Unrecoverable error: " + downstreamMessageResponse.getError() + " -> " + downstreamMessageResponse.getErrorDescription());
    }

    public void handleConnectionDrainingFailure() {
        // TODO: handle the connection draining failure. Force reconnect?
        logger.info("FCM Connection is draining!");
    }
}
