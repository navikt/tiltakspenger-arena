package no.nav.tiltakspenger.arena.repository

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import no.nav.tiltakspenger.arena.db.DB_PASSWORD_KEY
import no.nav.tiltakspenger.arena.db.DB_URL
import no.nav.tiltakspenger.arena.db.DB_USERNAME_KEY
import no.nav.tiltakspenger.arena.db.flywayMigrate
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.oracle.OracleContainer

@Testcontainers
class SakRepositoryTest {

    companion object {
        private const val FNR_SOM_IKKE_FINNES = "15"
        private const val FNR_MED_0_SAKER = "1"
        private const val FNR_MED_1_SAK = "3"
        private const val FNR_MED_2_SAKER = "2"

        @Container
        val container: OracleContainer = OracleContainer("gvenzl/oracle-free:23.6-slim-faststart")
            .withStartupTimeoutSeconds(30)
            .withConnectTimeoutSeconds(30)

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

            flywayMigrate()
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

    private val repo = SakRepository()

    @Test
    fun `hent saker for person som ikke er i Arena`() {
        val hentet = repo.hentSakerForFnr(FNR_SOM_IKKE_FINNES)

        hentet shouldNotBe null
        hentet.size shouldBe 0
    }

    @Test
    fun `hent saker for person med 0 saker`() {
        val hentet = repo.hentSakerForFnr(FNR_MED_0_SAKER)

        hentet shouldNotBe null
        hentet.size shouldBe 0
    }

    @Test
    fun `hent saker for person med 1 sak`() {
        val hentet = repo.hentSakerForFnr(FNR_MED_1_SAK)

        hentet shouldNotBe null
        hentet.size shouldBe 1
    }

    @Test
    fun `hent saker for person med 2 saker`() {
        val hentet = repo.hentSakerForFnr(FNR_MED_2_SAKER)

        hentet shouldNotBe null
        hentet.size shouldBe 2
    }
}
