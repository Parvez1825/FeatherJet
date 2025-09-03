package config

import (
	"fmt"
	"os"
	"time"

	"gopkg.in/yaml.v3"
)

// Config represents the application configuration
type Config struct {
	Server struct {
		Host         string        `yaml:"host"`
		Port         int           `yaml:"port"`
		ReadTimeout  time.Duration `yaml:"read_timeout"`
		WriteTimeout time.Duration `yaml:"write_timeout"`
		IdleTimeout  time.Duration `yaml:"idle_timeout"`
	} `yaml:"server"`

	Static struct {
		Directory   string `yaml:"directory"`
		CacheMaxAge string `yaml:"cache_max_age"`
	} `yaml:"static"`

	Logging struct {
		Level                string `yaml:"level"`
		EnableRequestLogging bool   `yaml:"enable_request_logging"`
	} `yaml:"logging"`

	Middleware struct {
		EnableCORS        bool `yaml:"enable_cors"`
		EnableCompression bool `yaml:"enable_compression"`
	} `yaml:"middleware"`
}

// Load reads and parses the configuration file
func Load(configPath string) (*Config, error) {
	// Set default values
	cfg := &Config{}
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
	cfg.Middleware.EnableCompression = false

	// Check if config file exists
	if _, err := os.Stat(configPath); os.IsNotExist(err) {
		fmt.Printf("Config file %s not found, using default configuration\n", configPath)
		return cfg, nil
	}

	// Read config file
	data, err := os.ReadFile(configPath)
	if err != nil {
		return nil, fmt.Errorf("failed to read config file: %w", err)
	}

	// Parse YAML
	if err := yaml.Unmarshal(data, cfg); err != nil {
		return nil, fmt.Errorf("failed to parse config file: %w", err)
	}

	return cfg, nil
}

// Validate checks if the configuration is valid
func (c *Config) Validate() error {
	if c.Server.Port < 1 || c.Server.Port > 65535 {
		return fmt.Errorf("invalid port number: %d", c.Server.Port)
	}

	if c.Static.Directory == "" {
		return fmt.Errorf("static directory cannot be empty")
	}

	validLogLevels := map[string]bool{
		"debug": true,
		"info":  true,
		"warn":  true,
		"error": true,
	}

	if !validLogLevels[c.Logging.Level] {
		return fmt.Errorf("invalid log level: %s", c.Logging.Level)
	}

	return nil
}
