SERVICEUSER_TPTS_USERNAME=$(cat /secrets/serviceuser/username) || true
export SERVICEUSER_TPTS_USERNAME
SERVICEUSER_TPTS_PASSWORD=$(cat /secrets/serviceuser/password) || true
export SERVICEUSER_TPTS_PASSWORD