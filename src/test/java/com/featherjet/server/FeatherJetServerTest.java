package com.featherjet.server;

import com.featherjet.server.config.ServerConfig;
import com.featherjet.server.util.MimeTypeUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic tests for FeatherJet Server components
 */
public class FeatherJetServerTest {
    
    @Test
    public void testServerConfigDefaults() {
        ServerConfig config = ServerConfig.getDefault();
        
        assertEquals(8080, config.getPort());
        assertEquals("0.0.0.0", config.getHost());
        assertEquals(200, config.getMaxThreads());
        assertEquals(10, config.getMinThreads());
        assertEquals("webapps", config.getWebAppsDir());
    }
    
    @Test
    public void testMimeTypeUtil() {
        assertEquals("text/html", MimeTypeUtil.getMimeType("index.html"));
        assertEquals("text/css", MimeTypeUtil.getMimeType("style.css"));
        assertEquals("application/javascript", MimeTypeUtil.getMimeType("script.js"));
        assertEquals("image/png", MimeTypeUtil.getMimeType("image.png"));
        assertEquals("application/json", MimeTypeUtil.getMimeType("data.json"));
        assertEquals("application/octet-stream", MimeTypeUtil.getMimeType("unknown.xyz"));
        assertEquals("application/octet-stream", MimeTypeUtil.getMimeType("noextension"));
        assertEquals("application/octet-stream", MimeTypeUtil.getMimeType(null));
    }
    
    @Test
    public void testMimeTypeTextDetection() {
        assertTrue(MimeTypeUtil.isTextType("text/html"));
        assertTrue(MimeTypeUtil.isTextType("text/plain"));
        assertTrue(MimeTypeUtil.isTextType("application/javascript"));
        assertTrue(MimeTypeUtil.isTextType("application/json"));
        assertTrue(MimeTypeUtil.isTextType("application/xml"));
        
        assertFalse(MimeTypeUtil.isTextType("image/png"));
        assertFalse(MimeTypeUtil.isTextType("application/octet-stream"));
        assertFalse(MimeTypeUtil.isTextType(null));
    }
}
