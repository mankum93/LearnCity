package com.learncity.backend.common.messaging.framework.message.listeners;

import com.learncity.backend.common.messaging.framework.message.model.incoming.UpstreamMessage;

import java.util.logging.Logger;

/**
 * Created by DJ on 5/4/2017.
 */
public class LoggedUpstreamMessageListener extends AbstractUpstreamMessageListener {

    private static final Logger logger = Logger.getLogger(LoggedUpstreamMessageListener.class.getSimpleName());

    @Override
    protected void processUpstreamMessage(UpstreamMessage upstreamMessage) {
        // Already logged!
    }
}
