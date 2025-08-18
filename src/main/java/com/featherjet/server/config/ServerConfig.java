package com.featherjet.server.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Server configuration management
 */
public class ServerConfig {
    
    private int port = 8080;
    private String host = "0.0.0.0";
    private int maxThreads = 200;
    private int minThreads = 10;
    private int connectionTimeout = 20000;
    private int maxConnections = 8192;
    private String webAppsDir = "webapps";
    private String logsDir = "logs";
    private String tempDir = "temp";
    private boolean staticContentEnabled = true;
    private boolean staticContentCache = true;
    private String staticContentCacheSize = "100MB";
    private boolean sslEnabled = false;
    private String sslKeystore = "";
    private String sslKeystorePassword = "";
    private String logLevel = "INFO";
    private String logFile = "logs/featherjet.log";
    private String logMaxFileSize = "10MB";
    private int logMaxFiles = 5;
    
    public static ServerConfig getDefault() {
        ServerConfig config = new ServerConfig();
        
        // Override with environment variables if present
        String envPort = System.getenv("FEATHERJET_PORT");
        if (envPort != null) {
            config.setPort(Integer.parseInt(envPort));
        }
        
        String envHost = System.getenv("FEATHERJET_HOST");
        if (envHost != null) {
            config.setHost(envHost);
        }
        
        String envWebApps = System.getenv("FEATHERJET_WEBAPPS");
        if (envWebApps != null) {
            config.setWebAppsDir(envWebApps);
        }
        
        return config;
    }
    
    public static ServerConfig fromFile(String configFile) throws IOException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(configFile)) {
            props.load(fis);
        }
        
        ServerConfig config = new ServerConfig();
        
        config.setPort(Integer.parseInt(props.getProperty("server.port", "8080")));
        config.setHost(props.getProperty("server.host", "0.0.0.0"));
        config.setMaxThreads(Integer.parseInt(props.getProperty("server.maxThreads", "200")));
        config.setMinThreads(Integer.parseInt(props.getProperty("server.minThreads", "10")));
        config.setConnectionTimeout(Integer.parseInt(props.getProperty("server.connectionTimeout", "20000")));
        config.setMaxConnections(Integer.parseInt(props.getProperty("server.maxConnections", "8192")));
        config.setWebAppsDir(props.getProperty("server.webAppsDir", "webapps"));
        config.setLogsDir(props.getProperty("server.logsDir", "logs"));
        config.setTempDir(props.getProperty("server.tempDir", "temp"));
        config.setStaticContentEnabled(Boolean.parseBoolean(props.getProperty("server.staticContent.enabled", "true")));
        config.setStaticContentCache(Boolean.parseBoolean(props.getProperty("server.staticContent.cache", "true")));
        config.setStaticContentCacheSize(props.getProperty("server.staticContent.cacheSize", "100MB"));
        config.setSslEnabled(Boolean.parseBoolean(props.getProperty("server.ssl.enabled", "false")));
        config.setSslKeystore(props.getProperty("server.ssl.keystore", ""));
        config.setSslKeystorePassword(props.getProperty("server.ssl.keystorePassword", ""));
        config.setLogLevel(props.getProperty("logging.level", "INFO"));
        config.setLogFile(props.getProperty("logging.file", "logs/featherjet.log"));
        config.setLogMaxFileSize(props.getProperty("logging.maxFileSize", "10MB"));
        config.setLogMaxFiles(Integer.parseInt(props.getProperty("logging.maxFiles", "5")));
        
        return config;
    }
    
    // Getters and Setters
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    
    public int getMaxThreads() { return maxThreads; }
    public void setMaxThreads(int maxThreads) { this.maxThreads = maxThreads; }
    
    public int getMinThreads() { return minThreads; }
    public void setMinThreads(int minThreads) { this.minThreads = minThreads; }
    
    public int getConnectionTimeout() { return connectionTimeout; }
    public void setConnectionTimeout(int connectionTimeout) { this.connectionTimeout = connectionTimeout; }
    
    public int getMaxConnections() { return maxConnections; }
    public void setMaxConnections(int maxConnections) { this.maxConnections = maxConnections; }
    
    public String getWebAppsDir() { return webAppsDir; }
    public void setWebAppsDir(String webAppsDir) { this.webAppsDir = webAppsDir; }
    
    public String getLogsDir() { return logsDir; }
    public void setLogsDir(String logsDir) { this.logsDir = logsDir; }
    
    public String getTempDir() { return tempDir; }
    public void setTempDir(String tempDir) { this.tempDir = tempDir; }
    
    public boolean isStaticContentEnabled() { return staticContentEnabled; }
    public void setStaticContentEnabled(boolean staticContentEnabled) { this.staticContentEnabled = staticContentEnabled; }
    
    public boolean isStaticContentCache() { return staticContentCache; }
    public void setStaticContentCache(boolean staticContentCache) { this.staticContentCache = staticContentCache; }
    
    public String getStaticContentCacheSize() { return staticContentCacheSize; }
    public void setStaticContentCacheSize(String staticContentCacheSize) { this.staticContentCacheSize = staticContentCacheSize; }
    
    public boolean isSslEnabled() { return sslEnabled; }
    public void setSslEnabled(boolean sslEnabled) { this.sslEnabled = sslEnabled; }
    
    public String getSslKeystore() { return sslKeystore; }
    public void setSslKeystore(String sslKeystore) { this.sslKeystore = sslKeystore; }
    
    public String getSslKeystorePassword() { return sslKeystorePassword; }
    public void setSslKeystorePassword(String sslKeystorePassword) { this.sslKeystorePassword = sslKeystorePassword; }
    
    public String getLogLevel() { return logLevel; }
    public void setLogLevel(String logLevel) { this.logLevel = logLevel; }
    
    public String getLogFile() { return logFile; }
    public void setLogFile(String logFile) { this.logFile = logFile; }
    
    public String getLogMaxFileSize() { return logMaxFileSize; }
    public void setLogMaxFileSize(String logMaxFileSize) { this.logMaxFileSize = logMaxFileSize; }
    
    public int getLogMaxFiles() { return logMaxFiles; }
    public void setLogMaxFiles(int logMaxFiles) { this.logMaxFiles = logMaxFiles; }
}
