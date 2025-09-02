package config

import (
	"os"
	"testing"
	"time"
)

func TestLoadDefaultConfig(t *testing.T) {
	// Test loading config when file doesn't exist
	cfg, err := Load("nonexistent.yaml")
	if err != nil {
		t.Fatalf("Expected no error for missing config file, got %v", err)
	}

	// Check default values
	if cfg.Server.Host != "localhost" {
		t.Errorf("Expected default host 'localhost', got %s", cfg.Server.Host)
	}

	if cfg.Server.Port != 8080 {
		t.Errorf("Expected default port 8080, got %d", cfg.Server.Port)
	}

	if cfg.Static.Directory != "./public" {
		t.Errorf("Expected default static directory './public', got %s", cfg.Static.Directory)
	}

	if cfg.Logging.Level != "info" {
		t.Errorf("Expected default log level 'info', got %s", cfg.Logging.Level)
	}
}

func TestLoadValidConfig(t *testing.T) {
	// Create a temporary config file
	configContent := `
server:
  host: "0.0.0.0"
  port: 9090
  read_timeout: "60s"
  write_timeout: "60s"
  idle_timeout: "240s"

static:
  directory: "./test-public"
  cache_max_age: "7200"

logging:
  level: "debug"
  enable_request_logging: false

middleware:
  enable_cors: false
  enable_compression: true
`

	tempFile, err := os.CreateTemp("", "test-config-*.yaml")
	if err != nil {
		t.Fatalf("Failed to create temp file: %v", err)
	}
	defer os.Remove(tempFile.Name())

	if _, err := tempFile.WriteString(configContent); err != nil {
		t.Fatalf("Failed to write config content: %v", err)
	}
	tempFile.Close()

	// Load the config
	cfg, err := Load(tempFile.Name())
	if err != nil {
		t.Fatalf("Expected no error loading config, got %v", err)
	}

	// Verify loaded values
	if cfg.Server.Host != "0.0.0.0" {
		t.Errorf("Expected host '0.0.0.0', got %s", cfg.Server.Host)
	}

	if cfg.Server.Port != 9090 {
		t.Errorf("Expected port 9090, got %d", cfg.Server.Port)
	}

	if cfg.Server.ReadTimeout != 60*time.Second {
		t.Errorf("Expected read timeout 60s, got %v", cfg.Server.ReadTimeout)
	}

	if cfg.Static.Directory != "./test-public" {
		t.Errorf("Expected static directory './test-public', got %s", cfg.Static.Directory)
	}

	if cfg.Static.CacheMaxAge != "7200" {
		t.Errorf("Expected cache max age '7200', got %s", cfg.Static.CacheMaxAge)
	}

	if cfg.Logging.Level != "debug" {
		t.Errorf("Expected log level 'debug', got %s", cfg.Logging.Level)
	}

	if cfg.Logging.EnableRequestLogging {
		t.Error("Expected request logging to be disabled")
	}

	if cfg.Middleware.EnableCORS {
		t.Error("Expected CORS to be disabled")
	}

	if !cfg.Middleware.EnableCompression {
		t.Error("Expected compression to be enabled")
	}
}

func TestValidate(t *testing.T) {
	cfg := &Config{}
	cfg.Server.Host = "localhost"
	cfg.Server.Port = 8080
	cfg.Static.Directory = "./public"
	cfg.Logging.Level = "info"

	if err := cfg.Validate(); err != nil {
		t.Errorf("Expected valid config to pass validation, got %v", err)
	}

	// Test invalid port
	cfg.Server.Port = 0
	if err := cfg.Validate(); err == nil {
		t.Error("Expected validation to fail for port 0")
	}

	cfg.Server.Port = 70000
	if err := cfg.Validate(); err == nil {
		t.Error("Expected validation to fail for port > 65535")
	}

	// Reset port and test empty static directory
	cfg.Server.Port = 8080
	cfg.Static.Directory = ""
	if err := cfg.Validate(); err == nil {
		t.Error("Expected validation to fail for empty static directory")
	}

	// Reset directory and test invalid log level
	cfg.Static.Directory = "./public"
	cfg.Logging.Level = "invalid"
	if err := cfg.Validate(); err == nil {
		t.Error("Expected validation to fail for invalid log level")
	}
}
