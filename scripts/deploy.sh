#!/bin/bash
set -e

# Define the repository path
REPO_PATH="/home/ubuntu/featherjet"

# Ensure Go is installed
if ! command -v go &> /dev/null; then
  sudo apt update
  sudo apt install -y golang-go
fi

# If repo already exists, delete it completely
if [ -d "$REPO_PATH" ]; then
  echo "Removing existing repository at $REPO_PATH..."
  rm -rf "$REPO_PATH"
fi

# Clone a fresh copy
echo "Cloning fresh repository into $REPO_PATH..."
git clone https://github.com/Parvez1825/FeatherJet.git "$REPO_PATH"

# Navigate into the repo
cd "$REPO_PATH"

# Build Go application
echo "Building FeatherJet..."
go build -o featherjet ./cmd/featherjet

# Create systemd service if not already present
SERVICE_FILE="/etc/systemd/system/featherjet.service"
if [ ! -f "$SERVICE_FILE" ]; then
  echo "Creating systemd service file..."
  sudo tee $SERVICE_FILE > /dev/null <<'SERVICE'
[Unit]
Description=FeatherJet Web Server
After=network.target

[Service]
Type=simple
User=ubuntu
WorkingDirectory=/home/ubuntu/featherjet
ExecStart=/home/ubuntu/featherjet/featherjet
Restart=on-failure

[Install]
WantedBy=multi-user.target
SERVICE

  sudo systemctl daemon-reload
  sudo systemctl enable featherjet
fi

# Restart service with new build
echo "Restarting FeatherJet service..."
sudo systemctl daemon-reload
sudo systemctl restart featherjet

# Preview deployed index.html
echo "Deployed index.html preview:"
head -n 15 "$REPO_PATH/public/index.html"
