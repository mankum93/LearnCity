package com.learncity.backend.common.messaging.framework.message.model.incoming;

/**
 * Created by DJ on 5/3/2017.
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * This is a message sent from the XMPP connection server to an app server.
 * Here are the primary types of messages that the XMPP connection server sends to the app server:
 *
 * <ul>
 *     <li>Delivery Receipt: If the app server included delivery_receipt_requested
 *     in the downstream message, the XMPP connection server sends a delivery receipt
 *     when it receives confirmation that the device received the message.</li>
 *
 *     <li>Control: These CCS-generated messages indicate that action is required from the app server.</li>
 * </ul>
 *
 * These details have been taken from,
 * <a href = "https://firebase.google.com/docs/cloud-messaging/xmpp-server-ref#ccs">here.</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeliveryReceiptOrControlMessage {


    // Common field for both delivery Receipt and Control type messages-------------------------------------------------

    /**
     * REQUIRED FIELD
     *
     * This parameter specifies the type of the CCS message: either delivery receipt or control.
     *
     * When it is set to receipt, the message includes from, message_id, category, and
     * dataPayload fields to provide additional information.
     * When it is set to control, the message includes control_type to indicate the type of control message.
     *
     *
     * NOTE: This is a common field for both Receipt and Control message(for which this is "control"
     * and another field called "control_type"(scroll down to see the field) is set)
     */
    @JsonProperty(value = "message_type")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String messageType;


    // Now follow the fields specific to Delivery receipt---------------------------------------------------------------

    /**
     * REQUIRED FIELD
     *
     * This parameter is set to gcm.googleapis.com, indicating that the message is sent from CCS.
     */
    @JsonProperty(value = "from")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String from;

    /**
     * REQUIRED FIELD
     *
     * This parameter specifies the original message ID that the receipt is intended for,
     * prefixed with dr2: to indicate that the message is a delivery receipt.
     * An app server must send an ack message with this message ID to acknowledge that
     * it received this delivery receipt.
     *
     * See <a href = "https://firebase.google.com/docs/cloud-messaging/xmpp-server-ref#table6">table 6</a>
     * for the ack message format.
     */
    @JsonProperty(value = "message_id")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String messageId;

    /**
     *  OPTIONAL PARAMETER
     *
     *  This parameter specifies the application package name of the client app
     *  that receives the message that this delivery receipt is reporting.
     *  This is available when message_type is receipt.
     */
    @JsonProperty(value = "category")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String category;

    /**
     *  OPTIONAL PARAMETER
     *
     *  This parameter specifies the key-value pairs for the delivery receipt message.
     *  This is available when the message type is receipt.
     *
     *  <ul>
     *      <li>message_status: This parameter specifies the status of the receipt message.
     *      It is set to MESSAGE_SENT_TO_DEVICE to indicate the device has confirmed its
     *      receipt of the original message.</li>
     *
     *      <li>original_message_id: This parameter specifies the ID of the original message
     *      that the app server sent to the client app.</li>
     *
     *      <li>device_registration_id: This parameter specifies the registration token of
     *      the client app to which the original message was sent.</li>
     *
     *  </ul>
     *
     */
    @JsonProperty(value = "data")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> dataPayload;


    // Control type specific--------------------------------------------------------------------------------------------

    /**
     * OPTIONAL PARAMETER
     *
     * This parameter specifies the type of control message sent from CCS.
     *
     * Currently, only CONNECTION_DRAINING is supported. The XMPP connection
     * server sends this control message before it closes a connection to perform
     * load balancing. As the connection drains, no more messages are allowed to be
     * sent to the connection, but existing messages in the pipeline continue to be processed.
     *
     */
    @JsonProperty(value = "control_type")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String controlType;


    // Getters, Setters and Constructors--------------------------------------------------------------------------------

    protected DeliveryReceiptOrControlMessage() {
    }

    protected DeliveryReceiptOrControlMessage(String messageType){
        this.messageType = messageType;
    }

    protected DeliveryReceiptOrControlMessage(Builder b){
        this.messageType = b.messageType;
        this.category = b.category;
        this.controlType = b.controlType;
        this.dataPayload = b.dataPayload;
        this.from = b.from;
        this.messageId = b.messageId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Map<String, Object> getDataPayload() {
        return dataPayload;
    }

    public void setDataPayload(Map<String, Object> dataPayload) {
        this.dataPayload = dataPayload;
    }

    public String getControlType() {
        return controlType;
    }

    public void setControlType(String controlType) {
        this.controlType = controlType;
    }



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

    //------------------------------------------------------------------------------------------------------------------
    // Using Builder - which actually is not expected(I mean constructing this object
    // by hand) as this is what CCS sends us always. However,
    // provided for flexibility and unknown requirements
    //------------------------------------------------------------------------------------------------------------------

    public static class Builder<B extends Builder<B>> {

        private String messageType;
        private String messageId;
        private String from;
        private String category;
        private Map<String, Object> dataPayload;
        private String controlType;

        protected Builder(String messageType) {
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

        public B setRegistrationId(String category) {
            this.category = category;
            return (B)getThis();
        }

        public B setError(Map<String, Object> data) {
            this.dataPayload = data;
            return (B)getThis();
        }

        public B setErrorDescription(String controlType) {
            this.controlType = controlType;
            return (B)getThis();
        }

        // Factory with compulsory fields for a Notification message
        public static Builder newBuilder(String messageType){
            return new Builder(messageType);
        }

        protected Builder getThis() {
            return this;
        }

        public DeliveryReceiptOrControlMessage build() {
            return new DeliveryReceiptOrControlMessage(this);
        }
    }
}
