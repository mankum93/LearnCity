package com.learncity.backend.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.logging.Logger;

/**
 * Created by DJ on 2/4/2017.
 */

public class ArrayUtils {

    private static final Logger logger = Logger.getLogger(ArrayUtils.class.getSimpleName());

    private static String ARRAY_SEPARATOR = "__,__";

    public static String getArraySeparator() {
        return ARRAY_SEPARATOR;
    }

    public static void setArraySeparator(String arraySeparator) {
        ARRAY_SEPARATOR = arraySeparator;
    }

    public static String convertArrayToString(String[] stringArray) {
        StringBuffer stringBuffer = new StringBuffer();
        for (String str : stringArray) {
            stringBuffer.append(str).append(ARRAY_SEPARATOR);
        }

        // Remove last separator
        int lastIndex = stringBuffer.lastIndexOf(ARRAY_SEPARATOR);
        stringBuffer.delete(lastIndex, lastIndex + ARRAY_SEPARATOR.length() + 1);

        return stringBuffer.toString();
    }

    public static String convertArrayToString(String[] stringArray, String separator) {
        StringBuffer stringBuffer = new StringBuffer();
        for (String str : stringArray) {
            stringBuffer.append(str).append(separator);
        }

        // Remove last separator
        int lastIndex = stringBuffer.lastIndexOf(separator);
        stringBuffer.delete(lastIndex, lastIndex + separator.length() + 1);

        return stringBuffer.toString();
    }
    public static String[] convertStringToArray(String str, String separator) {
        return str.split(separator);
    }

    public static String[] convertStringToArray(String str) {
        return str.split(ARRAY_SEPARATOR);
    }

    public static String[] trimArray(String[] array){
        for (int i = 0; i < array.length; i++)
            array[i] = array[i].trim();

        logger.info("Trimmed Array: " + Arrays.toString(array));
        return array;
    }

    public static String[] ensureUniqueness(String[] source){
        if(source == null || source.length == 0){
            return source;
        }
        source = new HashSet<String>(Arrays.asList(source)).toArray(new String[0]);

        logger.info("Array with duplicates removed: " + Arrays.toString(source));
        return source;
    }
}
