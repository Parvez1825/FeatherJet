#!/bin/bash
set -e

# Define the repository path in a user's home directory
REPO_PATH="/home/ubuntu/featherjet"

# Ensure Go is installed
if ! command -v go &> /dev/null; then
  sudo apt update
  sudo apt install -y golang-go
fi

# Clone the repo into the designated path if it doesn't exist.
# Otherwise, navigate to the folder and pull the latest changes.
if [ ! -d "$REPO_PATH" ]; then
  echo "Cloning the repository into $REPO_PATH..."
  mkdir -p "$REPO_PATH"
  git clone https://github.com/Parvez1825/FeatherJet.git "$REPO_PATH"
else
  echo "Repository already exists. Pulling latest changes..."
  cd "$REPO_PATH"
  git pull origin main
fi

# Navigate into the repository directory to ensure all commands are run from the correct location
cd "$REPO_PATH"

# Build Go application from the FeatherJet source code.
# The executable is named 'featherjet' to be consistent with the application name.
go build -o featherjet ./cmd/featherjet

# Create systemd service if it doesn't exist
SERVICE_FILE="/etc/systemd/system/featherjet.service"
if [ ! -f "$SERVICE_FILE" ]; then
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
# Restart the service to apply the new changes
sudo systemctl restart featherjet


