package no.nav.tiltakspenger.arena.db

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tiltakspenger.arena.Configuration
import no.nav.tiltakspenger.arena.Profile
import org.flywaydb.core.Flyway

private val LOG = KotlinLogging.logger {}

const val DB_URL = "ARENADB_URL"
const val DB_USERNAME_KEY = "ARENADB_USERNAME"
const val DB_PASSWORD_KEY = "ARENADB_PASSWORD"

private fun localFlyway() = Flyway
    .configure()
    .loggers("slf4j")
    .encoding("UTF-8")
    .locations("local-migrations")
    .dataSource(Datasource.hikariDataSource)
    .connectRetries(5)
    .cleanDisabled(false)
    .load()

fun flywayMigrate() {
    when (Configuration.applicationProfile()) {
        Profile.LOCAL -> localFlyway().migrate()
        else -> LOG.info { "Skipping flyway when not local" }
    }
}
