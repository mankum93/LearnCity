package com.learncity.backend.common.messaging;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiClass;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiReference;
import com.google.api.server.spi.config.Named;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.learncity.backend.common.BaseConfigEndpoint;
import com.learncity.backend.common.account.create.endpoints.BaseLearnerEndpoint;
import com.learncity.backend.common.messaging.framework.message.model.outgoing.NotificationMessage;

import org.jivesoftware.smack.XMPPException;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static com.learncity.backend.common.messaging.Message.TUTORING_REQUEST;

/**
 * Created by DJ on 4/2/2017.
 */
@Api(
        name = "messagingApi",
        title = "Messaging API"
)
@ApiReference(BaseConfigEndpoint.class)
@ApiClass(
        resource = "sendMessage"
)
public class MessagingEndpoint {

    private static final Logger logger = Logger.getLogger(MessagingEndpoint.class.getSimpleName());

    private static final String MESSAGING_SERVICE_URI = "localhost:8090/send";
    private static final Map<String, String> requestHeaders = new HashMap<>();

    static {
        requestHeaders.put("Accept", "application/json");
        requestHeaders.put("Content-Type", "application/json");
    }

    @ApiMethod(
            name = "sendMessage",
            path = "messaging",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public void sendMessage(Message message){
        // Retrieve the Firebase Token of the Receiver
        String firebaseTokenReceiver = BaseLearnerEndpoint.getFirebaseToken(message.getTo());

        if(firebaseTokenReceiver == null){
            // Account doesn't exist
            logger.warning("Account with the User ID: " + message.getTo() + "doesn't exist");
            return;
        }
        // Send this message to the receiver now.
        if(message.getMessageType() == TUTORING_REQUEST){

        }
        /*else if(message.getMessageType().intValue() == TEXT_MESSAGE){
            msgContent = message.getTextMessage();
        }*/
        else{
            // Do nothing
            return;
        }

        // Build a FCM Notification Message
        NotificationMessage notificationMessage = NotificationMessage.Builder
                .newBuilderInstance(firebaseTokenReceiver, null)
                .setNotificationPayload(NotificationMessage.NotificationPayload.Builder
                        .newBuilder()
                        .setTitle("Tutoring request")
                        .setBody("You have a new job opportunity")
                        .build())
                .build();
        message.setNotificationMessage(notificationMessage);
        String messageJson = message.toString();

        // Add the messaging task to the default queue.
        Queue queue = QueueFactory.getDefaultQueue();
        TaskOptions options = TaskOptions.Builder
                .withMethod(TaskOptions.Method.POST)
                .url(MESSAGING_SERVICE_URI)
                .headers(requestHeaders)
                .payload(messageJson);

        queue.addAsync(options);
    }
}
