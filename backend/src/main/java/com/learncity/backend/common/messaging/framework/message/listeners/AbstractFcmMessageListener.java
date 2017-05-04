package com.learncity.backend.common.messaging.framework.message.listeners;

import com.learncity.backend.common.messaging.framework.message.util.source_viewers.SourceViewer;
import com.learncity.backend.common.messaging.framework.message.util.source_viewers.impl.FcmMessageSourceViewer;

/**
 * Created by DJ on 5/4/2017.
 */
public abstract class AbstractFcmMessageListener<Message> implements GenericFcmMessageListener<Message>, SourceViewer<FcmMessageSourceViewer> {

    private FcmMessageSourceViewer fcmMessageSourceViewer;

    @Override
    public void setSource(FcmMessageSourceViewer fcmMessageSourceViewer) {
        this.fcmMessageSourceViewer = fcmMessageSourceViewer;
    }

    @Override
    public FcmMessageSourceViewer viewSource() {
        return fcmMessageSourceViewer;
    }
}
