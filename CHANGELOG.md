# Changelog

All notable changes to FeatherJet Server will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2025-08-18

### Added
- Initial release of FeatherJet Server
- HTTP/1.1 protocol support
- Multi-threaded request handling
- Static file serving with MIME type detection
- Web application management
- Configuration via properties file or command line
- Built-in metrics and health check endpoints
- Cross-platform startup scripts (Linux/macOS/Windows)
- Directory listing for folders without index files
- Comprehensive error handling and logging
- Maven build system with fat JAR packaging
- Distribution assembly with all necessary files
- Comprehensive documentation and README

### Features
- **Ultra Lightweight**: ~10MB total size
- **High Performance**: Concurrent request handling with thread pool
- **Easy Configuration**: Properties file and command line options
- **Cross Platform**: Runs on any Java 17+ system
- **Developer Friendly**: Hot deployment and comprehensive logging
- **Production Ready**: Health checks, metrics, and monitoring support

### Security
- Path traversal protection
- Input validation for HTTP requests
- Safe file serving with proper error handling

### Documentation
- Complete README with installation and usage instructions
- API documentation for metrics and health endpoints
- Configuration reference
- Troubleshooting guide
- Performance tuning recommendations
