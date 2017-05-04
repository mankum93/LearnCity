package com.learncity.backend.common.messaging.framework;

import com.learncity.backend.common.messaging.framework.util.smackx.fcm.packet.FcmPacketExtension;

import java.util.logging.Logger;

/**
 * Constants class for constants and generic methods
 */

public final class Constants {

    private static Logger logger = Logger.getLogger(Constants.class.getSimpleName());

	static{
		// Inform about default port
		logger.info("Default FCM port has been set to 5236(for Testing).");
	}

	// For the FCM connection
	public static final String HOST_ADDRESS = "fcm-xmpp.googleapis.com";
    public static final String HOST = "fcm-xmpp.googleapis.com";
    public static final String DOMAIN = "googleapis.com";

	// 5236 is for testing purposes; 5235 is for production;
    // Default is set to TESTING.
    public static final int FCM_PORT_FOR_TESTING = 5236;
    public static final int FCM_PORT_FOR_PRODUCTION = 5235;
    public static final int FCM_PORT = FCM_PORT_FOR_TESTING;

	public static final String FCM_ELEMENT = FcmPacketExtension.ELEMENT;
	public static final String FCM_NAMESPACE = FcmPacketExtension.NAMESPACE;
	public static final String FCM_SERVER_CONNECTION_ENDPOINT = "gcm.googleapis.com";

}
