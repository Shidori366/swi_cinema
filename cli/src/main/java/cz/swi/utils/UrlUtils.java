package cz.swi.utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class UrlUtils {

    private UrlUtils() {}

    public static String createUrlWithParams(String url, Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return url;
        }

        StringBuilder urlWithParams = new StringBuilder(url);
        boolean first = true;

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first) {
                urlWithParams.append('?');
                first = false;
            } else {
                urlWithParams.append('&');
            }

            String encodedKey = URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8);
            String encodedValue = URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8);

            urlWithParams.append(encodedKey).append('=').append(encodedValue);
        };

        return urlWithParams.toString();
    }
}
