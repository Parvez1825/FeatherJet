#!/bin/bash
set -e

# Define the repository path in a user's home directory
REPO_PATH="/home/ubuntu/velocity-tasks"

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
  # Attempt to pull, and if it fails, remove the directory and re-clone.
  if ! git pull origin main; then
    echo "Git pull failed. Repository may be corrupted. Removing and re-cloning..."
    cd ..
    sudo rm -rf "$REPO_PATH"
    git clone https://github.com/Parvez1825/FeatherJet.git "$REPO_PATH"
  fi
fi

# The rest of the script needs to be able to access the repo.
# It is a good practice to use 'cd' to the repo path.
cd "$REPO_PATH"

# Build Go application
go build -o velocity-tasks ./cmd/featherjet

# Create systemd service if it doesn't exist
SERVICE_FILE="/etc/systemd/system/velocity-tasks.service"
if [ ! -f "$SERVICE_FILE" ]; then
  sudo tee $SERVICE_FILE > /dev/null <<'SERVICE'
[Unit]
Description=Velocity Tasks Service
After=network.target

[Service]
Type=simple
User=ubuntu
WorkingDirectory=/home/ubuntu/velocity-tasks
ExecStart=/home/ubuntu/velocity-tasks/velocity-tasks
Restart=on-failure

[Install]
WantedBy=multi-user.target
SERVICE

  sudo systemctl daemon-reload
  sudo systemctl enable velocity-tasks
fi

# Restart the service
sudo systemctl restart velocity-tasks
ore-iehr-qeh