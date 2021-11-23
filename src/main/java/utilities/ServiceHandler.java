package utilities;

import configuration.Constants;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class ServiceHandler {

    public static String send(String url, Constants.METHOD method, Map<String, String> headers, String body) {
        if(method == Constants.METHOD.GET) {
            return doGet(url, headers);
        }
        else if(method == Constants.METHOD.POST) {
            return doPost(url, headers, body);
        }

        return null;
    }

    public static String send(String url, Constants.METHOD method, Map<String, String> headers) {
        if(method == Constants.METHOD.GET) {
            return doGet(url, headers);
        }
        else if(method == Constants.METHOD.POST) {
            return doPost(url, headers);
        }

        return null;
    }

    public static String send(String url, Constants.METHOD method) {
        if(method == Constants.METHOD.GET) {
            return doGet(url);
        }
        else if(method == Constants.METHOD.POST) {
            return doPost(url);
        }

        return null;
    }

    /**
     * Execute an HTTP GET for the specified URL and return the
     * body of the response as a String.
     * @param url
     * @return
     */
    private static String doGet(String url) {
        return doGet(url, null);
    }

    /**
     * Execute an HTTP GET for the specified URL and return
     * the body of the response as a String. Allows request
     * headers to be set.
     * @param url
     * @param headers
     * @return
     */
    private static String doGet(String url, Map<String, String> headers) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder(new URI(url));
            builder = setHeaders(builder, headers);
            HttpRequest request = builder.GET()
                    .build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch(URISyntaxException | IOException | InterruptedException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    private static String doPost(String url, Map<String, String> headers) {
        return doPost(url, headers, null);
    }

    private static String doPost(String url) {
        return doPost(url, null, null);
    }

    /**
     * Execute an HTTP POST for the specified URL and return the body of the
     * response as a String.
     * Headers for the request are provided in the map headers.
     * The body of the request is provided as a String.
     *
     * @param url
     * @param headers
     * @param body
     * @return
     */
    private static String doPost(String url, Map<String, String> headers, String body) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder(new URI(url));
            builder = setHeaders(builder, headers);
            HttpRequest request = builder.POST((HttpRequest.BodyPublishers.ofString(body)))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response);
            return response.body();

        } catch(URISyntaxException | IOException | InterruptedException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    /**
     * Helper method to set the headers of any HttpRequest.Builder.
     * @param builder
     * @param headers
     * @return
     */
    private static HttpRequest.Builder setHeaders(HttpRequest.Builder builder, Map<String, String> headers) {
        if(headers != null) {
            for (String key : headers.keySet()) {
                builder = builder.setHeader(key, headers.get(key));
            }
        }
        return builder;
    }
}
