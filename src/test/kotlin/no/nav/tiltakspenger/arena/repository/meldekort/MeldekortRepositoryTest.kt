package no.nav.tiltakspenger.arena.repository.meldekort

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import no.nav.tiltakspenger.arena.db.OracleTestbase
import no.nav.tiltakspenger.arena.repository.ArenaTestdata
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.time.LocalDate

/**
 * Kjører mot delt Oracle-testcontainer, se [OracleTestbase].
 * Hver test eier sine egne data og bruker unike id-er/fnr (2xx-serien).
 *
 * Happy-path (meldekort med dager, kodeverk-navn, periode) eies av MeldekortRouteTest.
 * Testene her beholdes kun for spørrings-/mappingdetaljer som ikke er observerbare gjennom ett route-kall.
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

    // Seeder et meldekort utenfor det etterspurte intervallet: pinner periodefilteret i spørringen (gir `[]`).
    @Test
    fun `meldekort utenfor etterspurt periode filtreres bort`() {
        ArenaTestdata.leggTilPerson(personId = 202, fnr = "202")
        ArenaTestdata.leggTilMeldekortperiode(
            år = 2023,
            periodekode = "52",
            datoFra = LocalDate.of(2023, 1, 16),
            datoTil = LocalDate.of(2023, 1, 29),
        )
        ArenaTestdata.leggTilMeldekort(meldekortId = 2021, personId = 202, år = 2023, periodekode = "52")
        ArenaTestdata.leggTilMeldelogg(meldekortId = 2021)

        repo.hentMeldekortForFnr("202", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31)) shouldBe emptyList()
    }

    // Repo-test fordi den asserter en NPE som kastes i DAO-mappingen før serialisering — ikke uttrykkbart som en route-kontrakt.
    @Test
    fun `meldekort uten meldelogg feiler - hendelsedato mappes non-null`() {
        // Dokumenterer den kjente skarpe kanten: HENDELSEDATO hentes via LEFT JOIN mot MELDELOGG, men mappes non-null.
        // 0 slike meldekort i Q2 (se doc/arena-ddl/nullability_arena_tilgang_ind.md), så vi håndterer bevisst ikke null-tilfellet.
        // Denne testen pinner oppførselen: skulle et meldekort mangle logg-treff, feiler det med NPE — da må feltene gjøres nullbare.
        ArenaTestdata.leggTilPerson(personId = 203, fnr = "203")
        ArenaTestdata.leggTilMeldekortperiode(
            år = 2023,
            periodekode = "53",
            datoFra = LocalDate.of(2023, 1, 30),
            datoTil = LocalDate.of(2023, 2, 12),
        )
        // Ingen meldelogg lagt til
        ArenaTestdata.leggTilMeldekort(meldekortId = 2031, personId = 203, år = 2023, periodekode = "53")

        shouldThrow<NullPointerException> {
            repo.hentMeldekortForFnr("203", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31))
        }
    }
}
