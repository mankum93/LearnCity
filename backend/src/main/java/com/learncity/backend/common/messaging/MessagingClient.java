package com.learncity.backend.common.messaging;

import com.google.appengine.repackaged.com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.appengine.repackaged.com.google.api.client.http.GenericUrl;
import com.google.appengine.repackaged.com.google.api.client.http.HttpHeaders;
import com.google.appengine.repackaged.com.google.api.client.http.HttpRequest;
import com.google.appengine.repackaged.com.google.api.client.http.HttpRequestFactory;
import com.google.appengine.repackaged.com.google.api.client.http.HttpRequestInitializer;
import com.google.appengine.repackaged.com.google.api.client.http.HttpResponse;
import com.google.appengine.repackaged.com.google.api.client.http.HttpTransport;
import com.google.appengine.repackaged.com.google.api.client.http.json.JsonHttpContent;
import com.google.appengine.repackaged.com.google.api.client.json.JsonFactory;
import com.google.appengine.repackaged.com.google.api.client.json.JsonObjectParser;
import com.google.appengine.repackaged.com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by DJ on 5/21/2017.
 */

/**
 * This class uses <a href="https://developers.google.com/api-client-library/java/google-http-java-client/">Google's HTTP Client Library</a>,
 * using URL Fetch Service(through the {@link UrlFetchTransport})
 */
public class MessagingClient {

    private static final Logger logger = Logger.getLogger(MessagingClient.class.getSimpleName());

    private static final HttpTransport HTTP_TRANSPORT = UrlFetchTransport.getDefaultInstance();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    private static final String MESSAGING_SERVICE_URI = "http://127.0.0.1:8090/send";
    private static final GenericUrl URL = new GenericUrl(MESSAGING_SERVICE_URI);

    // Configure a HTTPRequest Factory
    private static final HttpRequestFactory requestFactory =
            HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                @Override
                public void initialize(HttpRequest request) {
                    request.setParser(new JsonObjectParser(JSON_FACTORY));
                }
            });

    private static final MessagingClient DEFAULT_INSTANCE = new MessagingClient();

    public static final MessagingClient getDefaultInstance(){
        return DEFAULT_INSTANCE;
    }

    public void sendMessage(String jsonMessage){

        // Build a HTTP Post Request
        HttpRequest request;
        try {
            request = requestFactory.buildPostRequest(URL, new JsonHttpContent(JSON_FACTORY, jsonMessage));
            // URL Fetch will automatically set the header, "X-Appengine-Inbound-Appid"
            // because of disabling Redirects.
            request.setFollowRedirects(false);

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept("application/json");
            headers.setContentType("application/json; charset=utf-8");
            request.setHeaders(headers);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("There was a problem parsing the JSON Message", e);
        }

        // Execute the request.
        try {
            parseResponse(request.execute());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseResponse(HttpResponse response){

    }
}
