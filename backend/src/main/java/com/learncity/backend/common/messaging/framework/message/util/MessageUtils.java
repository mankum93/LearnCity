package com.learncity.backend.common.messaging.framework.message.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.learncity.backend.common.messaging.framework.message.model.outgoing.AbstractDownstreamMessage;
import com.learncity.backend.common.messaging.framework.message.model.outgoing.DataMessage;
import com.learncity.backend.common.messaging.framework.message.model.outgoing.NotificationMessage;

/**
 * Includes common message utils like:
 * <ul>
 *     <li>To transform an object to its Attribute Map.(and Vice Versa)</li>
 *     <li>Pretty print Json</li>
 * </ul>
 */
public class MessageUtils {

    /**Time based UUID generator from Java UUID Generator(JUG).*/
    private static final TimeBasedGenerator gen = Generators.timeBasedGenerator(EthernetAddress.fromInterface());
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Map<String, Object> toAttributeMap(Object object){
        return objectMapper.convertValue(object, new TypeReference<Map<String, Object>>() {
        });
    }

    public static Map<String, Object> getAttributeMapFromJsonString(String json){
        Map<String, Object> map;
        try {
            map = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("There was a problem parsing the JSON string.");
        }
        return map;
    }

    public static String getPrettyPrintedJson(Object toBePrettyPrinted){
        // TODO: It doesn't actually pretty print. Check, why not?
    	String prettyPrint = null;
		try {
			prettyPrint = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(toBePrettyPrinted);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return prettyPrint;
	}

	/**
	 * Creates a JSON encoded ACK message for a received incoming message.
     *
     * Use UpstreamMessage.getAcknowledgement() to get the Acknowledgement now.
	 */
	@Deprecated
	public static String createJsonAck(String to, String messageId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("message_type", "ack");
		map.put("to", to);
		map.put("message_id", messageId);

		return createJsonMessage(map);
	}

	public static String createJsonMessage(Map<String, Object> jsonMap) {
	    String json;
        try {
            json = objectMapper.writeValueAsString(jsonMap);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("There was a problem processing Json.");
        }
		return json;
	}

	/**
	 * Returns a random message id to uniquely identify a message - based on time based UUID
	 */
	public static String getUniqueMessageId() {
        return gen.generate().toString();
	}

}
