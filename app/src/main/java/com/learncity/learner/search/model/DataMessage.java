package com.learncity.learner.search.model;

import java.util.Map;

/**
 * Created by DJ on 4/24/2017.
 */

/**
 * Represents a Downstream JSON Data message. The message fields in this class are in-built supported by Firebase.
 */
public class DataMessage extends AbstractDownstreamMessage {

    /**
     * Represents a Data Payload component of a Downstream message.
     */
    private Map<String, String> dataPayload;

    public Map<String, String> getDataPayload() {
        return dataPayload;
    }

    public void setDataPayload(Map<String, String> dataPayload) {
        this.dataPayload = dataPayload;
    }

    protected DataMessage(Builder b){
        super(b);
        this.dataPayload = b.dataPayload;
    }

    protected DataMessage(){

    }

    public static class Builder<B extends Builder<B>> extends AbstractDownstreamMessage.Builder{

        private Map<String, String> dataPayload;

        public static Builder newInstance(String to, String messageId, Map<String, String> dataPayload){
            /*if(dataPayload == null){
                throw new IllegalArgumentException("Data payload cannot be null for a \"Data\" message.");
            }*/
            return new Builder(to, messageId, dataPayload);
        }

        protected Builder(String to, String messageId, Map<String, String> dataPayload) {
            super(to, messageId);
            this.dataPayload = dataPayload;
        }

        // So that the subclasser can make data payload optional
        protected Builder(String to, String messageId) {
            super(to, messageId);
        }

        public B setDataPayload(Map<String, String> dataPayload){
            this.dataPayload = dataPayload;
            return (B)getThis();
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        public DataMessage build() {
            return new DataMessage(this);
        }
    }
}
