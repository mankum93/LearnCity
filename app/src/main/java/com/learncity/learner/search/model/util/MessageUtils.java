package com.learncity.learner.search.model.util;
import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

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

	/**
	 * Returns a random message id to uniquely identify a message - based on time based UUID
	 */
	public static String getUniqueMessageId() {
        return gen.generate().toString();
	}

}
