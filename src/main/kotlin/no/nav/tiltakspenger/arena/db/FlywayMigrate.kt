package no.nav.tiltakspenger.arena.db

import mu.KotlinLogging
import no.nav.tiltakspenger.arena.Configuration
import no.nav.tiltakspenger.arena.Profile
import org.flywaydb.core.Flyway

private val LOG = KotlinLogging.logger {}

private fun localFlyway() = Flyway
    .configure()
    .encoding("UTF-8")
    .locations("db/local-migrations")
    .dataSource(Datasource.hikariDataSource)
    .connectRetries(5)
    .cleanDisabled(false)
    .cleanOnValidationError(true)
    .load()

fun flywayMigrate() {
    when (Configuration.applicationProfile()) {
        Profile.LOCAL -> localFlyway().migrate()
        else -> LOG.info { "Skipping flyway when not local" }
    }
}

fun flywayCleanAndMigrate() {
    when (Configuration.applicationProfile()) {
        Profile.LOCAL -> localFlyway().let { it.clean(); it.migrate() }
        else -> LOG.info { "Skipping flyway when not local" }
    }
}
