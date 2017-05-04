/**
 *
 * Copyright © 2014 Florian Schmaus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.learncity.backend.common.messaging.framework.util.smackx.fcm.packet;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.gcm.packet.GcmPacketExtension;
import org.jivesoftware.smackx.pubsub.util.XmlUtils;

import java.util.logging.Logger;

/**
 * XMPP extension elements as used by FCM Cloud Connection Server (XMPP).
 */
public class FcmPacketExtension extends GcmPacketExtension {

    private static final Logger logger = Logger.getLogger(FcmPacketExtension.class.getSimpleName());

    // Reserved in case Google decides to change them for Firebase.
    public static final String ELEMENT = "gcm";
    public static final String NAMESPACE = "google:mobile:data";

    public FcmPacketExtension(String json) {
        super(json);
    }

    public Message toMessage() {

        Message message = new Message();
        message.addExtension(this);

        logger.info("The Message(Stanza) is:");
        XmlUtils.prettyPrint(null, message.toXML().toString());

        return message;
    }
}
