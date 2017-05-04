package com.learncity.backend.common.messaging.framework.message.listeners;

import com.learncity.backend.common.messaging.framework.client.CcsClient;
import com.learncity.backend.common.messaging.framework.message.model.incoming.UpstreamMessage;

import java.util.logging.Logger;

/**
 * Created by DJ on 5/4/2017.
 */
public abstract class AbstractUpstreamMessageListener extends AbstractFcmMessageListener<UpstreamMessage> {

    private static final Logger logger = Logger.getLogger(AbstractUpstreamMessageListener.class.getSimpleName());

    @Override
    public void onReceiveMessage(UpstreamMessage upstreamMessage) {

        logger.info("Upstream message received: \n" + this.viewSource().toString());

        processUpstreamMessage(upstreamMessage);

        //final String action = inMessage.getDataPayload().get(Constants.PAYLOAD_ATTRIBUTE_ACTION);
        /*if (action != null) {
            PayloadProcessor processor = ProcessorFactory.getProcessor(action);
            processor.handleMessage(inMessage);
        }*/

        // Send ACK to Ccs
        logger.info("Sending Ack to Ccs for the Upstream Message received...");
        CcsClient.getInstance().sendAsync(upstreamMessage.getAcknowledgement().toString());
    }

    protected abstract void processUpstreamMessage(UpstreamMessage upstreamMessage);
}
