SERVICEUSER_USERNAME=$(cat /secrets/serviceuser/username) || true
export SERVICEUSER_USERNAME
SERVICEUSER_PASSWORD=$(cat /secrets/serviceuser/password) || true
export SERVICEUSER_PASSWORD