package com.learncity.backend.common.messaging.framework.message.model.outgoing;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learncity.backend.common.messaging.framework.message.util.MessageUtils;

import java.util.Map;

/**
 * Created by DJ on 4/25/2017.
 */

/**
 * Represents a Downstream JSON Notification message.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationMessage extends DataMessage {

    /**
     * This represents the Notification Payload containing above fields. Getter
     * for this Payload is not exposed because this is internally constructed
     * from other fields and any extras if provided such that it doesn't leave
     * any requirement of a direct contract.
     *
     * This is ultimately the only container field to be serialized/deserialized.
     */
    @JsonProperty(value = "notification")
    private Map<String, Object> notificationPayload;


    private static final ObjectMapper objectMapper = new ObjectMapper();

    protected NotificationMessage(Builder b){
        super(b);
        // Now get the Payload from the Payload POJO in an Attribute Map
        this.notificationPayload = MessageUtils.toAttributeMap(b.notificationPayload);
    }

    protected NotificationMessage(){
    }

    // Using Builder----------------------------------------------------------------------------------------------------

    public static class Builder<B extends Builder<B>> extends DataMessage.Builder{

        private NotificationPayload notificationPayload;

        // Factory with compulsory fields for a Notification message
        public static Builder newBuilderInstance(String to, String messageId){
            return new Builder(to, messageId);
        }

        protected Builder(String to, String messageId) {
            super(to, messageId);
        }

        public B setNotificationPayload(NotificationPayload notificationPayload){
            this.notificationPayload = notificationPayload;
            return (B)getThis();
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        public NotificationMessage build() {
            return new NotificationMessage(this);
        }
    }




    /**
     * Represents a Outgoing JSON Notification payload. The message fields in this class are in-built supported by Firebase.
     *
     * Although, it extends Data message, having a Data Payload is optional.
     *
     * Note that the description of the fields have been taken from the Firebase official Docs,
     * <a href = "https://firebase.google.com/docs/cloud-messaging/xmpp-server-ref#notification-payload-support">here</a>
     */
    public static class NotificationPayload {


        //----------------OPTIONAL PARAMETERS-------------------------------------------------------------------------------
        /**
         * OPTIONAL PARAMETER
         *
         * The notification's title.
         */
        private String title;

        /**
         * OPTIONAL PARAMETER
         *
         *  The notification's body text.
         */
        private String body;

        /**
         * OPTIONAL PARAMETER
         *
         *  The notification's icon.
         *
         *  Sets the notification icon to myicon for drawable resource myicon.
         *  If you don't send this key in the request, FCM displays the launcher icon specified in your app manifest.
         */
        private String icon;

        /**
         *  OPTIONAL PARAMETER
         *
         *  The sound to play when the device receives the notification.
         *
         *  Supports "default" or the filename of a sound resource bundled in the app.
         *  Sound files must reside in /res/raw/.
         */
        private String sound;

        /**
         *  OPTIONAL PARAMETER
         *
         *  Identifier used to replace existing notifications in the notification drawer.
         *
         *  If not specified, each request creates a new notification.
         *  If specified and a notification with the same tag is already being shown,
         *  the new notification replaces the existing one in the notification drawer.
         */
        private String tag;

        /**
         * OPTIONAL PARAMETER
         *
         * The notification's icon color, expressed in #rrggbb format.
         */
        private String color;

        /**
         *  OPTIONAL PARAMETER
         *
         *  The action associated with a user click on the notification.
         *
         *  If specified, an activity with a matching intent filter is launched when a user clicks on the notification.
         */
        private String click_action;

        /**
         *  OPTIONAL PARAMETER
         *
         *  The key to the body string in the app's string resources to use to
         *  localize the body text to the user's current localization.
         *
         *  See <a href = "https://developer.android.com/guide/topics/resources/string-resource.html">String Resources</a>
         *  for more information.
         */
        private String body_loc_key;

        /**
         *  OPTIONAL PARAMETER
         *
         *  Variable string values to be used in place of the format specifiers
         *  in body_loc_key to use to localize the body text to the user's current localization.
         *
         *  See <a href = "https://developer.android.com/guide/topics/resources/string-resource.html#FormattingAndStyling">Formatting and Styling</a> for more information.
         */
        private String body_loc_args;

        /**
         *  OPTIONAL PARAMETER
         *
         *  The key to the title string in the app's string resources to use to
         *  localize the title text to the user's current localization.
         *
         *  See <a href = "https://developer.android.com/guide/topics/resources/string-resource.html">String Resources</a>
         *  for more information.
         */
        private String title_loc_key;

        /**
         *  OPTIONAL PARAMETER
         *
         *  Variable string values to be used in place of the format specifiers in
         *  title_loc_key to use to localize the title text to the user's current localization.
         *
         *  See <a href = "https://developer.android.com/guide/topics/resources/string-resource.html#FormattingAndStyling">Formatting and Styling</a> for more information.
         */
        private String title_loc_args;

        /**
         * OPTIONAL PARAMETER
         *
         * This field inerts as it is whatever extra data user directs us to
         * insert in the notification payload with the built-in fields
         */
        private Map<String, Object> notificationExtras;

        // GETTERS, SETTERS & CONSTRUCTORS----------------------------------------------------------------------------------

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getSound() {
            return sound;
        }

        public void setSound(String sound) {
            this.sound = sound;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getClick_action() {
            return click_action;
        }

        public void setClick_action(String click_action) {
            this.click_action = click_action;
        }

        public String getBody_loc_key() {
            return body_loc_key;
        }

        public void setBody_loc_key(String body_loc_key) {
            this.body_loc_key = body_loc_key;
        }

        public String getBody_loc_args() {
            return body_loc_args;
        }

        public void setBody_loc_args(String body_loc_args) {
            this.body_loc_args = body_loc_args;
        }

        public String getTitle_loc_key() {
            return title_loc_key;
        }

        public void setTitle_loc_key(String title_loc_key) {
            this.title_loc_key = title_loc_key;
        }

        public String getTitle_loc_args() {
            return title_loc_args;
        }

        public void setTitle_loc_args(String title_loc_args) {
            this.title_loc_args = title_loc_args;
        }

        public Map<String, Object> getNotificationExtras() {
            return notificationExtras;
        }

        public void setNotificationExtras(Map<String, Object> notificationExtras) {
            this.notificationExtras = notificationExtras;
        }

        protected NotificationPayload(Builder b){
            this.notificationExtras = b.notificationExtras;
            this.title = b.title;
            this.body = b.body;
            this.color = b.color;
            this.tag = b.tag;
            this.sound = b.sound;
            this.icon = b.icon;
            this.body_loc_args = b.body_loc_args;
            this.body_loc_key = b.body_loc_key;
            this.click_action = b.click_action;
            this.title_loc_args = b.title_loc_args;
            this.title_loc_key = b.title_loc_key;
        }

        // Using Builder----------------------------------------------------------------------------------------------------

        public static class Builder<B extends Builder<B>>{

            private String title;

            private String body;

            private String icon;

            private String sound;

            private String tag;

            private String color;

            private String click_action;

            private String body_loc_key;

            private String body_loc_args;

            private String title_loc_key;

            private String title_loc_args;

            private Map<String, Object> notificationExtras;

            // Factory with compulsory fields for a Notification message
            public static Builder newBuilder(){
                return new Builder();
            }

            public B setTitle(String title){
                this.title = title;
                return (B)getThis();
            }

            public B setBody(String body) {
                this.body = body;
                return (B)getThis();
            }

            public B setIcon(String icon) {
                this.icon = icon;
                return (B)getThis();
            }

            public B setSound(String sound) {
                this.sound = sound;
                return (B)getThis();
            }

            public B setTag(String tag) {
                this.tag = tag;
                return (B)getThis();
            }

            public B setColor(String color) {
                this.color = color;
                return (B)getThis();
            }

            public B setClick_action(String click_action) {
                this.click_action = click_action;
                return (B)getThis();
            }

            public B setBody_loc_key(String body_loc_key) {
                this.body_loc_key = body_loc_key;
                return (B)getThis();
            }

            public B setBody_loc_args(String body_loc_args) {
                this.body_loc_args = body_loc_args;
                return (B)getThis();
            }

            public B setTitle_loc_key(String title_loc_key) {
                this.title_loc_key = title_loc_key;
                return (B)getThis();
            }

            public B setTitle_loc_args(String title_loc_args) {
                this.title_loc_args = title_loc_args;
                return (B)getThis();
            }

            public B setNotificationExtras(Map<String, Object> notificationExtras){
                this.notificationExtras = notificationExtras;
                return (B)getThis();
            }

            protected Builder getThis() {
                return this;
            }

            public NotificationPayload build() {
                return new NotificationPayload(this);
            }
        }
    }
}
