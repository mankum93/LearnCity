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
package com.learncity.backend.common.messaging.framework.util.smackx.fcm.provider;

import com.learncity.backend.common.messaging.framework.util.smackx.fcm.packet.FcmPacketExtension;
import org.jivesoftware.smackx.gcm.provider.GcmExtensionProvider;

public class FcmExtensionProvider extends GcmExtensionProvider {

    @Override
    public FcmPacketExtension from(String json) {
        return new FcmPacketExtension(json);
    }
}
