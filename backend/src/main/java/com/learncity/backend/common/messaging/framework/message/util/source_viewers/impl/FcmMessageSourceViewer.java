package com.learncity.backend.common.messaging.framework.message.util.source_viewers.impl;

import com.learncity.backend.common.messaging.framework.message.util.MessageUtils;
import com.learncity.backend.common.messaging.framework.message.util.source_viewers.SourceViewer;
import com.learncity.backend.common.messaging.framework.util.smackx.fcm.packet.FcmPacketExtension;
import org.jivesoftware.smack.packet.Message;

import java.util.Map;

import static com.learncity.backend.common.messaging.framework.Constants.FCM_NAMESPACE;

/**
 * Created by DJ on 5/3/2017.
 */
public class FcmMessageSourceViewer implements SourceViewer<Message> {

    private String sourceAsString;
    private Map<String, Object> sourceAsMap;
    private Message messageReceived;

    private final Object lock = new Object();

    public FcmMessageSourceViewer(Message messageReceived) {
        this.messageReceived = messageReceived;
        resetSources();
    }

    @Override
    public void setSource(Message messageReceived) {
        this.messageReceived = messageReceived;
        resetSources();
    }

    @Override
    public Message viewSource() {
        return messageReceived;
    }

    public String getSourceAsJSONString() {
        //Lazy get
        synchronized (lock){
            if(messageReceived != null){
                if(sourceAsString == null){
                    FcmPacketExtension gcmPacket = (FcmPacketExtension) messageReceived.getExtension(FCM_NAMESPACE);
                    sourceAsString = gcmPacket.getJson();
                }
            }
        }
        return sourceAsString;
    }

    public Map<String, Object> getSourceAsMap() {

        synchronized (lock){
            if(messageReceived != null){
                // If the source string has not been get, get() it
                if(sourceAsString == null){
                    getSourceAsJSONString();
                }
                if(sourceAsMap == null){
                    sourceAsMap = MessageUtils.getAttributeMapFromJsonString(sourceAsString);
                }
            }
        }
        return sourceAsMap;
    }

    private void resetSources(){
        // Explicit setting of other forms of Source to null
        // so that they can be recalculated for new main source.
        synchronized (lock){
            sourceAsString = null;
            sourceAsMap = null;
        }
    }
}
