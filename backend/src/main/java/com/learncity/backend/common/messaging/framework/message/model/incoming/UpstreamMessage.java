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
 * Represents a standard Upstream message sent by the client App.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpstreamMessage {


    /**
     * REQUIRED PARAMETER
     *
     * This parameter specifies who sent the message.
     *
     * The value is the registration token of the client app.
     */
    @JsonProperty(value = "from")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String from;

    /**
     * REQUIRED PARAMETER
     *
     * This parameter specifies the application package name of the client app that sent the message.
     */
    @JsonProperty(value = "category")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String category;

    /**
     * REQUIRED PARAMETER
     *
     * This parameter specifies the unique ID of the message.
     */
    @JsonProperty(value = "message_id")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String messageId;

    /**
     * OPTIONAL PARAMETER
     *
     * This parameter specifies the key-value pairs of the message's payload.
     */
    @JsonProperty(value = "dataPayload")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> dataPayload;


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

    public Acknowledgement getAcknowledgement(){
        if(this.from == null || this.messageId == null){
            throw new RuntimeException("Either there is no user to send the Acknowledgement or the Message Id is absent.");
        }
        return new Acknowledgement(this.from, this.messageId, "ack");
    }

    // CONSTRUCTORS, GETTERS & SETTERS----------------------------------------------------------------------------------

    protected UpstreamMessage() {
    }

    protected UpstreamMessage(String from, String category, String messageId) {
        this.from = from;
        this.category = category;
        this.messageId = messageId;
    }

    protected UpstreamMessage(Builder b){
        this.from = b.from;
        this.messageId = b.messageId;
        this.category = b.category;
        this.dataPayload = b.dataPayload;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Map<String, Object> getDataPayload() {
        return dataPayload;
    }

    public void setDataPayload(Map<String, Object> dataPayload) {
        this.dataPayload = dataPayload;
    }


    // Using Builder----------------------------------------------------------------------------------------------------

    public static class Builder<B extends Builder<B>> {

        private String messageId;
        private String from;
        private String category;
        private Map<String, Object> dataPayload;

        protected Builder(String messageId, String from, String category) {
            this.messageId = messageId;
            this.from = from;
            this.category = category;
        }

        public B setFrom(String from) {
            this.from = from;
            return (B)getThis();
        }

        public B setMessageId(String messageId) {
            this.messageId = messageId;
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

        // Factory with compulsory fields for a Notification message
        public static Builder newBuilder(String messageId, String from, String category){
            return new Builder(messageId, from, category);
        }

        protected Builder getThis() {
            return this;
        }

        public UpstreamMessage build() {
            return new UpstreamMessage(this);
        }
    }

    /**
     * Acknowledgement for this type of messages.
     */
    public static class Acknowledgement{

        /**
         * REQUIRED PARAMETER
         *
         * This parameter specifies the recipient of a response message.
         *
         * The value must be a registration token of the client app that sent the upstream message.
         */
        @JsonProperty(value = "to")
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private String to;

        /**
         * REQUIRED PARAMETER
         *
         * This parameter specifies which message the response is intended for.
         *
         * The value must be the message_id value from the corresponding upstream message.
         */
        @JsonProperty(value = "message_id")
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private String messageId;

        /**
         * REQUIRED PARAMETER
         *
         * This parameter specifies an ack message from an app server to CCS.
         *
         * For upstream messages, it should always be set to ack.
         */
        @JsonProperty(value = "message_type")
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private String messageType;

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

        protected Acknowledgement() {
        }

        public Acknowledgement(String to, String messageId, String messageType) {
            this.to = to;
            this.messageId = messageId;
            this.messageType = messageType;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
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
    }
}
