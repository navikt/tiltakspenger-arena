package no.nav.tiltakspenger.arena.repository

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import no.nav.tiltakspenger.arena.db.Datasource
import no.nav.tiltakspenger.arena.db.flywayMigrate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.OracleContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class SakRepositoryTest {

    private val ID_SOM_SKAL_HENTES = "sak1"

    companion object {
        @Container
        val testContainer: OracleContainer = OracleContainer("gvenzl/oracle-xe:21-slim-faststart")
    }

    @BeforeEach
    fun setup() {
        testContainer.let {
            System.setProperty(Datasource.DB_URL, it.jdbcUrl)
            System.setProperty(Datasource.DB_USERNAME_KEY, it.username)
            System.setProperty(Datasource.DB_PASSWORD_KEY, it.password)
        }

        flywayMigrate()
    }

    private val repo = SakRepository()

    @Test
    fun `lagre og hent`() {
        val hentet = repo.hent(ID_SOM_SKAL_HENTES)

        hentet shouldNotBe null
        hentet shouldBe ID_SOM_SKAL_HENTES
    }
}
