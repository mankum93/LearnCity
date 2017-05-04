package com.learncity.backend.common.messaging.framework.message.listeners;

import com.learncity.backend.common.messaging.framework.message.model.incoming.DeliveryReceiptOrControlMessage;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by DJ on 4/26/2017.
 */
public abstract class AbstractDeliveryReceiptOrControlMessageListener extends AbstractFcmMessageListener<DeliveryReceiptOrControlMessage> {

    private static final Logger logger = Logger.getLogger(AbstractDeliveryReceiptOrControlMessageListener.class.getSimpleName());

    @Override
    public void onReceiveMessage(DeliveryReceiptOrControlMessage deliveryReceiptOrControlMessage) {

        if(deliveryReceiptOrControlMessage.getMessageType().equals("receipt")){
            handleDeliveryReceipt(deliveryReceiptOrControlMessage);
        }
        else{
            handleControlMessage(deliveryReceiptOrControlMessage);
        }
    }

    /**
     * Handles a Delivery Receipt message from FCM (when a device confirms that
     * it received a particular message)
     */
    protected abstract void handleDeliveryReceipt(DeliveryReceiptOrControlMessage deliveryReceiptOrControlMessage);

    /**
     * Handles a Control message from FCM
     */
    protected void handleControlMessage(DeliveryReceiptOrControlMessage deliveryReceiptOrControlMessage) {

        String controlType = deliveryReceiptOrControlMessage.getControlType();

        if (controlType.equals("CONNECTION_DRAINING")) {
            handleConnectionDrainingFailure();
        } else {
            logger.log(Level.INFO, "Received unknown FCM Control message: " + controlType);
        }
    }

    protected abstract void handleConnectionDrainingFailure();
}
