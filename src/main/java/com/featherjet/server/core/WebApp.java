package com.featherjet.server.core;

import com.featherjet.server.config.ServerConfig;
import com.featherjet.server.http.HttpRequest;
import com.featherjet.server.http.HttpResponse;
import com.featherjet.server.util.Logger;
import com.featherjet.server.util.MimeTypeUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Represents a web application
 */
public class WebApp {
    
    private static final Logger logger = Logger.getLogger(WebApp.class);
    
    private final String name;
    private final Path appPath;
    private final ServerConfig config;
    
    public WebApp(String name, Path appPath, ServerConfig config) {
        this.name = name;
        this.appPath = appPath;
        this.config = config;
    }
    
    public void initialize() throws IOException {
        logger.debug("Initializing web application: {}", name);
        
        // Create basic directory structure if needed
        Path webInfPath = appPath.resolve("WEB-INF");
        if (!Files.exists(webInfPath)) {
            Files.createDirectories(webInfPath);
        }
        
        // You could load web.xml here for servlet configuration
        // For now, we'll just serve static content
    }
    
    public void handleRequest(HttpRequest request, HttpResponse response, String resourcePath) throws IOException {
        logger.debug("Handling request for resource: {} in app: {}", resourcePath, name);
        
        // Normalize resource path
        if (resourcePath.equals("/")) {
            resourcePath = "/index.html";
        }
        
        // Security check - prevent directory traversal
        if (resourcePath.contains("..") || resourcePath.contains("\\")) {
            response.setStatus(400, "Bad Request");
            response.setBody("Invalid resource path");
            return;
        }
        
        // Resolve file path
        Path filePath = appPath.resolve(resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath);
        
        if (!Files.exists(filePath)) {
            handle404(response, resourcePath);
            return;
        }
        
        if (Files.isDirectory(filePath)) {
            // Try to serve index.html from directory
            Path indexPath = filePath.resolve("index.html");
            if (Files.exists(indexPath)) {
                serveFile(indexPath, response);
            } else {
                handleDirectoryListing(filePath, response, resourcePath);
            }
        } else {
            serveFile(filePath, response);
        }
    }
    
    private void serveFile(Path filePath, HttpResponse response) throws IOException {
        byte[] content = Files.readAllBytes(filePath);
        String mimeType = MimeTypeUtil.getMimeType(filePath.toString());
        
        response.setStatus(200, "OK");
        response.setHeader("Content-Type", mimeType);
        response.setHeader("Content-Length", String.valueOf(content.length));
        response.setBodyBytes(content);
        
        logger.debug("Served file: {} ({} bytes, {})", filePath.getFileName(), content.length, mimeType);
    }
    
    private void handle404(HttpResponse response, String resourcePath) {
        String html = String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <title>404 - Not Found</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 40px; }
                    .error-container { max-width: 600px; margin: 0 auto; text-align: center; }
                    h1 { color: #d32f2f; }
                    .resource-path { font-family: monospace; background: #f5f5f5; padding: 10px; border-radius: 4px; }
                </style>
            </head>
            <body>
                <div class="error-container">
                    <h1>404 - Not Found</h1>
                    <p>The requested resource was not found:</p>
                    <div class="resource-path">%s</div>
                    <p><a href="/">Go back to home</a></p>
                </div>
            </body>
            </html>
            """, resourcePath);
        
        response.setStatus(404, "Not Found");
        response.setHeader("Content-Type", "text/html");
        response.setBody(html);
    }
    
    private void handleDirectoryListing(Path dirPath, HttpResponse response, String resourcePath) throws IOException {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n<head>\n");
        html.append("<title>Directory listing for ").append(resourcePath).append("</title>\n");
        html.append("<style>\n");
        html.append("body { font-family: Arial, sans-serif; margin: 40px; }\n");
        html.append("table { border-collapse: collapse; width: 100%; }\n");
        html.append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n");
        html.append("th { background-color: #f2f2f2; }\n");
        html.append("a { text-decoration: none; color: #1976d2; }\n");
        html.append("a:hover { text-decoration: underline; }\n");
        html.append("</style>\n");
        html.append("</head>\n<body>\n");
        html.append("<h1>Directory listing for ").append(resourcePath).append("</h1>\n");
        html.append("<table>\n");
        html.append("<tr><th>Name</th><th>Size</th><th>Modified</th></tr>\n");
        
        // Add parent directory link if not root
        if (!resourcePath.equals("/")) {
            String parentPath = resourcePath.substring(0, resourcePath.lastIndexOf('/'));
            if (parentPath.isEmpty()) parentPath = "/";
            html.append("<tr><td><a href=\"").append(parentPath).append("\">..</a></td><td>-</td><td>-</td></tr>\n");
        }
        
        // List directory contents
        Files.list(dirPath).forEach(path -> {
            try {
                String fileName = path.getFileName().toString();
                String href = resourcePath.endsWith("/") ? resourcePath + fileName : resourcePath + "/" + fileName;
                
                if (Files.isDirectory(path)) {
                    fileName += "/";
                    href += "/";
                }
                
                long size = Files.isDirectory(path) ? -1 : Files.size(path);
                String sizeStr = size == -1 ? "-" : formatBytes(size);
                String modified = Files.getLastModifiedTime(path).toString();
                
                html.append("<tr>");
                html.append("<td><a href=\"").append(href).append("\">").append(fileName).append("</a></td>");
                html.append("<td>").append(sizeStr).append("</td>");
                html.append("<td>").append(modified).append("</td>");
                html.append("</tr>\n");
            } catch (IOException e) {
                logger.warn("Error reading file info for {}: {}", path, e.getMessage());
            }
        });
        
        html.append("</table>\n");
        html.append("</body>\n</html>");
        
        response.setStatus(200, "OK");
        response.setHeader("Content-Type", "text/html");
        response.setBody(html.toString());
    }
    
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }
    
    public String getName() {
        return name;
    }
    
    public Path getAppPath() {
        return appPath;
    }
    
    public void destroy() {
        logger.debug("Destroying web application: {}", name);
        // Cleanup resources here if needed
    }
}
