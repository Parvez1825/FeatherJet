#!/bin/bash
set -e

# Define repository path
REPO_PATH="/home/ubuntu/velocity-tasks"
REPO_URL="https://github.com/Parvez1825/FeatherJet.git"

# Ensure Go is installed
if ! command -v go &> /dev/null; then
  sudo apt update
  sudo apt install -y golang-go git
fi

# Clone or update the repo
if [ ! -d "$REPO_PATH" ]; then
  echo "Cloning repository..."
  git clone "$REPO_URL" "$REPO_PATH"
else
  echo "Repository exists. Pulling latest changes..."
  cd "$REPO_PATH"
  if ! git pull origin main; then
    echo "Git pull failed. Re-cloning..."
    cd ..
    sudo rm -rf "$REPO_PATH"
    git clone "$REPO_URL" "$REPO_PATH"
  fi
fi

cd "$REPO_PATH"

###########################
# Build Velocity Tasks
###########################
echo "Building velocity-tasks..."
go build -o velocity-tasks ./cmd/featherjet

# Velocity Tasks systemd service
VELOCITY_SERVICE="/etc/systemd/system/velocity-tasks.service"
if [ ! -f "$VELOCITY_SERVICE" ]; then
  sudo tee "$VELOCITY_SERVICE" > /dev/null <<SERVICE
[Unit]
Description=Velocity Tasks Service
After=network.target

[Service]
Type=simple
User=ubuntu
WorkingDirectory=$REPO_PATH
ExecStart=$REPO_PATH/velocity-tasks
Restart=on-failure

[Install]
WantedBy=multi-user.target
SERVICE

  sudo systemctl daemon-reload
  sudo systemctl enable velocity-tasks
fi
sudo systemctl restart velocity-tasks

###########################
# Build FeatherJet
###########################
echo "Building featherjet..."
go build -o featherjet ./cmd/featherjet

# FeatherJet systemd service
FEATHERJET_SERVICE="/etc/systemd/system/featherjet.service"
if [ ! -f "$FEATHERJET_SERVICE" ]; then
  sudo tee "$FEATHERJET_SERVICE" > /dev/null <<SERVICE
[Unit]
Description=FeatherJet Web Server
After=network.target

[Service]
Type=simple
User=ubuntu
WorkingDirectory=$REPO_PATH
ExecStart=$REPO_PATH/featherjet
Restart=on-failure

[Install]
WantedBy=multi-user.target
SERVICE

  sudo systemctl daemon-reload
  sudo systemctl enable featherjet
fi
sudo systemctl restart featherjet

echo "Deployment complete: velocity-tasks & featherjet running."