package com.learncity.learner.search.model.message;

import com.learncity.learner.search.model.message.util.MessageUtils;
import com.learncity.learner.search.model.message.util.generator.Generator;

/**
 * Represents the JSON outgoing message.
 *
 * Note that the description of the fields have been taken from the Firebase official Docs,
 * <a href = "https://firebase.google.com/docs/cloud-messaging/xmpp-server-ref">here</a>
 */
public abstract class AbstractDownstreamMessage {

    /**
     *  OPTIONAL PARAMETER
     *
     *  This parameter specifies the recipient of a message.
     *
     *  The value can be a device's registration token, a device group's notification key,
     *  or a single topic (prefixed with /topics/). To send to multiple topics, use the condition parameter.
     */
	private String to;

    /**
     * OPTIONAL PARAMETER
     *
     * This parameter specifies a logical expression of conditions that determines the message target.
     *
     * Supported condition: Topic, formatted as "'yourTopic' in topics". This value is case-insensitive.
     * Supported operators: &&, ||. Maximum two operators per topic message supported.
     */
	private String condition;


    /**
     * REQUIRED PARAMETER
     *
     * This parameter uniquely identifies a message in an XMPP connection.
     */
	private String messageId;


    /**
     * OPTIONAL PARAMETER
     *
     * This parameter identifies a group of messages (e.g., with collapse_key: "Updates Available")
     * that can be collapsed so that only the last message gets sent when delivery is resumed.
     * This is intended to avoid sending too many of the same messages when the device comes
     * back online or comes out of doze.
     *
     * There is no guarantee of the order in which messages get sent.
     * Note: A maximum of 4 different collapse keys is allowed at any given time.
     * This means an FCM connection server can simultaneously store 4 different send-to-sync
     * messages per client app. If you exceed this number, there is no guarantee which
     * 4 collapse keys the FCM connection server will keep.
     */
	private String collapseKey;


    /**
     * OPTIONAL PARAMETER
     *
     * Sets the priority of the message. Valid values are "normal" and "high."
     * On iOS, these correspond to APNs priorities 5 and 10.
     *
     * By default, notification messages are sent with high priority, and data
     * messages are sent with normal priority. Normal priority optimizes the client
     * app's battery consumption and should be used unless immediate delivery is required.
     * For messages with normal priority, the app may receive the message with unspecified delay.
     * When a message is sent with high priority, it is sent immediately, and the app can
     * wake a sleeping device and open a network connection to your server.
     * For more information, see <a href = "https://firebase.google.com/docs/cloud-messaging/concept-options#setting-the-priority-of-a-message">Setting the priority of a message.</a>
     */
	private String priority;


    /**
     * OPTIONAL PARAMETER
     *
     * When a notification or message is sent and this is set to true, an inactive
     * client app is awakened. Data messages wake the app by default.
     */
	private Boolean contentAvailable;


    /**
     * OPTIONAL PARAMETER
     *
     * This parameter specifies how long (in seconds) the message should be kept
     * in FCM storage if the device is offline. The maximum time to live supported
     * is 4 weeks, and the default value is 4 weeks. For more information,
     * see <a href = "https://firebase.google.com/docs/cloud-messaging/concept-options#ttl">Setting the lifespan of a message.</a>
     */
	private Integer timeToLive;


    /**
     * OPTIONAL PARAMETER
     *
     * This parameter lets the app server request confirmation of message delivery.
     *
     * When this parameter is set to true, CCS sends a delivery receipt when the
     * device confirms that it received the message. The default value is false.
     */
	private Boolean deliveryReceiptRequested;


    /**
     * OPTIONAL PARAMETER
     *
     * This parameter, when set to true, allows developers to test a request without actually sending a message.
     *
     * The default value is false.
     */
	private Boolean dryRun;

	protected AbstractDownstreamMessage(String to, String messageId) {
		this.to = to;
		this.messageId = messageId;
	}

	protected AbstractDownstreamMessage(Builder b){
        this.to = b.to;
        this.condition = b.condition;
        this.messageId = b.messageId;
        this.collapseKey = b.collapseKey;
        this.priority = b.priority;
        this.contentAvailable = b.contentAvailable;
        this.timeToLive = b.timeToLive;
        this.deliveryReceiptRequested = b.deliveryReceiptRequested;
        this.dryRun = b.dryRun;
    }

    protected AbstractDownstreamMessage() {
    }

    // Getters and Setters----------------------------------------------------------------------------------------------

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getCollapseKey() {
        return collapseKey;
    }

    public void setCollapseKey(String collapseKey) {
        this.collapseKey = collapseKey;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Boolean isContentAvailable() {
        return contentAvailable;
    }

    public void setContentAvailable(Boolean contentAvailable) {
        this.contentAvailable = contentAvailable;
    }

    public Integer getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(Integer timeToLive) {
        this.timeToLive = timeToLive;
    }

    public Boolean isDeliveryReceiptRequested() {
        return deliveryReceiptRequested;
    }

    public void setDeliveryReceiptRequested(Boolean deliveryReceiptRequested) {
        this.deliveryReceiptRequested = deliveryReceiptRequested;
    }

    public Boolean isDryRun() {
        return dryRun;
    }

    public void setDryRun(Boolean dryRun) {
        this.dryRun = dryRun;
    }

    // Using Builder----------------------------------------------------------------------------------------------------

    public abstract static class Builder<B extends Builder<B>> {
        private String to;
        private String messageId;
        private String condition;
        private String collapseKey;
        private String priority;
        private Boolean contentAvailable;
        private Integer timeToLive;
        private Boolean deliveryReceiptRequested;
        private Boolean dryRun;

        /*
         * This generator is not part of the Message class because of
         * the obvious purpose it serves.
         */
        protected Generator<String, Object> messageIdGenerator;

        protected abstract B getThis();

        protected Builder(String to, String messageId) {
            this.to = to;
            this.messageId = messageId;

            assignMessageId();
        }

        public B setTo(String to) {
            this.to = to;
            return getThis();
        }

        public B setMessageId(String messageId) {
            this.messageId = messageId;
            return getThis();
        }

        public B setCondition(String condition) {
            this.condition = condition;
            return getThis();
        }

        public B setCollapseKey(String collapseKey) {
            this.collapseKey = collapseKey;
            return getThis();
        }

        public B setPriority(String priority) {
            this.priority = priority;
            return getThis();
        }

        public B setContentAvailable(Boolean contentAvailable) {
            this.contentAvailable = contentAvailable;
            return getThis();
        }

        public B setTimeToLive(Integer timeToLive) {
            this.timeToLive = timeToLive;
            return getThis();
        }

        public B setDeliveryReceiptRequested(Boolean deliveryReceiptRequested) {
            this.deliveryReceiptRequested = deliveryReceiptRequested;
            return getThis();
        }

        public B setDryRun(Boolean dryRun) {
            this.dryRun = dryRun;
            return getThis();
        }

        public B setMessageIdGenerator(Generator<String, Object> messageIdGenerator) {
            this.messageIdGenerator = messageIdGenerator;
            return getThis();
        }

        private void assignMessageId(){
            if(messageId == null){
                if(messageIdGenerator == null){
                    messageId = MessageUtils.getUniqueMessageId();
                }
                else{
                    messageId = messageIdGenerator.next(null);
                }
            }
        }

    }
}