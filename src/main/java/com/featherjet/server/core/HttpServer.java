package com.featherjet.server.core;

import com.featherjet.server.config.ServerConfig;
import com.featherjet.server.util.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Core HTTP Server implementation
 */
public class HttpServer {
    
    private static final Logger logger = Logger.getLogger(HttpServer.class);
    
    private final ServerConfig config;
    private final ExecutorService threadPool;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicLong requestCount = new AtomicLong(0);
    private ServerSocket serverSocket;
    private WebAppManager webAppManager;
    
    public HttpServer(ServerConfig config) {
        this.config = config;
        this.threadPool = Executors.newFixedThreadPool(config.getMaxThreads());
        this.webAppManager = new WebAppManager(config);
    }
    
    public void start() throws IOException {
        if (running.get()) {
            throw new IllegalStateException("Server is already running");
        }
        
        // Initialize web applications
        webAppManager.initialize();
        
        // Create server socket
        serverSocket = new ServerSocket();
        serverSocket.setReuseAddress(true);
        serverSocket.bind(new InetSocketAddress(config.getHost(), config.getPort()));
        
        running.set(true);
        
        logger.info("Starting HTTP server on {}:{}", config.getHost(), config.getPort());
        
        // Start accepting connections
        while (running.get()) {
            try {
                Socket clientSocket = serverSocket.accept();
                clientSocket.setSoTimeout(config.getConnectionTimeout());
                
                // Submit request to thread pool
                threadPool.submit(new HttpRequestHandler(clientSocket, config, webAppManager, requestCount));
                
            } catch (IOException e) {
                if (running.get()) {
                    logger.error("Error accepting client connection: {}", e.getMessage());
                }
            }
        }
    }
    
    public void stop() throws IOException {
        logger.info("Stopping HTTP server...");
        
        running.set(false);
        
        // Close server socket
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
        
        // Shutdown thread pool
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(30, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        // Cleanup web applications
        if (webAppManager != null) {
            webAppManager.destroy();
        }
        
        logger.info("HTTP server stopped");
    }
    
    public boolean isRunning() {
        return running.get();
    }
    
    public long getRequestCount() {
        return requestCount.get();
    }
    
    public ServerConfig getConfig() {
        return config;
    }
}
