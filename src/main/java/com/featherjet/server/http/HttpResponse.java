package com.featherjet.server.http;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTP response representation
 */
public class HttpResponse {
    
    private int statusCode = 200;
    private String statusMessage = "OK";
    private Map<String, String> headers = new HashMap<>();
    private String body;
    private byte[] bodyBytes;
    
    public HttpResponse() {
        // Set default headers
        headers.put("Server", "FeatherJet/1.0.0");
        headers.put("Connection", "close");
        headers.put("Cache-Control", "no-cache");
    }
    
    public void setStatus(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }
    
    public void setHeader(String name, String value) {
        headers.put(name, value);
    }
    
    public void setBody(String body) {
        this.body = body;
        this.bodyBytes = body != null ? body.getBytes() : null;
        if (bodyBytes != null) {
            setHeader("Content-Length", String.valueOf(bodyBytes.length));
        }
    }
    
    public void setBodyBytes(byte[] bodyBytes) {
        this.bodyBytes = bodyBytes;
        this.body = null;
        if (bodyBytes != null) {
            setHeader("Content-Length", String.valueOf(bodyBytes.length));
        }
    }
    
    // Getters
    public int getStatusCode() { return statusCode; }
    public String getStatusMessage() { return statusMessage; }
    public Map<String, String> getHeaders() { return headers; }
    public String getBody() { return body; }
    public byte[] getBodyBytes() { return bodyBytes; }
    
    public String getHeader(String name) {
        return headers.get(name);
    }
}
