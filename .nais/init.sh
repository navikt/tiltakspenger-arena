SERVICEUSER_TPTS_USERNAME=$(cat /secrets/serviceuser/username) || true
export SERVICEUSER_TPTS_USERNAME
SERVICEUSER_TPTS_PASSWORD=$(cat /secrets/serviceuser/password) || true
export SERVICEUSER_TPTS_PASSWORD
ARENADB_USERNAME=$(cat /secrets/dbcreds/username) || true
export ARENADB_USERNAME
ARENADB_PASSWORD=$(cat /secrets/dbcreds/password) || true
export ARENADB_PASSWORD
ARENADB_URL=$(cat /secrets/dbconfig/jdbc_url) || true
export ARENADB_URL
