package com.learncity.backend.common.messaging;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;
import com.learncity.backend.common.messaging.framework.message.model.outgoing.DataMessage;
import com.learncity.backend.common.messaging.framework.message.model.outgoing.NotificationMessage;
import com.learncity.backend.common.messaging.framework.message.util.MessageUtils;
import com.learncity.backend.util.IdUtils;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by DJ on 4/2/2017.
 */

/**
 * Message type dictates if it would be acceptable to have a
 * data component. Setting it when it is not dictated by the type
 * would result in the payload being ignored - This can be a bit hard
 * to debug if rules are not carefully examined.
 * */
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message implements Serializable{

    private static final Logger logger = Logger.getLogger(Message.class.getSimpleName());

    /**Message type.*/
    @Index
    private Integer messageType;
    /**No explicit "content" - would be sent as a FCM Notification for a tutoring request.*/
    public static final int TUTORING_REQUEST = 0x0002;
    /** Would be sent as a FCM Notification */
    public static final int CHAT_TEXT_MESSAGE = 0x0004;
    /** A general FCM Data message */
    public static final int FCM_DATA_MESSAGE = 0x0008;
    /** A general FCM Notification message */
    public static final int FCM_NOTIFICATION_MESSAGE = 0x0016;

    /**User ID of the Sender - currently Email based UUID*/
    private String from;
    /**User ID of the Receiver - currently Email based UUID*/
    private String to;

    /**
     * A Message ID for every message that is sent, depends on,
     * the Sender's UUID, Receiver's UUID, and some timestamp.
     */
    @Id
    @JsonIgnore
    private String messageId;

    /**
     * A FCM Data Message(payload)
     */
    private DataMessage dataMessage;

    @JsonIgnore
    private Integer sentStatus;

    /**
     * A FCM Notification Message(payload)
     */
    @Ignore
    private NotificationMessage notificationMessage;

    /**
     * This payload is to support ad-hoc fields specific to
     * the situation that the generator must be facing.
     *
     * Just DON'T confuse this field with Firebase's data payload
     * whatsoever. This is purely for specific business case.
     */
    @Ignore
    private Map<String, String> dataPayload;

    public Message(String from, String to, int messageType) {
        // Validate input
        validateInput(messageType);
        // from should be a UUID
        validateInput(from);
        //validateInput(to);

        this.from = from;
        this.to = to;
        this.messageType = messageType;

        // Assign a unique message ID to it.
        messageId = IdUtils.getType5UUID(from + System.currentTimeMillis() + to);
    }

    /**
     * @return Returns a JSON Stringified representation of this object using Jackson Object Mapper.
     */
    @Override
    public String toString(){
        String json;
        try {
            json = MessageUtils.objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("There was a problem processing Json.");
        }
        return json;
    }

    //Default constructor for Serialization
    protected Message(){}

    private void validateInput(String UUID){
        java.util.UUID.fromString(UUID);
    }

    private void validateInput(int messageType){
        switch(messageType){
            case TUTORING_REQUEST:
                break;
            case CHAT_TEXT_MESSAGE:
                break;
            case FCM_DATA_MESSAGE:
                break;
            case FCM_NOTIFICATION_MESSAGE:
                break;
            default:
                throw new IllegalStateException("Invalid message type.");

        }
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        UUID.fromString(from);
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        //UUID.fromString(to);
        this.to = to;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        validateInput(messageType);
        this.messageType = messageType;
    }

    public DataMessage getDataMessage() {
        return dataMessage;
    }

    public void setDataMessage(DataMessage dataMessage) {
        this.dataMessage = dataMessage;
    }

    public NotificationMessage getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(NotificationMessage notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    /**
     * Refreshes the Message ID to take current System
     * time into account.
     */
    public void refreshMessageId(){
        messageId = IdUtils.getType5UUID(from + System.currentTimeMillis() + to);
    }

    public String getMessageId() {
        return messageId;
    }

    public Integer getSentStatus() {
        return sentStatus;
    }

    public void setSentStatus(Integer sentStatus) {
        this.sentStatus = sentStatus;
    }

    public Map<String, String> getDataPayload() {
        return dataPayload;
    }

    public void setDataPayload(Map<String, String> dataPayload) {
        this.dataPayload = dataPayload;
    }
}
