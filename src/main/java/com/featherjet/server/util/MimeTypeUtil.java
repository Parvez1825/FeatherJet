package com.featherjet.server.util;

import java.util.HashMap;
import java.util.Map;

/**
 * MIME type utility for determining content types
 */
public class MimeTypeUtil {
    
    private static final Map<String, String> MIME_TYPES = new HashMap<>();
    
    static {
        // Text files
        MIME_TYPES.put("html", "text/html");
        MIME_TYPES.put("htm", "text/html");
        MIME_TYPES.put("css", "text/css");
        MIME_TYPES.put("js", "application/javascript");
        MIME_TYPES.put("json", "application/json");
        MIME_TYPES.put("xml", "application/xml");
        MIME_TYPES.put("txt", "text/plain");
        
        // Images
        MIME_TYPES.put("png", "image/png");
        MIME_TYPES.put("jpg", "image/jpeg");
        MIME_TYPES.put("jpeg", "image/jpeg");
        MIME_TYPES.put("gif", "image/gif");
        MIME_TYPES.put("bmp", "image/bmp");
        MIME_TYPES.put("ico", "image/x-icon");
        MIME_TYPES.put("svg", "image/svg+xml");
        MIME_TYPES.put("webp", "image/webp");
        
        // Fonts
        MIME_TYPES.put("woff", "font/woff");
        MIME_TYPES.put("woff2", "font/woff2");
        MIME_TYPES.put("ttf", "font/ttf");
        MIME_TYPES.put("eot", "application/vnd.ms-fontobject");
        
        // Documents
        MIME_TYPES.put("pdf", "application/pdf");
        MIME_TYPES.put("doc", "application/msword");
        MIME_TYPES.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        MIME_TYPES.put("xls", "application/vnd.ms-excel");
        MIME_TYPES.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        
        // Archives
        MIME_TYPES.put("zip", "application/zip");
        MIME_TYPES.put("tar", "application/x-tar");
        MIME_TYPES.put("gz", "application/gzip");
        MIME_TYPES.put("rar", "application/x-rar-compressed");
        
        // Media
        MIME_TYPES.put("mp3", "audio/mpeg");
        MIME_TYPES.put("wav", "audio/wav");
        MIME_TYPES.put("mp4", "video/mp4");
        MIME_TYPES.put("avi", "video/x-msvideo");
        MIME_TYPES.put("mov", "video/quicktime");
        
        // Other
        MIME_TYPES.put("jar", "application/java-archive");
        MIME_TYPES.put("war", "application/java-archive");
        MIME_TYPES.put("class", "application/java-vm");
    }
    
    public static String getMimeType(String filename) {
        if (filename == null) {
            return "application/octet-stream";
        }
        
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "application/octet-stream";
        }
        
        String extension = filename.substring(lastDotIndex + 1).toLowerCase();
        return MIME_TYPES.getOrDefault(extension, "application/octet-stream");
    }
    
    public static boolean isTextType(String mimeType) {
        return mimeType != null && (
            mimeType.startsWith("text/") ||
            mimeType.equals("application/javascript") ||
            mimeType.equals("application/json") ||
            mimeType.equals("application/xml")
        );
    }
}
