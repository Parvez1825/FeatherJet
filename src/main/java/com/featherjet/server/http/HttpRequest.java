package com.featherjet.server.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP request representation
 */
public class HttpRequest {
    
    private String method;
    private String path;
    private String protocol;
    private Map<String, String> headers = new HashMap<>();
    private String body;
    
    public static HttpRequest parse(BufferedReader reader) throws IOException {
        // Read request line
        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.trim().isEmpty()) {
            return null;
        }
        
        String[] parts = requestLine.split(" ");
        if (parts.length != 3) {
            return null;
        }
        
        HttpRequest request = new HttpRequest();
        request.method = parts[0];
        request.path = parts[1];
        request.protocol = parts[2];
        
        // Read headers
        String line;
        while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
            int colonIndex = line.indexOf(':');
            if (colonIndex > 0) {
                String headerName = line.substring(0, colonIndex).trim();
                String headerValue = line.substring(colonIndex + 1).trim();
                request.headers.put(headerName.toLowerCase(), headerValue);
            }
        }
        
        // Read body if content-length is specified
        String contentLengthHeader = request.headers.get("content-length");
        if (contentLengthHeader != null) {
            try {
                int contentLength = Integer.parseInt(contentLengthHeader);
                if (contentLength > 0) {
                    char[] bodyChars = new char[contentLength];
                    int totalRead = 0;
                    while (totalRead < contentLength) {
                        int read = reader.read(bodyChars, totalRead, contentLength - totalRead);
                        if (read == -1) break;
                        totalRead += read;
                    }
                    request.body = new String(bodyChars, 0, totalRead);
                }
            } catch (NumberFormatException e) {
                // Invalid content-length, ignore
            }
        }
        
        return request;
    }
    
    // Getters
    public String getMethod() { return method; }
    public String getPath() { return path; }
    public String getProtocol() { return protocol; }
    public Map<String, String> getHeaders() { return headers; }
    public String getBody() { return body; }
    
    public String getHeader(String name) {
        return headers.get(name.toLowerCase());
    }
    
    // Query parameter parsing
    public Map<String, String> getQueryParameters() {
        Map<String, String> params = new HashMap<>();
        
        if (path == null) return params;
        
        int questionIndex = path.indexOf('?');
        if (questionIndex == -1) return params;
        
        String queryString = path.substring(questionIndex + 1);
        String[] pairs = queryString.split("&");
        
        for (String pair : pairs) {
            int equalIndex = pair.indexOf('=');
            if (equalIndex > 0) {
                String key = pair.substring(0, equalIndex);
                String value = pair.substring(equalIndex + 1);
                params.put(key, value);
            }
        }
        
        return params;
    }
    
    @Override
    public String toString() {
        return String.format("%s %s %s", method, path, protocol);
    }
}
