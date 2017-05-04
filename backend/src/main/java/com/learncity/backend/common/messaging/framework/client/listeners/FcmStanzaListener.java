package com.learncity.backend.common.messaging.framework.client.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learncity.backend.common.messaging.framework.message.listeners.AbstractFcmMessageListener;
import com.learncity.backend.common.messaging.framework.message.model.incoming.DeliveryReceiptOrControlMessage;
import com.learncity.backend.common.messaging.framework.message.model.incoming.DownstreamMessageResponse;
import com.learncity.backend.common.messaging.framework.message.model.incoming.UpstreamMessage;
import com.learncity.backend.common.messaging.framework.message.util.source_viewers.impl.FcmMessageSourceViewer;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by DJ on 4/26/2017.
 */
public class FcmStanzaListener implements StanzaListener {

    private static final Logger logger = Logger.getLogger(FcmStanzaListener.class.getSimpleName());

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Listeners for all relevant messages.
    private AbstractFcmMessageListener<DeliveryReceiptOrControlMessage> deliveryReceiptOrControlMessageListener;
    private AbstractFcmMessageListener<UpstreamMessage> upStreamMessageListener;
    private AbstractFcmMessageListener<DownstreamMessageResponse> downstreamMessageResponseListener;

    @Override
    public void processStanza(Stanza incomingStanza) throws SmackException.NotConnectedException, InterruptedException {
        logger.log(Level.INFO, "Received: " + incomingStanza.toXML());

        final FcmMessageSourceViewer fcmMessageSourceViewer = new FcmMessageSourceViewer((Message) incomingStanza);

        // Now, we need to extract the JSON as a POJO. For that, if we
        // get it directly from Stanza, we would be performing double
        // calculations. How?
        // Well, won't the SourceViewer's user might want to get it from
        // SourceViewer later..? For that the same calc. will have to be performed again.
        // So, lets ask the SourceViewer for it in the first place.
        Map<String, Object> sourceAsMap = fcmMessageSourceViewer.getSourceAsMap();
        String sourceAsString = fcmMessageSourceViewer.getSourceAsJSONString();

        // Check for which Message type warrants what POJO creation?

        String messageTypeString = (String)sourceAsMap.get("message_type");
        // For "ack" message type
        if(messageTypeString == null){
            // UpstreamMessage type

            UpstreamMessage response = null;
            if(upStreamMessageListener != null){
                try {
                    response = objectMapper.readValue(sourceAsString, UpstreamMessage.class);
                } catch (IOException e) {
                    e.printStackTrace();
                    // TODO: In this case, it is most appropriate to inform the Sender about this
                    // depending on the importance of the message sent. Handle it.
                }
                upStreamMessageListener.setSource(fcmMessageSourceViewer);
                upStreamMessageListener.onReceiveMessage(response);
            }
        }
        else if(messageTypeString.equals("ack") || messageTypeString.equals("nack")){
            // A DownstreamMessageResponse type message

            DownstreamMessageResponse response = null;
            if(downstreamMessageResponseListener != null){
                try {
                    response = objectMapper.readValue(sourceAsString, DownstreamMessageResponse.class);
                } catch (IOException e) {
                    e.printStackTrace();
                    // TODO: In this case, it is most appropriate to inform the Sender about this
                    // depending on the importance of the message sent. Handle it.
                }
                downstreamMessageResponseListener.setSource(fcmMessageSourceViewer);
                downstreamMessageResponseListener.onReceiveMessage(response);
            }
        }
        else if(messageTypeString.equals("receipt") || messageTypeString.equals("control")){
            // Ccs to App server messages, i.e, DeliveryReceiptOrControlMessage type

            DeliveryReceiptOrControlMessage response = null;
            if(deliveryReceiptOrControlMessageListener != null){
                try {
                    response = objectMapper.readValue(sourceAsString, DeliveryReceiptOrControlMessage.class);
                } catch (IOException e) {
                    e.printStackTrace();
                    // TODO: In this case, it is most appropriate to inform the Sender about this
                    // depending on the importance of the message sent. Handle it.
                }
                deliveryReceiptOrControlMessageListener.setSource(fcmMessageSourceViewer);
                deliveryReceiptOrControlMessageListener.onReceiveMessage(response);
            }
        }
        else{
            // Unknown type - Does App server have a new one now?
            // ...Or, it is somehow a truly unknown message?
            logger.severe("An unknown message type received from CCS. Does" +
                    "Firebase have a new type we don't know about?...Or, if not then" +
                    "it is truly an unknown type.");
            throw new RuntimeException("Unknown Message type received from Ccs.");
        }
    }

    // Getters and Setters----------------------------------------------------------------------------------------------

    public AbstractFcmMessageListener<DeliveryReceiptOrControlMessage> getDeliveryReceiptOrControlMessageListener() {
        return deliveryReceiptOrControlMessageListener;
    }

    public void setDeliveryReceiptOrControlMessageListener(AbstractFcmMessageListener<DeliveryReceiptOrControlMessage> deliveryReceiptOrControlMessageListener) {
        this.deliveryReceiptOrControlMessageListener = deliveryReceiptOrControlMessageListener;
    }

    public void removeDeliveryReceiptOrControlMessageListener(AbstractFcmMessageListener<DeliveryReceiptOrControlMessage> deliveryReceiptOrControlMessageListener) {
        this.deliveryReceiptOrControlMessageListener = null;
    }

    public AbstractFcmMessageListener<UpstreamMessage> getUpStreamMessageListener() {
        return upStreamMessageListener;
    }

    public void setUpStreamMessageListener(AbstractFcmMessageListener<UpstreamMessage> upStreamMessageListener) {
        this.upStreamMessageListener = upStreamMessageListener;
    }

    public void removeUpStreamMessageListener(AbstractFcmMessageListener<UpstreamMessage> upStreamMessageListener) {
        this.upStreamMessageListener = null;
    }

    public AbstractFcmMessageListener<DownstreamMessageResponse> getDownstreamMessageResponseListener() {
        return downstreamMessageResponseListener;
    }

    public void setDownstreamMessageResponseListener(AbstractFcmMessageListener<DownstreamMessageResponse> downstreamMessageResponseListener) {
        this.downstreamMessageResponseListener = downstreamMessageResponseListener;
    }

    public void removeDownstreamMessageResponseListener(AbstractFcmMessageListener<DownstreamMessageResponse> downstreamMessageResponseListener) {
        this.downstreamMessageResponseListener = null;
    }
}
