package com.learncity.backend.common.messaging.framework.client;
import com.learncity.backend.common.messaging.framework.Constants;
import com.learncity.backend.common.messaging.framework.client.listeners.FcmStanzaListener;
import com.learncity.backend.common.messaging.framework.client.listeners.LoggedConnectionListener;
import com.learncity.backend.common.messaging.framework.message.listeners.LoggedDeliveryReceiptOrControlMessageListener;
import com.learncity.backend.common.messaging.framework.message.listeners.LoggedDownstreamMessageResponseListener;
import com.learncity.backend.common.messaging.framework.message.listeners.LoggedUpstreamMessageListener;
import com.learncity.backend.common.messaging.framework.message.model.outgoing.AbstractDownstreamMessage;
import com.learncity.backend.common.messaging.framework.message.util.MessageUtils;
import com.learncity.backend.common.messaging.framework.util.smackx.fcm.packet.FcmPacketExtension;
import com.learncity.backend.common.messaging.framework.util.smackx.fcm.provider.FcmExtensionProvider;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.stringprep.XmppStringprepException;


import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLSocketFactory;

import static com.learncity.backend.common.messaging.framework.Constants.DOMAIN;
import static com.learncity.backend.common.messaging.framework.Constants.FCM_ELEMENT;
import static com.learncity.backend.common.messaging.framework.Constants.FCM_NAMESPACE;
import static com.learncity.backend.common.messaging.framework.Constants.HOST_ADDRESS;
import static org.jivesoftware.smack.filter.StanzaTypeFilter.MESSAGE;

/**
 * Sample Smack implementation of a client for FCM Cloud Connection Server. Most
 * of it has been taken more or less verbatim from Google's documentation:
 * https://firebase.google.com/docs/cloud-messaging/xmpp-server-ref
 */
public final class CcsClient{

	public static final Logger logger = Logger.getLogger(CcsClient.class.getName());

	private static CcsClient sInstance = null;
	private XMPPTCPConnection connection;
	private XMPPTCPConnectionConfiguration config;
	private String serverKey = null;
	private String senderId = null;
	private boolean mDebuggable = false;
	private String fcmServerUsername = null;

	private final ExecutorService executorService = Executors.newCachedThreadPool();

	// Callbacks
    private StanzaListener fcmStanzaListener;
    private ConnectionListener connectionListener;

