package com.learncity.util;

import java.util.Arrays;
import java.util.List;

/**
 * Created by DJ on 2/4/2017.
 */

public class ArraysUtil {

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

    public static String[] convertStringToArray(String str) {
        return str.split(ARRAY_SEPARATOR);
    }
}
