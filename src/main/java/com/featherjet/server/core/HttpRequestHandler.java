package com.featherjet.server.core;

import com.featherjet.server.config.ServerConfig;
import com.featherjet.server.http.HttpRequest;
import com.featherjet.server.http.HttpResponse;
import com.featherjet.server.util.Logger;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Handles individual HTTP requests
 */
public class HttpRequestHandler implements Runnable {
    
    private static final Logger logger = Logger.getLogger(HttpRequestHandler.class);
    
    private final Socket clientSocket;
    private final ServerConfig config;
    private final WebAppManager webAppManager;
    private final AtomicLong requestCount;
    
    public HttpRequestHandler(Socket clientSocket, ServerConfig config, 
                             WebAppManager webAppManager, AtomicLong requestCount) {
        this.clientSocket = clientSocket;
        this.config = config;
        this.webAppManager = webAppManager;
        this.requestCount = requestCount;
    }
    
    @Override
    public void run() {
        long requestId = requestCount.incrementAndGet();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
             OutputStream outputStream = clientSocket.getOutputStream()) {
            
            // Parse HTTP request
            HttpRequest request = HttpRequest.parse(reader);
            if (request == null) {
                sendBadRequest(writer);
                return;
            }
            
            logger.debug("Request #{}: {} {}", requestId, request.getMethod(), request.getPath());
            
            // Create response
            HttpResponse response = new HttpResponse();
            
            // Handle special endpoints
            if (request.getPath().equals("/_metrics")) {
                handleMetrics(response);
            } else if (request.getPath().equals("/_health")) {
                handleHealth(response);
            } else {
                // Handle regular requests
                handleRequest(request, response);
            }
            
            // Send response
            sendResponse(writer, outputStream, response);
            
            logger.debug("Request #{} completed with status {}", requestId, response.getStatusCode());
            
        } catch (Exception e) {
            logger.error("Error handling request #{}: {}", requestId, e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                logger.warn("Error closing client socket: {}", e.getMessage());
            }
        }
    }
    
    private void handleRequest(HttpRequest request, HttpResponse response) throws IOException {
        String path = request.getPath();
        
        // Remove query parameters
        int queryIndex = path.indexOf('?');
        if (queryIndex != -1) {
            path = path.substring(0, queryIndex);
        }
        
        // Normalize path
        if (path.equals("/")) {
            path = "/index.html";
        }
        
        // Determine web application
        String contextPath = "";
        String resourcePath = path;
        
        if (path.startsWith("/")) {
            String[] pathParts = path.substring(1).split("/");
            if (pathParts.length > 0) {
                String firstPart = pathParts[0];
                if (webAppManager.hasWebApp(firstPart)) {
                    contextPath = "/" + firstPart;
                    resourcePath = path.substring(contextPath.length());
                    if (resourcePath.isEmpty()) {
                        resourcePath = "/";
                    }
                }
            }
        }
        
        // Try to serve from web application
        WebApp webApp = webAppManager.getWebApp(contextPath.isEmpty() ? "ROOT" : contextPath.substring(1));
        if (webApp != null) {
            webApp.handleRequest(request, response, resourcePath);
        } else {
            // Serve static content from ROOT
            WebApp rootApp = webAppManager.getWebApp("ROOT");
            if (rootApp != null) {
                rootApp.handleRequest(request, response, path);
            } else {
                response.setStatus(404, "Not Found");
                response.setBody("404 - Not Found");
            }
        }
    }
    
    private void handleMetrics(HttpResponse response) {
        String metrics = String.format("""
            {
              "uptime": "%s",
              "requests": {
                "total": %d,
                "rate": "%.1f/sec"
              },
              "threads": {
                "active": %d,
                "total": %d
              },
              "memory": {
                "used": "%d MB",
                "free": "%d MB",
                "total": "%d MB"
              }
            }
            """,
            getUptime(),
            requestCount.get(),
            calculateRequestRate(),
            Thread.activeCount(),
            config.getMaxThreads(),
            (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024,
            Runtime.getRuntime().freeMemory() / 1024 / 1024,
            Runtime.getRuntime().totalMemory() / 1024 / 1024
        );
        
        response.setStatus(200, "OK");
        response.setHeader("Content-Type", "application/json");
        response.setBody(metrics);
    }
    
    private void handleHealth(HttpResponse response) {
        response.setStatus(200, "OK");
        response.setHeader("Content-Type", "application/json");
        response.setBody("{\"status\": \"UP\", \"server\": \"FeatherJet\"}");
    }
    
    private void sendBadRequest(PrintWriter writer) {
        writer.println("HTTP/1.1 400 Bad Request");
        writer.println("Content-Type: text/plain");
        writer.println("Content-Length: 11");
        writer.println();
        writer.println("Bad Request");
    }
    
    private void sendResponse(PrintWriter writer, OutputStream outputStream, HttpResponse response) throws IOException {
        // Send status line
        writer.printf("HTTP/1.1 %d %s\r\n", response.getStatusCode(), response.getStatusMessage());
        
        // Send headers
        for (String headerName : response.getHeaders().keySet()) {
            writer.printf("%s: %s\r\n", headerName, response.getHeaders().get(headerName));
        }
        
        // Send content length if body exists
        byte[] body = response.getBodyBytes();
        if (body != null) {
            writer.printf("Content-Length: %d\r\n", body.length);
        }
        
        // End headers
        writer.println();
        writer.flush();
        
        // Send body
        if (body != null) {
            outputStream.write(body);
            outputStream.flush();
        }
    }
    
    private String getUptime() {
        long uptimeMs = System.currentTimeMillis() - getStartTime();
        long seconds = uptimeMs / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        return String.format("%dh %dm %ds", hours, minutes % 60, seconds % 60);
    }
    
    private double calculateRequestRate() {
        long uptimeMs = System.currentTimeMillis() - getStartTime();
        if (uptimeMs == 0) return 0.0;
        return (requestCount.get() * 1000.0) / uptimeMs;
    }
    
    private long getStartTime() {
        // This should be stored when the server starts, for now return a reasonable value
        return System.currentTimeMillis() - 3600000; // 1 hour ago as placeholder
    }
}
