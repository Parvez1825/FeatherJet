package com.featherjet.server;

import com.featherjet.server.config.ServerConfig;
import com.featherjet.server.core.HttpServer;
import com.featherjet.server.util.Logger;

import java.io.IOException;
import java.util.Arrays;

/**
 * FeatherJet Server - Main entry point
 * A lightweight web server implementation
 */
public class FeatherJetServer {
    
    private static final Logger logger = Logger.getLogger(FeatherJetServer.class);
    private static final String VERSION = "1.0.0";
    private static final String BANNER = """
            
            ███████╗███████╗ █████╗ ████████╗██╗  ██╗███████╗██████╗      ██╗███████╗████████╗
            ██╔════╝██╔════╝██╔══██╗╚══██╔══╝██║  ██║██╔════╝██╔══██╗     ██║██╔════╝╚══██╔══╝
            █████╗  █████╗  ███████║   ██║   ███████║█████╗  ██████╔╝     ██║█████╗     ██║   
            ██╔══╝  ██╔══╝  ██╔══██║   ██║   ██╔══██║██╔══╝  ██╔══██╗██   ██║██╔══╝     ██║   
            ██║     ███████╗██║  ██║   ██║   ██║  ██║███████╗██║  ██║╚█████╔╝███████╗   ██║   
            ╚═╝     ╚══════╝╚═╝  ╚═╝   ╚═╝   ╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝ ╚════╝ ╚══════╝   ╚═╝   
            
                           Light as a feather, fast as a jet! v%s
            
            """;
    
    public static void main(String[] args) {
        try {
            printBanner();
            
            // Parse command line arguments
            ServerConfig config = parseArguments(args);
            
            // Create and start the server
            HttpServer server = new HttpServer(config);
            
            // Add shutdown hook
            addShutdownHook(server);
            
            // Start the server
            server.start();
            
            logger.info("FeatherJet Server started successfully");
            logger.info("Server listening on {}:{}", config.getHost(), config.getPort());
            logger.info("Web applications directory: {}", config.getWebAppsDir());
            
        } catch (Exception e) {
            logger.error("Failed to start FeatherJet Server: {}", e.getMessage());
            System.exit(1);
        }
    }
    
    private static void printBanner() {
        System.out.printf(BANNER, VERSION);
    }
    
    private static ServerConfig parseArguments(String[] args) throws IOException {
        ServerConfig config = ServerConfig.getDefault();
        
        for (String arg : args) {
            if (arg.equals("--help") || arg.equals("-h")) {
                printUsage();
                System.exit(0);
            } else if (arg.equals("--version") || arg.equals("-v")) {
                System.out.println("FeatherJet Server v" + VERSION);
                System.exit(0);
            } else if (arg.startsWith("--port=")) {
                config.setPort(Integer.parseInt(arg.substring(7)));
            } else if (arg.startsWith("--host=")) {
                config.setHost(arg.substring(7));
            } else if (arg.startsWith("--config=")) {
                config = ServerConfig.fromFile(arg.substring(9));
            } else if (arg.startsWith("--webapps=")) {
                config.setWebAppsDir(arg.substring(10));
            } else if (!arg.isEmpty()) {
                System.err.println("Unknown argument: " + arg);
                printUsage();
                System.exit(1);
            }
        }
        
        return config;
    }
    
    private static void printUsage() {
        System.out.println("Usage: java -jar featherjet-server.jar [OPTIONS]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  --port=PORT              Set server port (default: 8080)");
        System.out.println("  --host=HOST              Set bind address (default: 0.0.0.0)");
        System.out.println("  --config=FILE            Use custom configuration file");
        System.out.println("  --webapps=DIR            Set webapps directory");
        System.out.println("  --help, -h               Show this help message");
        System.out.println("  --version, -v            Show version information");
        System.out.println();
        System.out.println("Environment Variables:");
        System.out.println("  FEATHERJET_PORT          Server port");
        System.out.println("  FEATHERJET_HOST          Bind address");
        System.out.println("  FEATHERJET_CONFIG        Configuration file path");
        System.out.println("  FEATHERJET_WEBAPPS       Web applications directory");
    }
    
    private static void addShutdownHook(HttpServer server) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down FeatherJet Server...");
            try {
                server.stop();
                logger.info("FeatherJet Server stopped gracefully");
            } catch (Exception e) {
                logger.error("Error during server shutdown: {}", e.getMessage());
            }
        }));
    }
}
