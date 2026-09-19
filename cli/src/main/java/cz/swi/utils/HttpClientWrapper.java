package cz.swi.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class HttpClientWrapper {
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private HttpClientWrapper() {}

    public static <T> T getRequest(String url, Map<String, String> params, Class<T> responseType) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(UrlUtils.createUrlWithParams(url, params)))
                .GET().build();

        return sendRequestAndGetResult(request, responseType);
    }

    public static <T, B> T postRequest(String url, Map<String, String> params, B body, Class<T> responseType) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(UrlUtils.createUrlWithParams(url, params)))
                .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(body))).build();

        return sendRequestAndGetResult(request, responseType);
    }

    public static <B> boolean postRequestNoResponse(String url, Map<String, String> params, B body) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(UrlUtils.createUrlWithParams(url, params)))
                .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(body))).build();

        HttpResponse<Void> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.discarding());

        return response.statusCode() < 400;
    }

    public static boolean deleteRequestNoResponse(String url, Map<String, String> params) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(UrlUtils.createUrlWithParams(url, params)))
                .DELETE().build();

        HttpResponse<Void> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.discarding());

        return response.statusCode() < 400;
    }

    private static <T> T sendRequestAndGetResult(HttpRequest request, Class<T> responseType) throws IOException, InterruptedException {
        HttpResponse<InputStream> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream());

        if (response.statusCode() >= 400) {
            throw new RuntimeException("HTTP Request failed with status: " + response.statusCode());
        }

        try (InputStream inputStream = response.body()){
            return OBJECT_MAPPER.readValue(inputStream, responseType);
        }
    }


}
