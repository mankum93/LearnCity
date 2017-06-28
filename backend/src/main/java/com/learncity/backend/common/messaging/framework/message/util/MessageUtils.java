package com.learncity.backend.common.messaging.framework.message.util;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Enumeration;
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
public final class MessageUtils {

    /**Time based UUID generator from Java UUID Generator(JUG).*/
    private static final TimeBasedGenerator gen = Generators.timeBasedGenerator(fromInterface());
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



	/**
	 *----------------------------------------------------------------------------------------------
	 * NOTE: This method is a corrected version of its counterpart in
	 * {@link com.fasterxml.uuid.EthernetAddress}. There is a bug in the original method
	 * in the line, <code>Enumeration&lt;NetworkInterface&gt; en = NetworkInterface.getNetworkInterfaces();</code>
	 * where en can be null and therefore may end up throwing a {@link NullPointerException} if
	 * the system has no {@link NetworkInterface}s. We correct it here.
	 *
	 * P.S: Last I checked, this bug is still present in JUG 3.3.1.
	 *----------------------------------------------------------------------------------------------
	 *
	 * Factory method that locates a network interface that has
	 * a suitable mac address (ethernet cards, and things that
	 * emulate one), and return that address. If there are multiple
	 * applicable interfaces, one of them is returned; which one
	 * is returned is not specified.
	 * Method is meant for accessing an address needed to construct
	 * generator for time+location based UUID generation method.
	 *
	 * @return Ethernet address of one of interfaces system has;
	 *    not including local or loopback addresses; if one exists,
	 *    null if no such interfaces are found.
	 */
	public static EthernetAddress fromInterface()
	{
		try {
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();

			// CORRECTION
			if(en != null){
				while (en.hasMoreElements()) {
					NetworkInterface nint = en.nextElement();
					if (!nint.isLoopback()) {
						byte[] data = nint.getHardwareAddress();
						if (data != null && data.length == 6) {
							return new EthernetAddress(data);
						}
					}
				}
			}

		} catch (java.net.SocketException e) {
			// fine, let's take is as signal of not having any interfaces
		}
		return null;
	}

}
