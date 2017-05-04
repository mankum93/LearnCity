package com.learncity.backend.common.messaging.framework.client.listeners;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by DJ on 5/3/2017.
 */
public class LoggedConnectionListener implements ConnectionListener {

    private static final Logger logger = Logger.getLogger(LoggedConnectionListener.class.getSimpleName());

    @Override
    public void connected(XMPPConnection connection) {
        logger.log(Level.INFO, "Connection successful ...");
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        logger.log(Level.INFO, "Authenticated successful ...");
    }

    @Override
    public void reconnectionSuccessful() {
        logger.log(Level.INFO, "Reconnection successful ...");
    }

    @Override
    public void reconnectionFailed(Exception e) {
        logger.log(Level.INFO, "Reconnection failed: ", e.getMessage());
    }

    @Override
    public void reconnectingIn(int seconds) {
        logger.log(Level.INFO, "Reconnecting in %d secs", seconds);
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        logger.log(Level.INFO, "Connection closed on error");
    }

    @Override
    public void connectionClosed() {
        logger.log(Level.INFO, "Connection closed");
    }
}
