package com.learncity.backend.common.messaging;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiClass;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiReference;

import com.google.appengine.repackaged.com.google.api.client.http.HttpResponse;

import com.googlecode.objectify.ObjectifyService;

import com.learncity.backend.common.BaseConfigEndpoint;
import com.learncity.backend.common.account.create.Account;
import com.learncity.backend.common.account.create.endpoints.BaseLearnerEndpoint;
import com.learncity.backend.common.messaging.framework.message.model.outgoing.NotificationMessage;
import com.learncity.backend.tutor.account.create.TutorProfileVer1;
import com.learncity.backend.util.ArrayUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;
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

    static {
        ObjectifyService.register(Message.class);
    }

    @ApiMethod(
            name = "sendMessage",
            path = "messaging",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public void sendMessage(final Message message){

        // Retrieve the Account and Firebase Token of the Receiver
        Account acc = BaseLearnerEndpoint.getAccount(message.getTo());

        if(acc == null){
            // Account doesn't exist
            logger.warning("Account with the User ID: " + message.getTo() + "doesn't exist");
            return;
        }
        String firebaseTokenReceiver = acc.getUserDeviceFirebaseToken();

        if(firebaseTokenReceiver == null){
            // Firebase Token for the device not registered
            logger.severe("Firebase Token for the device(receiver's) not registered. WTF?");
            return;
        }

        // The JSON to be sent as a message.
        String messageJson;

        // Send this message to the receiver now.
        if(message.getMessageType() == TUTORING_REQUEST){

            // Retrieve other fields for a Tutoring Request besides the
            // Firebase Token
            TutorProfileVer1 profile = (TutorProfileVer1) acc.getProfile();
            Map<String, String> dataPayload = new HashMap<>();
            // Message Id
            // Jackson's deserialization constructs an object with
            // no message Id. Therefore we generate it manually.
            message.refreshMessageId();
            dataPayload.put("messageId", message.getMessageId());
            // Name of the requester
            dataPayload.put("name", profile.getName());
            // Subjects that they teach
            dataPayload.put("subjects", message.getDataPayload().get("subjects"));
            // Location in short format
            String shortFormattedAddress = acc.getLocationInfo() == null ? null : acc.getLocationInfo().getShortFormattedAddress();
            if(shortFormattedAddress != null){
                dataPayload.put("location", shortFormattedAddress);
            }

            // Build a FCM Notification Message
            NotificationMessage notificationMessage = NotificationMessage.Builder
                    .newBuilderInstance(firebaseTokenReceiver, null)
                    .setNotificationPayload(NotificationMessage.NotificationPayload.Builder
                            .newBuilder()
                            .setTitle("Tutoring request")
                            .setBody("You have a new job opportunity")
                            .setClick_action("ACTION_TUTORING_REQUEST")
                            .build())
                    .build();
            notificationMessage.setDataPayload(dataPayload);
            message.setNotificationMessage(notificationMessage);
            messageJson = message.toString();
        }
        else{
            // Append code here for more message categories.
            return;
        }

        // Let the Messaging client handle HTTP nitty-gritty
        MessagingClient client = MessagingClient.getDefaultInstance();

        // Stash this Message to Datastore with the sent status
        client.setMessageResponseListener(new MessagingClient.MessageResponseListener() {
            @Override
            public void onReceiveResponse(HttpResponse response) {

                int statusCode = response.getStatusCode();
                message.setSentStatus(statusCode);
                ofy().save().entity(message).now();
            }
        });

        client.sendMessage(messageJson);
    }
}
