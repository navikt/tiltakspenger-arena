package no.nav.tiltakspenger.arena.db

import no.nav.tiltakspenger.arena.db.Datasource.DB_PASSWORD_KEY
import no.nav.tiltakspenger.arena.db.Datasource.DB_URL
import no.nav.tiltakspenger.arena.db.Datasource.DB_USERNAME_KEY
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.testcontainers.containers.OracleContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
internal class DatasourceTest {
    companion object {
        @Container
        val container = OracleContainer("gvenzl/oracle-xe:18.4.0-slim-faststart")
        // .withDatabaseName("testDB")
        // .withUsername("testUser")
        // .withPassword("testPassword")
    }

    @Test
    fun `flyway migrations skal kjøre uten feil`() {
        assertTrue(container.isCreated)
        assertTrue(container.isRunning)

        // assertTrue(container.isHealthy)
        // assertTrue(container.isHostAccessible)
        // assertTrue(container.)

        container.let {
            System.setProperty(DB_URL, it.jdbcUrl)
            System.setProperty(DB_USERNAME_KEY, it.username)
            System.setProperty(DB_PASSWORD_KEY, it.password)
        }

        flywayMigrate()
    }
}