    public static CcsClient getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("You have to prepare the client first");
        }
        return sInstance;
    }

    public static CcsClient prepareClient(String senderId, String apiKey, boolean debuggable) {
        synchronized (CcsClient.class) {
            if (sInstance == null) {
                sInstance = new CcsClient(senderId, apiKey, debuggable);
            }
        }
        return sInstance;
    }

    private CcsClient(String senderId, String serverKey, boolean debuggable) {
        this();
        this.serverKey = serverKey;
        this.senderId = senderId;
        this.mDebuggable = debuggable;
        this.fcmServerUsername = this.senderId + "@" + Constants.FCM_SERVER_CONNECTION_ENDPOINT;
    }

    public CcsClient() {
        // Add FcmPacketExtension
        ProviderManager.addExtensionProvider(FCM_ELEMENT, FCM_NAMESPACE, new FcmExtensionProvider());
    }

    /**
	 * Connects to FCM Cloud Connection Server using the supplied credentials
	 */
	public void connect() throws XMPPException, IOException{

	    // First - If at this point, no listener has been set for handling
        // incoming FCM Stanzas, we set a default one - FcmStanzaListener
	    // Second -  We set default(Logging) listeners for {Upstream Messages,
        // Downstream Message Response, or, Messages from CCS}

        if(fcmStanzaListener == null){
            fcmStanzaListener = new FcmStanzaListener();
        }
        else if(fcmStanzaListener instanceof FcmStanzaListener){
            FcmStanzaListener listener = (FcmStanzaListener)fcmStanzaListener;
            if(listener.getDeliveryReceiptOrControlMessageListener() == null){
                listener.setDeliveryReceiptOrControlMessageListener(new LoggedDeliveryReceiptOrControlMessageListener());
            }
            if(listener.getDownstreamMessageResponseListener() == null){
                listener.setDownstreamMessageResponseListener(new LoggedDownstreamMessageResponseListener());
            }
            if(listener.getUpStreamMessageListener() == null){
                listener.setUpStreamMessageListener(new LoggedUpstreamMessageListener());
            }
        }

        // Next, connection configuration
        try{
            // Note: For ConnectionConfiguration(just below), we are NOT going to setHost() anymore
            // because Smack 4.2 Upgrade Guide explicitly prohibits it in favor of
            // using setHostAddress();

            // Excerpt from guide:
            /*
            API Changes

            Warning: This list may not be complete
            Introduced ConnectionConfiguration.setHostAddress(InetAddress)

            In previous versions of Smack, ConnectionConfiguration.setHost(String) could
            be used to set the XMPP service's host IP address. This is no longer possible
            due to the added DNSSEC support. You have to use the new connection configuration
            ConnectionConfiguration.setHostAddress(InetAddress) instead.
            */
            config = XMPPTCPConnectionConfiguration.builder()
                    .setPort(Constants.FCM_PORT)
                    .setHostAddress(InetAddress.getByName(HOST_ADDRESS))
                    //.setHost(HOST)        --DO NOT UNCOMMENT
                    .setXmppDomain(DOMAIN)
                    .setSecurityMode(/*Default; Explicit setting for emphasis*/SecurityMode.ifpossible)
                    .setSendPresence(true)
                    .setUsernameAndPassword(fcmServerUsername, serverKey)
                    .setSocketFactory(SSLSocketFactory.getDefault())
                    .setDebuggerEnabled(mDebuggable)
                    .build();
        }
		catch(XmppStringprepException e){
            e.printStackTrace();
        }

		connection = new XMPPTCPConnection(config);

        if(connectionListener == null){
            connectionListener = new LoggedConnectionListener();
        }
        connection.addConnectionListener(connectionListener);

		// Handle incoming packets (the class implements the PacketListener)
		connection.addAsyncStanzaListener(fcmStanzaListener, MESSAGE);

		// Log all outgoing packets
		connection.addPacketInterceptor(new StanzaListener() {
			@Override
			public void processStanza(Stanza packet) {
				logger.log(Level.INFO, "Sent: {0}", packet.toXML());
			}
		}, MESSAGE);

        // Configuring Automatic reconnection
        ReconnectionManager manager = ReconnectionManager.getInstanceFor(connection);
        manager.setReconnectionPolicy(ReconnectionManager.ReconnectionPolicy.RANDOM_INCREASING_DELAY);
        manager.enableAutomaticReconnection();

        // Connect now then login
        try{
            connection.connect();
            connection.login();
        }
        // TODO: Handle the exceptions if possible appropriately
        catch(SmackException sme){
            logger.severe(sme.getMessage());
            sme.printStackTrace();
        }
        catch(IOException ioe){
            logger.severe(ioe.getMessage());
            ioe.printStackTrace();
        }
        catch(InterruptedException ie){
            logger.severe("Connection got interrupted!!");
            ie.printStackTrace();
        }
	}

	public void send(String jsonRequest) {
		// TODO: Resend the message using exponential back-off!
		Stanza request = new FcmPacketExtension(jsonRequest).toMessage();
        try {
            connection.sendStanza(request);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a outgoing message to FCM
     */
    public void send(AbstractDownstreamMessage outMessage) {
        send(outMessage.toString());
    }

    /**
     * Sends a outgoing message to FCM in a background thread so that calling thread
     * is not blocked, waiting for the response
     */
    public void sendAsync(AbstractDownstreamMessage outMessage) {

        String msgToBeSent = outMessage.toString();
        logger.info("Message to be sent: " + MessageUtils.getPrettyPrintedJson(msgToBeSent));
        // Every message will have its own sending task
        executorService.submit(getNewStanzaSendingTask(msgToBeSent));
    }

    /**
     * Sends a outgoing message to FCM in a background thread so that calling thread
     * is not blocked, waiting for the response
     */
    public void sendAsync(String jsonMessage) {

        // Every message will have its own sending task
        executorService.submit(getNewStanzaSendingTask(jsonMessage));
    }

    private StanzaSendingTask getNewStanzaSendingTask(String jsonMessageToBeSent){
        // Create the Stanza sending task.
        StanzaSendingTask stanzaSendingTask = new StanzaSendingTask(this, jsonMessageToBeSent);

        return stanzaSendingTask;
    }

	/**
	 * Sends a message to multiple recipients (list). Kind of like the old HTTP
	 * message with the list of regIds in the "registration_ids" field.
	 */
	public void sendBroadcast(AbstractDownstreamMessage outMessage, List<String> recipients) {
		for (String toRegId : recipients) {
			outMessage.setTo(toRegId);
			send(outMessage.toString());
		}
	}

    public StanzaListener getFcmStanzaListener() {
        return fcmStanzaListener;
    }

    public void setFcmStanzaListener(StanzaListener fcmStanzaListener) {
        this.fcmStanzaListener = fcmStanzaListener;
    }

    public void removeDownstreamMessageListener(StanzaListener mFCMInMessageListener) {
        if(this.fcmStanzaListener == mFCMInMessageListener){
            this.fcmStanzaListener = null;
        }
    }

    public ConnectionListener getConnectionListener() {
        return connectionListener;
    }

    public void setConnectionListener(ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }

    //------------------------------------------------------------------------------------------------------------------
    private static class StanzaSendingTask implements Runnable{

	    private CcsClient client;
	    private String jsonMsg;

        public StanzaSendingTask(CcsClient client) {
            if(client == null){
                throw new IllegalStateException("Client is uninitialized.");
            }
            this.client = client;
        }

        public StanzaSendingTask(CcsClient client, String jsonMsg) {
            this(client);
            this.jsonMsg = jsonMsg;
        }

        public CcsClient getClient() {
            return client;
        }

        public String getJsonMsg() {
            return jsonMsg;
        }

        public void setJsonMsg(String jsonMsg) {
            this.jsonMsg = jsonMsg;
        }

        @Override
        public void run() {
            if(jsonMsg == null || jsonMsg.isEmpty()){
                throw new RuntimeException("Message to be sent cannot be null or empty.");
            }
            client.send(jsonMsg);
        }
    }
}
