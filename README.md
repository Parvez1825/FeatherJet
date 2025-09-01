# FeatherJet - Lightweight Web Server

[![Java](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Size](https://img.shields.io/badge/Size-~10MB-green.svg)]()

FeatherJet is a lightweight, high-performance web server designed to be a minimal alternative to Apache Tomcat. At approximately 10MB, it provides essential web server functionality with a small footprint, making it perfect for microservices, development environments, and resource-constrained deployments.

## 🚀 Features

- **Ultra Lightweight**: ~10MB total size
- **HTTP/1.1 Support**: Full HTTP protocol implementation
- **Static File Serving**: Efficient static content delivery
- **Servlet Support**: Basic servlet container functionality
- **Multi-threaded**: Concurrent request handling
- **Configuration-driven**: Easy setup and customization
- **Cross-platform**: Runs on any Java-enabled system
- **Hot Deployment**: Deploy applications without restart
- **Minimal Dependencies**: Self-contained with minimal external libraries

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+ (for building from source)
- Linux/Windows/macOS operating system

## 🔧 Installation

### Option 1: Download Pre-built Release

1. Download the latest release from [GitHub Releases](https://github.com/Rishabh-Tamrakar/FeatherJet/releases)
2. Extract the archive:
   ```bash
   tar -xzf featherjet-1.0.0.tar.gz
   cd featherjet-1.0.0
   ```

### Option 2: Build from Source

1. Clone the repository:
   ```bash
   git clone https://github.com/Rishabh-Tamrakar/FeatherJet.git
   cd FeatherJet
   ```

2. Build the project:
   ```bash
   mvn clean package
   ```

3. The built server will be in `target/featherjet-1.0.0-dist/`

## 🚀 Quick Start

### Starting the Server

```bash
# Navigate to installation directory
cd featherjet-1.0.0

# Start the server (default port 8080)
java -jar featherjet-server.jar

# Start with custom port
java -jar featherjet-server.jar --port=9090

# Start with custom configuration
java -jar featherjet-server.jar --config=conf/server.properties
```

### Accessing the Server

Once started, open your browser and navigate to:
- `http://localhost:8080` (default)
- Your custom port if specified

## 📁 Directory Structure

```
featherjet-1.0.0/
├── featherjet-server.jar     # Main server executable
├── conf/
│   ├── server.properties     # Server configuration
│   └── logging.properties    # Logging configuration
├── webapps/
│   └── ROOT/                 # Default web application
│       ├── index.html        # Default welcome page
│       └── WEB-INF/          # Web application metadata
├── logs/                     # Server logs
├── temp/                     # Temporary files
├── lib/                      # Additional libraries (if any)
└── bin/
    ├── startup.sh            # Linux/Mac startup script
    └── startup.bat           # Windows startup script
```

## ⚙️ Configuration

### Server Configuration (`conf/server.properties`)

```properties
# Server Settings
server.port=8080
server.host=0.0.0.0
server.maxThreads=200
server.minThreads=10

# Connection Settings
server.connectionTimeout=20000
server.maxConnections=8192

# Static Content
server.staticContent.enabled=true
server.staticContent.cache=true
server.staticContent.cacheSize=100MB

# Security
server.ssl.enabled=false
server.ssl.keystore=
server.ssl.keystorePassword=

# Logging
logging.level=INFO
logging.file=logs/featherjet.log
logging.maxFileSize=10MB
logging.maxFiles=5
```

## 🌐 Deploying Applications

### Static Content

1. Place your static files (HTML, CSS, JS, images) in `webapps/ROOT/`
2. Files are immediately accessible via HTTP

### Web Applications

1. Create a new directory in `webapps/` (e.g., `webapps/myapp/`)
2. Place your application files in the directory
3. Access via `http://localhost:8080/myapp/`

### Servlet Applications

1. Create a `WEB-INF/` directory in your application folder
2. Add `web.xml` configuration:
   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <web-app xmlns="http://java.sun.com/xml/ns/javaee" version="3.0">
       <servlet>
           <servlet-name>MyServlet</servlet-name>
           <servlet-class>com.example.MyServlet</servlet-class>
       </servlet>
       <servlet-mapping>
           <servlet-name>MyServlet</servlet-name>
           <url-pattern>/api/*</url-pattern>
       </servlet-mapping>
   </web-app>
   ```
3. Place compiled classes in `WEB-INF/classes/`
4. Place JAR dependencies in `WEB-INF/lib/`

## 🔧 Advanced Usage

### Command Line Options

```bash
java -jar featherjet-server.jar [OPTIONS]

Options:
  --port=PORT              Set server port (default: 8080)
  --host=HOST              Set bind address (default: 0.0.0.0)
  --config=FILE            Use custom configuration file
  --webapps=DIR            Set webapps directory
  --help                   Show help message
  --version                Show version information
```

### Environment Variables

```bash
export FEATHERJET_PORT=8080
export FEATHERJET_HOST=localhost
export FEATHERJET_CONFIG=/path/to/config.properties
export FEATHERJET_WEBAPPS=/path/to/webapps
export FEATHERJET_LOGS=/path/to/logs
```

### SystemD Service (Linux)

Create `/etc/systemd/system/featherjet.service`:

```ini
[Unit]
Description=FeatherJet Web Server
After=network.target

[Service]
Type=simple
User=featherjet
WorkingDirectory=/opt/featherjet
ExecStart=/usr/bin/java -jar featherjet-server.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

Enable and start:
```bash
sudo systemctl enable featherjet
sudo systemctl start featherjet
```

## 📊 Performance Tuning

### JVM Options

```bash
# Memory settings
java -Xms512m -Xmx1g -jar featherjet-server.jar

# Garbage Collection (G1GC recommended)
java -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -jar featherjet-server.jar

# Performance monitoring
java -XX:+PrintGC -XX:+PrintGCDetails -jar featherjet-server.jar
```

### Thread Pool Tuning

Adjust in `server.properties`:
```properties
server.maxThreads=400      # High traffic
server.minThreads=20       # Baseline threads
server.queueSize=1000      # Request queue size
```

## 🔍 Monitoring & Logging

### Log Files

- `logs/featherjet.log` - Main server log
- `logs/access.log` - HTTP access log
- `logs/error.log` - Error log

### Metrics Endpoint

Access server metrics at: `http://localhost:8080/_metrics`

```json
{
  "uptime": "2h 30m 15s",
  "requests": {
    "total": 1523,
    "rate": "12.5/sec"
  },
  "threads": {
    "active": 15,
    "peak": 23,
    "total": 200
  },
  "memory": {
    "used": "245MB",
    "free": "755MB",
    "total": "1000MB"
  }
}
```

## 🐛 Troubleshooting

### Common Issues

**Port Already in Use**
```bash
# Check what's using the port
netstat -tlnp | grep :8080

# Kill the process
kill -9 <PID>
```

**Permission Denied (Linux)**
```bash
# If port < 1024, run as root or use authbind
sudo java -jar featherjet-server.jar --port=80

# Or use authbind
authbind --deep java -jar featherjet-server.jar --port=80
```

**Out of Memory**
```bash
# Increase heap size
java -Xmx2g -jar featherjet-server.jar
```

### Debug Mode

```bash
# Enable debug logging
java -Dlogging.level=DEBUG -jar featherjet-server.jar

# Enable JVM debugging
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar featherjet-server.jar
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments
- Special thanks to Sakshi (https://github.com/SakshiP3103) for contributions to this project.

## 📞 Support

- 📧 Email: rishabh.tamrakar@protonmail.com
- 🐛 Issues: [GitHub Issues](https://github.com/Rishabh-Tamrakar/FeatherJet/issues)
- 💬 Discussions: [GitHub Discussions](https://github.com/Rishabh-Tamrakar/FeatherJet/discussions)

---

**FeatherJet** - *Light as a feather, fast as a jet!* ✈️
