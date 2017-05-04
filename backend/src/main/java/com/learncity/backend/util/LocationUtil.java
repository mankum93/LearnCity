package com.learncity.backend.util;

import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.learncity.backend.common.account.create.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by DJ on 3/7/2017.
 */

public class LocationUtil {

    private static final Logger logger = Logger.getLogger(LocationUtil.class.getName());

    private static final String API_KEY = "AIzaSyCSi1Df1lZNv207--_xddTvrpwHiyNXeJk";

    private static final GeocodingService service = new GeocodingService();

    private static final Gson g = new Gson();

    public static String getFormattedAddress(final LatLng geoCoordinates){

        logger.info(new StringBuilder("Geocoordinates to be reverse geocoded: ")
                        .append("{")
                        .append(geoCoordinates.getLatitude()).append(",")
                        .append(geoCoordinates.getLongitude()).append("}").toString());

        Map<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("key", API_KEY);
        urlParams.put("result_type", "route|political|sublocality");
        urlParams.put("location_type", "APPROXIMATE");

        String response = null;
        JsonObject jsonObject = service.reverseGeocode(geoCoordinates, urlParams);
        if(jsonObject!= null){
            logger.info("JSON Response received from Reverse Geocoding: " + jsonObject);
            response = parseResponse(jsonObject);

        }
        logger.info("Response extracted from JSON Addresses: " + response);
        return response;
    }

    public static String getFormattedAddress(final LatLng geoCoordinates, Map<String, String> urlParams){

        logger.info(new StringBuilder("Geocoordinates to be reverse geocoded: ")
                .append("{")
                .append(geoCoordinates.getLatitude()).append(",")
                .append(geoCoordinates.getLongitude()).append("}").toString());

        if(urlParams == null){
            urlParams = new HashMap<String, String>();
        }
        urlParams.put("key", API_KEY);

        String response = null;
        JsonObject jsonObject = service.reverseGeocode(geoCoordinates, urlParams);
        if(jsonObject!= null){
            logger.info("JSON Response received from Reverse Geocoding: " + jsonObject);
            response = parseResponse(jsonObject);

        }
        logger.info("Response extracted from JSON Addresses: " + response);
        return response;
    }

    public static String getFormattedAddress(final LatLng geoCoordinates, Map<String, String> urlParams, boolean shouldUseKey){

        logger.info(new StringBuilder("Geocoordinates to be reverse geocoded: ")
                .append("{")
                .append(geoCoordinates.getLatitude()).append(",")
                .append(geoCoordinates.getLongitude()).append("}").toString());

        if(shouldUseKey){
            if(urlParams == null){
                urlParams = new HashMap<String, String>();
            }
            urlParams.put("key", API_KEY);
        }
        String response = null;
        JsonObject jsonObject = service.reverseGeocode(geoCoordinates, urlParams);
        if(jsonObject!= null){
            logger.info("JSON Response received from Reverse Geocoding: " + jsonObject);
            response = parseResponse(jsonObject);

        }
        logger.info("Response extracted from JSON Addresses: " + response);
        return response;
    }

    private static String parseResponse(JsonObject response){

        // Get the status first
        String status = response.get("status").getAsString();
        if(!status.equals("OK")){
            return status;
        }

        StringBuilder formattedAddress = new StringBuilder(30);

        JsonArray results = response.get("results").getAsJsonArray();
        // Each result is an Address component
        // Get the address from first element having location_type as "APPROXIMATE"
        for(JsonElement addr : results){
            if(addr.getAsJsonObject()
                    .get("geometry").getAsJsonObject()
                    .get("location_type").getAsString().equals("APPROXIMATE"));{

                JsonArray addrElements = addr.getAsJsonObject().get("address_components").getAsJsonArray();
                // Get the first 3 address elements
                int i=0;
                for(JsonElement o : addrElements){
                    formattedAddress.append(o.getAsJsonObject().get("long_name").getAsString()).append(", ");
                    i++;
                    if(i==3){
                        formattedAddress.delete(formattedAddress.length()-2, formattedAddress.length()-1);
                        break;
                    }
                }
                break;
            }
        }
        return formattedAddress.toString();
    }


    private static class GeocodingService{

        private static final String ROOT_URL = "https://maps.googleapis.com/maps/api/geocode/json";

        private static final HttpTransport HTTP_TRANSPORT = new UrlFetchTransport();
        private static final JsonFactory JSON_FACTORY = new GsonFactory();

        private static final HttpRequestFactory requestFactory =
                HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest request) {
                        request.setParser(new JsonObjectParser(JSON_FACTORY));
                    }
                });

        public static class GeocoderUrl extends GenericUrl{
            public GeocoderUrl(String encodedUrl) {
                super(encodedUrl);
            }

        }

        /**Return the complete Reverse geocoded result(JSON)*/
        public JsonObject reverseGeocode(LatLng geoCoordinates){
            GeocoderUrl url = buildUrlWithLatLng(geoCoordinates);
            return sendRequest(url);
        }
        public JsonObject reverseGeocode(LatLng geoCoordinates, Map<String, String> urlFields){
            GeocoderUrl url = buildUrlWithLatLng(geoCoordinates);
            if(urlFields != null){
                url.putAll(urlFields);
            }
            return sendRequest(url);
        }
        private GeocoderUrl buildUrlWithLatLng(LatLng geoCoordinates){
            GeocoderUrl url = new GeocoderUrl(ROOT_URL);
            url.put("latlng", geoCoordinates.getLatitude() + "," + geoCoordinates.getLongitude());
            return url;
        }
        private JsonObject sendRequest(GeocoderUrl url){

            logger.info("URL for request: " + url.build());

            HttpRequest request;
            HttpResponse response;

            JsonObject jsonObject;
            try {
                request = requestFactory.buildGetRequest(url);
                response = request.execute();
                // Get the content of the HTTPResponse
                InputStream in = response.getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                Writer writer = new StringWriter();
                String readed;
                while ((readed = br.readLine()) != null) {
                    writer.write(readed);
                }
                logger.info("HTTP response from Geocoding request: " + writer.toString());
                jsonObject = g.fromJson(writer.toString(), JsonObject.class);
                logger.info("Parsed JSON response from Geocoding request: " + jsonObject);
            }
            catch (HttpResponseException e) {
                e.printStackTrace();
                return null;
            }
            catch(IOException ioe){
                ioe.printStackTrace();
                return null;
            }
            return jsonObject;
        }

    }
}
