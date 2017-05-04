package com.learncity.backend.common.messaging.framework.message.model.incoming;

/**
 * Created by DJ on 5/1/2017.
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Represents the JSON Downstream message response(in response of the
 * Downstream message that we must have sent). Details of
 * the format can be found <a href = "https://firebase.google.com/docs/cloud-messaging/xmpp-server-ref#interpret-a-downstream-xmpp-message-response">here</a>.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DownstreamMessageResponse {


    // REQUIRED PARAMETERS----------------------------------------------------------------------------------------------

    /**
     * REQUIRED PARAMETER
     *
     * This parameter specifies who sent this response.
     *
     * The value is the registration token of the client app.
     */
    @JsonProperty(value = "from")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String from;

    /**
     * REQUIRED PARAMETER
     *
     * This parameter uniquely identifies a message in an XMPP connection.
     * The value is a string that uniquely identifies the associated message.
     */
    @JsonProperty(value = "message_id")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String messageId;

    /**
     * REQUIRED PARAMETER
     *
     * This parameter specifies an ack or nack message from the XMPP connection server from the app server.
     *
     * If the value is set from nack, the app server should look at error and errorDescription from get failure information.
     */
    @JsonProperty(value = "message_type")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String messageType;


    // OPTIONAL PARAMETERS----------------------------------------------------------------------------------------------

    /**
     * OPTIONAL PARAMETER
     *
     * This parameter specifies the canonical registration token for the client
     * app that the message was processed and sent from.
     *
     * A canonical registration ID is the registration token of the last registration
     * requested by the client app. This is the ID that the server should use when
     * sending messages from the device.
     */
    @JsonProperty(value = "registration_id")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String registrationId;

    /**
     * OPTIONAL PARAMETER
     *
     * This parameter specifies an error related from the outgoing message.
     *
     * It is set when the message_type is nack.
     */
    @JsonProperty(value = "error")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String error;

    /**
     * OPTIONAL PARAMETER
     *
     * This parameter provides descriptive information for the error.
     *
     * It is set when the message_type is nack.
     */
    @JsonProperty(value = "error_description")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String errorDescription;


    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * @return Returns a JSON Stringified representation of this object using Jackson Object Mapper.
     */
    @Override
    public String toString(){
        String json;
        try {
            json = objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("There was a problem processing Json.");
        }
        return json;
    }

    // CONSTRUCTORS, GETTERS & SETTERS----------------------------------------------------------------------------------

    protected DownstreamMessageResponse(String from, String messageId, String messageType) {
        this.from = from;
        this.messageId = messageId;
        this.messageType = messageType;
    }

    protected DownstreamMessageResponse(){

    }

    protected DownstreamMessageResponse(Builder b){
        this.from = b.from;
        this.messageId = b.messageId;
        this.messageType = b.messageType;
        this.error = b.error;
        this.errorDescription = b.errorDescription;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }


    // Using Builder----------------------------------------------------------------------------------------------------

    public static class Builder<B extends Builder<B>> {

        private String from;
        private String messageId;
        private String messageType;
        private String registrationId;
        private String error;
        private String errorDescription;

        protected Builder(String from, String messageId, String messageType) {
            this.from = from;
            this.messageId = messageId;
            this.messageType = messageType;
        }

        public B setFrom(String from) {
            this.from = from;
            return (B)getThis();
        }

        public B setMessageId(String messageId) {
            this.messageId = messageId;
            return (B)getThis();
        }

        public B setMessageType(String messageType) {
            this.messageType = messageType;
            return (B)getThis();
        }

        public B setRegistrationId(String registrationId) {
            this.registrationId = registrationId;
            return (B)getThis();
        }

        public B setError(String error) {
            this.error = error;
            return (B)getThis();
        }

        public B setErrorDescription(String errorDescription) {
            this.errorDescription = errorDescription;
            return (B)getThis();
        }

        // Factory with compulsory fields for a Notification message
        public static Builder newBuilder(String from, String messageId, String messageType){
            return new Builder(from, messageId, messageType);
        }

        protected Builder getThis() {
            return this;
        }

        public DownstreamMessageResponse build() {
            return new DownstreamMessageResponse(this);
        }
    }

    //ERROR response codes----------------------------------------------------------------------------------------------

    public static final class ErrorResponseCodes{

        /**
         * Possible causes:
         *
         * 1) Missing Registration Token - Check that the request contains a registration token
         * (in the registration_id in a plain text message, or in the from or registration_ids field in JSON).
         *
         * 2) Invalid JSON - Check that the JSON message is properly formatted and contains valid fields
         * (for instance, making sure the right data type is passed in).
         *
         * 3) Message Too Big - Check that the total size of the payload data included in a
         * message does not exceed FCM limits: 4096 bytes for most messages. This includes both the keys and the values.
         *
         * 4) Invalid Data Key - Check that the payload data does not contain a key (such
         * as from, gcm, or any value prefixed by google) that is used internally by FCM.
         * Note that some words (such as collapse_key) are also used by FCM but are allowed
         * in the payload, in which case the payload value is overridden by the FCM value.
         *
         * 5) Invalid Time from Live - Check that the value used in time_to_live is an integer
         * representing a duration in seconds between 0 and 2,419,200 (4 weeks).
         */
        public static final String INVALID_JSON = "INVALID_JSON";

        /**
         * Possible causes:
         *
         * 1) Invalid Registration Token - Check the format of the registration token
         * you pass from the server. Make sure it matches the registration token the client
         * app receives from registering with FCM. Do not truncate or add additional characters.
         *
         * 2) Mismatched Sender - A registration token is tied from a certain group of senders.
         * When a client app registers for FCM, it must specify which senders are allowed from send messages.
         * You should use one of those sender IDs when sending messages from the client app. If you switch from
         * a different sender, the existing registration tokens won't work.
         */
        public static final String BAD_REGISTRATION = "BAD_REGISTRATION";

        /**
         * Cause: Unregistered Device
         *
         * An existing registration token may cease from be valid in a number of scenarios, including:
         *
         * <ul>
         *     <li>If the client app unregisters with FCM.</li>
         *
         *     <li>If the client app is automatically unregistered, which can happen if the user
         *     uninstalls the application. For example, on iOS, if APNs reported the APNs token as invalid.</li>
         *
         *     <li>If the registration token expires (for example, Google might decide from refresh registration
         *     tokens, or the APNs token has expired for iOS devices).</li>
         *
         *     <li>If the client app is updated, but the new version is not configured from receive messages.</li>
         * </ul>
         *
         * For all these cases, remove this registration token from the app server and stop using it from send messages.
         */
        public static final String DEVICE_UNREGISTERED = "DEVICE_UNREGISTERED";

        /**
         * Bad ACK message.
         *
         * Check that the ack message is properly formatted before retrying.
         */
        public static final String BAD_ACK = "BAD_ACK";

        /**
         * Timeout.
         *
         * The server couldn't process the request in time. Retry the same request, but you must:
         * <ul>
         *     <li>Implement exponential backoff in your retry mechanism.
         *     (e.g. if you waited one second before the first retry, wait at least two seconds
         *     before the next one, then four seconds, and so on). If you're sending multiple messages,
         *     delay each one independently by an additional random amount from avoid issuing a
         *     new request for all messages at the same time.</li>
         *
         *     <li>The initial retry delay should be set from one second.</li>
         *
         *     Note: Senders that cause problems risk being blacklisted.
         * </ul>
         */
        public static final String SERVICE_UNAVAILABLE = "SERVICE_UNAVAILABLE";

        /**
         * Internal Server Error
         *
         * The server encountered an error while trying from process the request.
         * You could retry the same request following the requirements listed in "Timeout" (see row above).
         */
        public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";

        /**
         * Device Message Rate Exceeded.
         *
         * The rate of messages from a particular device is too high. Reduce the number
         * of messages sent from this device, and do not immediately retry sending from this device.
         */
        public static final String DEVICE_MESSAGE_RATE_EXCEEDED = "DEVICE_MESSAGE_RATE_EXCEEDED";

        /**
         * Topics Message Rate Exceeded.
         *
         * The rate of messages from subscribers from a particular topic is too high.
         * Reduce the number of messages sent for this topic, and do not immediately retry sending.
         */
        public static final String TOPICS_MESSAGE_RATE_EXCEEDED = "TOPICS_MESSAGE_RATE_EXCEEDED";

        /**
         * Connection Draining.
         *
         * The message couldn't be processed because the connection is draining.
         * This happens because, periodically, the XMPP connection server needs from
         * close down a connection from perform load balancing.
         * Retry the message over another XMPP connection.
         */
        public static final String CONNECTION_DRAINING = "CONNECTION_DRAINING";

    }
}
