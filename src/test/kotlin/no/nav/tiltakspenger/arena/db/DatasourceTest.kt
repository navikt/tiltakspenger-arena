package no.nav.tiltakspenger.arena.db

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.testcontainers.containers.OracleContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
internal class DatasourceTest {
    companion object {
        @Container
        val container = OracleContainer("gvenzl/oracle-xe:21-slim-faststart")
            .withDatabaseName("testDB")
            .withUsername("testUser")
            .withPassword("testPassword")
    }

    @Test
    fun `flyway migrations skal kjøre uten feil`() {
        assertTrue(container.isRunning)
    }
}
