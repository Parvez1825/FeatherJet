package server

import (
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"testing"
	"time"

	"github.com/featherjet/featherjet/internal/config"
)

func TestNew(t *testing.T) {
	cfg := &config.Config{}
	cfg.Server.Host = "localhost"
	cfg.Server.Port = 8080
	cfg.Server.ReadTimeout = 30 * time.Second
	cfg.Server.WriteTimeout = 30 * time.Second
	cfg.Server.IdleTimeout = 120 * time.Second
	cfg.Static.Directory = "./public"
	cfg.Static.CacheMaxAge = "3600"
	cfg.Logging.Level = "info"
	cfg.Logging.EnableRequestLogging = true
	cfg.Middleware.EnableCORS = true

	server := New(cfg)
	if server == nil {
		t.Fatal("Expected server to be created, got nil")
	}

	if server.config != cfg {
		t.Error("Expected server config to match provided config")
	}
}

func TestHandleHello(t *testing.T) {
	cfg := &config.Config{}
	cfg.Server.Host = "localhost"
	cfg.Server.Port = 8080
	cfg.Server.ReadTimeout = 30 * time.Second
	cfg.Server.WriteTimeout = 30 * time.Second
	cfg.Server.IdleTimeout = 120 * time.Second
	cfg.Static.Directory = "./public"
	cfg.Logging.Level = "info"
	cfg.Logging.EnableRequestLogging = false // Disable for testing
	cfg.Middleware.EnableCORS = false

	server := New(cfg)

	req, err := http.NewRequest("GET", "/api/hello", nil)
	if err != nil {
		t.Fatal(err)
	}

	rr := httptest.NewRecorder()
	handler := http.HandlerFunc(server.handleHello)
	handler.ServeHTTP(rr, req)

	if status := rr.Code; status != http.StatusOK {
		t.Errorf("Expected status code %d, got %d", http.StatusOK, status)
	}

	expected := "application/json"
	if ct := rr.Header().Get("Content-Type"); ct != expected {
		t.Errorf("Expected content type %s, got %s", expected, ct)
	}

	var response map[string]interface{}
	if err := json.Unmarshal(rr.Body.Bytes(), &response); err != nil {
		t.Errorf("Failed to parse JSON response: %v", err)
	}

	if message, ok := response["message"].(string); !ok || message != "Hello from FeatherJet!" {
		t.Errorf("Expected message 'Hello from FeatherJet!', got %v", response["message"])
	}

	if method, ok := response["method"].(string); !ok || method != "GET" {
		t.Errorf("Expected method 'GET', got %v", response["method"])
	}
}

func TestHandleStatus(t *testing.T) {
	cfg := &config.Config{}
	cfg.Server.Host = "localhost"
	cfg.Server.Port = 8080
	cfg.Server.ReadTimeout = 30 * time.Second
	cfg.Server.WriteTimeout = 30 * time.Second
	cfg.Server.IdleTimeout = 120 * time.Second
	cfg.Static.Directory = "./public"
	cfg.Logging.Level = "info"
	cfg.Logging.EnableRequestLogging = false
	cfg.Middleware.EnableCORS = false

	server := New(cfg)

	req, err := http.NewRequest("GET", "/api/status", nil)
	if err != nil {
		t.Fatal(err)
	}

	rr := httptest.NewRecorder()
	handler := http.HandlerFunc(server.handleStatus)
	handler.ServeHTTP(rr, req)

	if status := rr.Code; status != http.StatusOK {
		t.Errorf("Expected status code %d, got %d", http.StatusOK, status)
	}

	var response map[string]interface{}
	if err := json.Unmarshal(rr.Body.Bytes(), &response); err != nil {
		t.Errorf("Failed to parse JSON response: %v", err)
	}

	if status, ok := response["status"].(string); !ok || status != "healthy" {
		t.Errorf("Expected status 'healthy', got %v", response["status"])
	}

	if server, ok := response["server"].(string); !ok || server != "FeatherJet" {
		t.Errorf("Expected server 'FeatherJet', got %v", response["server"])
	}
}

func TestHandleInfo(t *testing.T) {
	cfg := &config.Config{}
	cfg.Server.Host = "localhost"
	cfg.Server.Port = 8080
	cfg.Server.ReadTimeout = 30 * time.Second
	cfg.Server.WriteTimeout = 30 * time.Second
	cfg.Server.IdleTimeout = 120 * time.Second
	cfg.Static.Directory = "./public"
	cfg.Logging.Level = "info"
	cfg.Logging.EnableRequestLogging = false
	cfg.Middleware.EnableCORS = true

	server := New(cfg)

	req, err := http.NewRequest("GET", "/api/info", nil)
	if err != nil {
		t.Fatal(err)
	}

	rr := httptest.NewRecorder()
	handler := http.HandlerFunc(server.handleInfo)
	handler.ServeHTTP(rr, req)

	if status := rr.Code; status != http.StatusOK {
		t.Errorf("Expected status code %d, got %d", http.StatusOK, status)
	}

	var response map[string]interface{}
	if err := json.Unmarshal(rr.Body.Bytes(), &response); err != nil {
		t.Errorf("Failed to parse JSON response: %v", err)
	}

	// Check server info
	serverInfo, ok := response["server"].(map[string]interface{})
	if !ok {
		t.Error("Expected server info object")
	} else {
		if name, ok := serverInfo["name"].(string); !ok || name != "FeatherJet" {
			t.Errorf("Expected server name 'FeatherJet', got %v", serverInfo["name"])
		}
		if port, ok := serverInfo["port"].(float64); !ok || int(port) != 8080 {
			t.Errorf("Expected port 8080, got %v", serverInfo["port"])
		}
	}

	// Check middleware info
	middlewareInfo, ok := response["middleware"].(map[string]interface{})
	if !ok {
		t.Error("Expected middleware info object")
	} else {
		if cors, ok := middlewareInfo["cors_enabled"].(bool); !ok || !cors {
			t.Errorf("Expected CORS enabled to be true, got %v", middlewareInfo["cors_enabled"])
		}
	}
}
