package com.learncity.backend.common.messaging.framework.message.listeners;

import com.learncity.backend.common.messaging.framework.message.model.incoming.DeliveryReceiptOrControlMessage;

import java.util.logging.Logger;

/**
 * Created by DJ on 5/4/2017.
 */
public class LoggedDeliveryReceiptOrControlMessageListener extends AbstractDeliveryReceiptOrControlMessageListener {

    private static final Logger logger = Logger.getLogger(LoggedDeliveryReceiptOrControlMessageListener.class.getSimpleName());

    @Override
    protected void handleDeliveryReceipt(DeliveryReceiptOrControlMessage deliveryReceiptOrControlMessage) {
        logger.info("Delivery Receipt received.");
    }

    @Override
    protected void handleConnectionDrainingFailure() {
        logger.info("FCM Connection is draining!");
    }
}
