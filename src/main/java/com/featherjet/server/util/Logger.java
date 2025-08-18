package com.featherjet.server.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple logging utility
 */
public class Logger {
    
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    private final String name;
    private static LogLevel globalLogLevel = LogLevel.INFO;
    
    public enum LogLevel {
        DEBUG(0), INFO(1), WARN(2), ERROR(3);
        
        private final int level;
        LogLevel(int level) { this.level = level; }
        public int getLevel() { return level; }
    }
    
    private Logger(String name) {
        this.name = name;
    }
    
    public static Logger getLogger(Class<?> clazz) {
        return new Logger(clazz.getSimpleName());
    }
    
    public static Logger getLogger(String name) {
        return new Logger(name);
    }
    
    public static void setGlobalLogLevel(LogLevel level) {
        globalLogLevel = level;
    }
    
    public void debug(String message, Object... args) {
        log(LogLevel.DEBUG, message, args);
    }
    
    public void info(String message, Object... args) {
        log(LogLevel.INFO, message, args);
    }
    
    public void warn(String message, Object... args) {
        log(LogLevel.WARN, message, args);
    }
    
    public void error(String message, Object... args) {
        log(LogLevel.ERROR, message, args);
    }
    
    private void log(LogLevel level, String message, Object... args) {
        if (level.getLevel() < globalLogLevel.getLevel()) {
            return;
        }
        
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String formattedMessage = args.length > 0 ? String.format(message.replace("{}", "%s"), args) : message;
        String logLine = String.format("[%s] %s [%s] %s", 
            timestamp, level.name(), name, formattedMessage);
        
        System.out.println(logLine);
    }
}
