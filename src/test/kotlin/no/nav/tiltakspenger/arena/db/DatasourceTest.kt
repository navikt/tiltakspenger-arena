package no.nav.tiltakspenger.arena.db

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.oracle.OracleContainer

@Testcontainers
@Disabled("SakRepositoryTest kjører implisitt denne testen også, og jeg har ikke klart å få begge til å fungere samtidig")
internal class DatasourceTest {
    companion object {
        @Container
        val container: OracleContainer = OracleContainer("gvenzl/oracle-free:23.6-slim-faststart")

        @BeforeAll
        @JvmStatic
        fun setup() {
            container.start()
            container.let {
                println(it.jdbcUrl)
                System.setProperty(DB_URL, it.jdbcUrl)
                System.setProperty(DB_USERNAME_KEY, it.username)
                System.setProperty(DB_PASSWORD_KEY, it.password)
            }
        }

        @AfterAll
        @JvmStatic
        fun teardown() {
            container.let {
                println(it.jdbcUrl)
                System.clearProperty(DB_URL)
                System.clearProperty(DB_USERNAME_KEY)
                System.clearProperty(DB_PASSWORD_KEY)
            }
            container.stop()
        }
    }

    @Test
    fun `flyway migrations skal kjøre uten feil`() {
        assertTrue(container.isCreated)
        assertTrue(container.isRunning)

        flywayMigrate()
    }
}
