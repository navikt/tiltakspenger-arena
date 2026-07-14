package no.nav.tiltakspenger.arena.repository.meldekort

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import no.nav.tiltakspenger.arena.db.OracleTestbase
import no.nav.tiltakspenger.arena.repository.ArenaTestdata
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.time.LocalDate

/**
 * Kjører mot delt Oracle-testcontainer, se [OracleTestbase]. Hver test eier sine egne data og
 * bruker unike id-er/fnr (2xx-serien).
 */
class MeldekortRepositoryTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            OracleTestbase.start()
        }
    }

    private val repo = MeldekortRepository()

    @Test
    fun `person som ikke finnes i Arena gir tom liste`() {
        repo.hentMeldekortForFnr("200", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31)) shouldBe emptyList()
    }

    @Test
    fun `henter meldekort med dager innenfor perioden`() {
        ArenaTestdata.leggTilPerson(personId = 201, fnr = "201")
        ArenaTestdata.leggTilMeldekortperiode(
            aar = 2023,
            periodekode = "51",
            datoFra = LocalDate.of(2023, 1, 2),
            datoTil = LocalDate.of(2023, 1, 15),
            ukenrUke1 = 1,
            ukenrUke2 = 2,
        )
        ArenaTestdata.leggTilMeldekort(meldekortId = 2011, personId = 201, aar = 2023, periodekode = "51")
        ArenaTestdata.leggTilMeldelogg(meldekortId = 2011, hendelsetypekode = "FERDI", hendelsedato = LocalDate.of(2023, 1, 17))
        ArenaTestdata.leggTilMeldekortdag(meldekortId = 2011, ukenr = 1, dagnr = 1, statusKurs = "J")
        ArenaTestdata.leggTilMeldekortdag(meldekortId = 2011, ukenr = 1, dagnr = 2, statusKurs = "N", statusSyk = "J")

        val meldekort = repo.hentMeldekortForFnr("201", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31))

        val kort = meldekort.single()
        kort.meldekortId shouldBe "2011"
        kort.beregningstatusnavn shouldBe "Ferdig beregnet"
        kort.meldekortType shouldBe "Elektronisk meldekort"
        kort.meldegruppenavn shouldBe "Individstønad"
        kort.hendelsedato shouldBe LocalDate.of(2023, 1, 17)
        kort.meldekortperiode.datoFra shouldBe LocalDate.of(2023, 1, 2)
        kort.meldekortperiode.datoTil shouldBe LocalDate.of(2023, 1, 15)
        kort.dager.size shouldBe 2
    }

    @Test
    fun `meldekort utenfor etterspurt periode filtreres bort`() {
        ArenaTestdata.leggTilPerson(personId = 202, fnr = "202")
        ArenaTestdata.leggTilMeldekortperiode(
            aar = 2023,
            periodekode = "52",
            datoFra = LocalDate.of(2023, 1, 16),
            datoTil = LocalDate.of(2023, 1, 29),
        )
        ArenaTestdata.leggTilMeldekort(meldekortId = 2021, personId = 202, aar = 2023, periodekode = "52")
        ArenaTestdata.leggTilMeldelogg(meldekortId = 2021)

        repo.hentMeldekortForFnr("202", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31)) shouldBe emptyList()
    }

    @Test
    fun `meldekort uten meldelogg feiler - hendelsedato mappes som ikke-nullbar fra en left join`() {
        // Dokumenterer dagens oppførsel: MELDELOGG er LEFT JOIN-et i MeldekortDAO, men
        // HENDELSEDATO mappes med localDate() som ikke tåler null.
        // TODO: hendelsedato (og modDato) skal gjøres nullbare gjennom hele kjeden, og
        //  kontrakten mot NKS endres rett før - se TODO i ArenaMeldekortDTO. Da snus denne
        //  testen til å forvente meldekort med statusDato = null.
        ArenaTestdata.leggTilPerson(personId = 203, fnr = "203")
        ArenaTestdata.leggTilMeldekortperiode(
            aar = 2023,
            periodekode = "53",
            datoFra = LocalDate.of(2023, 1, 30),
            datoTil = LocalDate.of(2023, 2, 12),
        )
        ArenaTestdata.leggTilMeldekort(meldekortId = 2031, personId = 203, aar = 2023, periodekode = "53")

        shouldThrow<NullPointerException> {
            repo.hentMeldekortForFnr("203", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31))
        }
    }
}
