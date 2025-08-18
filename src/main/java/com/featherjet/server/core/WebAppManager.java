package com.featherjet.server.core;

import com.featherjet.server.config.ServerConfig;
import com.featherjet.server.util.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Manages web applications
 */
public class WebAppManager {
    
    private static final Logger logger = Logger.getLogger(WebAppManager.class);
    
    private final ServerConfig config;
    private final ConcurrentMap<String, WebApp> webApps = new ConcurrentHashMap<>();
    private final Path webAppsPath;
    
    public WebAppManager(ServerConfig config) {
        this.config = config;
        this.webAppsPath = Paths.get(config.getWebAppsDir());
    }
    
    public void initialize() throws IOException {
        logger.info("Initializing web applications from: {}", webAppsPath.toAbsolutePath());
        
        // Create webapps directory if it doesn't exist
        if (!Files.exists(webAppsPath)) {
            Files.createDirectories(webAppsPath);
            logger.info("Created webapps directory: {}", webAppsPath);
        }
        
        // Create ROOT application if it doesn't exist
        Path rootPath = webAppsPath.resolve("ROOT");
        if (!Files.exists(rootPath)) {
            createDefaultRootApp(rootPath);
        }
        
        // Load all web applications
        loadWebApps();
    }
    
    private void createDefaultRootApp(Path rootPath) throws IOException {
        Files.createDirectories(rootPath);
        
        // Create default index.html
        Path indexPath = rootPath.resolve("index.html");
        String defaultContent = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>FeatherJet Server</title>
                <style>
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        margin: 0;
                        padding: 40px;
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        color: white;
                        min-height: 100vh;
                        display: flex;
                        flex-direction: column;
                        align-items: center;
                        justify-content: center;
                    }
                    .container {
                        text-align: center;
                        background: rgba(255, 255, 255, 0.1);
                        padding: 40px;
                        border-radius: 20px;
                        backdrop-filter: blur(10px);
                        box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.37);
                        border: 1px solid rgba(255, 255, 255, 0.18);
                    }
                    h1 {
                        font-size: 3em;
                        margin-bottom: 20px;
                        text-shadow: 2px 2px 4px rgba(0,0,0,0.5);
                    }
                    .tagline {
                        font-size: 1.2em;
                        margin-bottom: 30px;
                        opacity: 0.9;
                    }
                    .features {
                        display: grid;
                        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
                        gap: 20px;
                        margin-top: 30px;
                    }
                    .feature {
                        background: rgba(255, 255, 255, 0.1);
                        padding: 20px;
                        border-radius: 10px;
                        border: 1px solid rgba(255, 255, 255, 0.2);
                    }
                    .feature h3 {
                        margin-top: 0;
                        color: #FFD700;
                    }
                    .metrics {
                        margin-top: 30px;
                        padding: 20px;
                        background: rgba(0, 0, 0, 0.2);
                        border-radius: 10px;
                    }
                    .metrics a {
                        color: #FFD700;
                        text-decoration: none;
                        font-weight: bold;
                    }
                    .metrics a:hover {
                        text-decoration: underline;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>🚀 FeatherJet Server</h1>
                    <p class="tagline">Light as a feather, fast as a jet!</p>
                    
                    <div class="features">
                        <div class="feature">
                            <h3>⚡ Ultra Lightweight</h3>
                            <p>~10MB total footprint</p>
                        </div>
                        <div class="feature">
                            <h3>🔥 High Performance</h3>
                            <p>Multi-threaded request handling</p>
                        </div>
                        <div class="feature">
                            <h3>🛠️ Easy to Use</h3>
                            <p>Simple configuration and deployment</p>
                        </div>
                        <div class="feature">
                            <h3>🌐 Cross Platform</h3>
                            <p>Runs on any Java-enabled system</p>
                        </div>
                    </div>
                    
                    <div class="metrics">
                        <h3>Server Status</h3>
                        <p>✅ Server is running successfully!</p>
                        <p><a href="/_health">Health Check</a> | <a href="/_metrics">Metrics</a></p>
                    </div>
                </div>
                
                <script>
                    // Add some interactivity
                    document.addEventListener('DOMContentLoaded', function() {
                        console.log('FeatherJet Server - Welcome to the fast lane! 🚀');
                    });
                </script>
            </body>
            </html>
            """;
        
        Files.write(indexPath, defaultContent.getBytes());
        logger.info("Created default ROOT application at: {}", rootPath);
    }
    
    private void loadWebApps() throws IOException {
        if (!Files.exists(webAppsPath)) {
            return;
        }
        
        Files.list(webAppsPath)
             .filter(Files::isDirectory)
             .forEach(appPath -> {
                 try {
                     String appName = appPath.getFileName().toString();
                     WebApp webApp = new WebApp(appName, appPath, config);
                     webApp.initialize();
                     webApps.put(appName, webApp);
                     logger.info("Loaded web application: {}", appName);
                 } catch (Exception e) {
                     logger.error("Failed to load web application at {}: {}", appPath, e.getMessage());
                 }
             });
    }
    
    public WebApp getWebApp(String name) {
        return webApps.get(name);
    }
    
    public boolean hasWebApp(String name) {
        return webApps.containsKey(name);
    }
    
    public void destroy() {
        for (WebApp webApp : webApps.values()) {
            try {
                webApp.destroy();
            } catch (Exception e) {
                logger.error("Error destroying web application {}: {}", webApp.getName(), e.getMessage());
            }
        }
        webApps.clear();
    }
}
