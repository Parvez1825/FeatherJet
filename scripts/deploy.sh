#!/bin/bash
set -euo pipefail

REPO_PATH="/home/ubuntu/featherjet"
SERVICE_FILE="/etc/systemd/system/featherjet.service"

# Ensure Go is installed
if ! command -v go &> /dev/null; then
  echo "Installing Go..."
  sudo DEBIAN_FRONTEND=noninteractive apt-get update -y
  sudo DEBIAN_FRONTEND=noninteractive apt-get install -y golang-go
fi

# Clone or update repo
if [ ! -d "$REPO_PATH/.git" ]; then
  echo "Cloning FeatherJet into $REPO_PATH..."
  mkdir -p "$REPO_PATH"
  git clone https://github.com/Parvez1825/FeatherJet.git "$REPO_PATH"
else
  echo "Updating existing repo..."
  cd "$REPO_PATH"
  git fetch origin main
  git reset --hard origin/main
fi

cd "$REPO_PATH"

# Build binary
echo "Building FeatherJet..."
go build -o featherjet ./cmd/featherjet

# Always ensure systemd service is present (create or overwrite)
echo "Ensuring systemd service exists..."
sudo tee "$SERVICE_FILE" > /dev/null <<'SERVICE'
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

# Restart service
echo "Restarting FeatherJet service..."
sudo systemctl restart featherjet
sudo systemctl status featherjet --no-pager -n 20
