package no.nav.tiltakspenger.arena.db

import no.nav.tiltakspenger.arena.repository.ArenaTestdata
import org.testcontainers.oracle.OracleContainer

/**
 * Felles Oracle-testcontainer for repository-testene. Startes én gang per JVM og deles av alle
 * testklassene ([Datasource] er en singleton som leser tilkoblingen fra system-properties ved
 * første bruk). Containeren ryddes opp av testcontainers (Ryuk) når JVM-en avsluttes.
 *
 * Testdata legges inn per test via [ArenaTestdata] - bruk unike id-er/fnr per test, siden
 * databasen deles på tvers av tester og testklasser.
 */
object OracleTestbase {
    private val container: OracleContainer by lazy {
        OracleContainer("gvenzl/oracle-free:23.6-slim-faststart")
            .withStartupTimeoutSeconds(60)
            .withConnectTimeoutSeconds(60)
            .also {
                it.start()
                System.setProperty(DB_URL, it.jdbcUrl)
                System.setProperty(DB_USERNAME_KEY, it.username)
                System.setProperty(DB_PASSWORD_KEY, it.password)
                flywayMigrate()
                ArenaTestdata.seedKodeverk()
            }
    }

    /** Sørger for at containeren er startet, migrert og kodeverk-seedet. Idempotent. */
    fun start() {
        container
    }
}
