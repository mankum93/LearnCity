package com.learncity.backend.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Created by DJ on 3/13/2017.
 */

public class JSONUtils {


    public static void removeDuplicateStringElementsFromAllArrays(String file) throws IOException {

        Writer fileWriter = new BufferedWriter(new FileWriter(new File("out.json")));

        JsonFactory f = new MappingJsonFactory();
        JsonParser jp = f.createJsonParser(new File(file));

        JsonToken current;

        current = jp.nextToken();
        if (current != JsonToken.START_OBJECT) {
            throw new RuntimeException("Error: root should be object: quiting.");
        }

        while (jp.nextToken() != JsonToken.END_OBJECT) {

            String fieldName = jp.getCurrentName();

            // move from field name to field value
            current = jp.nextToken();
            if (current == JsonToken.START_ARRAY) {
                // For each of the records in the array
                while (jp.nextToken() != JsonToken.END_ARRAY) {
                    // read the record into a tree model,
                    // this moves the parsing position to the end of it
                    JsonNode node = jp.readValueAsTree();
                    // And now we have random access to everything in the object

                    // If not basic JSON string value , skip
                    if(!node.isTextual()){
                        jp.getText(fileWriter);
                    }
                    else{
                        // Retrieve all
                    }
                }
            } else {
                // Write the JSON data to a file as it is
                jp.getText(fileWriter);
            }
        }
    }
}
