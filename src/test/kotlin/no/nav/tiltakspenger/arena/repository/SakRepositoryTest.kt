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

    private val FNR_SOM_IKKE_FINNES = "15"
    private val FNR_MED_0_SAKER = "1"
    private val FNR_MED_1_SAK = "3"
    private val FNR_MED_2_SAKER = "2"

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
    fun `hent saker for person som ikke er i Arena`() {
        val hentet = repo.hentSakerForFnr(FNR_SOM_IKKE_FINNES)

        hentet shouldBe null
    }

    @Test
    fun `hent saker for person med 0 saker`() {
        val hentet = repo.hentSakerForFnr(FNR_MED_0_SAKER)

        hentet shouldNotBe null
        hentet shouldBe "0"
    }

    @Test
    fun `hent saker for person med 1 sak`() {
        val hentet = repo.hentSakerForFnr(FNR_MED_1_SAK)

        hentet shouldNotBe null
        hentet shouldBe "1"
    }

    @Test
    fun `hent saker for person med 2 saker`() {
        val hentet = repo.hentSakerForFnr(FNR_MED_2_SAKER)

        hentet shouldNotBe null
        hentet shouldBe "2"
    }
}
