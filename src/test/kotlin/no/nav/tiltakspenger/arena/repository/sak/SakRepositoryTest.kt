package no.nav.tiltakspenger.arena.repository.sak

import io.kotest.matchers.shouldBe
import no.nav.tiltakspenger.arena.db.OracleTestbase
import no.nav.tiltakspenger.arena.repository.ArenaTestdata
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.time.LocalDate

/**
 * Kjører mot delt Oracle-testcontainer, se [OracleTestbase].
 * Hver test eier sine egne data og bruker unike id-er/fnr (1xx-serien).
 *
 * Happy-path og JSON-kontrakt eies av route-testene (VedtaksperioderRouteTest/VedtakRouteTest).
 * Testene her beholdes kun for spørringsdetaljer som ikke er observerbare gjennom ett route-kall: join-/filter-grener der data som IKKE skal med må seedes.
 */
class SakRepositoryTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            OracleTestbase.start()
        }
    }

    private val repo = SakRepository()

    // Person finnes i PERSON, men har ingen SAK.
    // Verifiserer at joinen forkaster personen (en LEFT JOIN-bug ville lekket en rad) — route-tom-lista bruker en ikke-eksisterende person og treffer ikke denne grenen.
    @Test
    fun `person uten saker gir tom liste`() {
        ArenaTestdata.leggTilPerson(personId = 101, fnr = "101")

        repo.hentSakerForFnr("101") shouldBe emptyList()
    }

    // Verifiserer at repoet returnerer flere saker for samme person (ingen utilsiktet dedup/kollaps) — en kardinalitetsegenskap ved spørringen, ikke en kontraktdetalj.
    @Test
    fun `person med to saker gir begge sakene`() {
        ArenaTestdata.leggTilPerson(personId = 103, fnr = "103")
        ArenaTestdata.leggTilSak(sakId = 1031, personId = 103, lopenrSak = 31)
        ArenaTestdata.leggTilVedtak(vedtakId = 10311, sakId = 1031, personId = 103)
        ArenaTestdata.leggTilSak(sakId = 1032, personId = 103, lopenrSak = 32)
        ArenaTestdata.leggTilVedtak(
            vedtakId = 10321,
            sakId = 1032,
            personId = 103,
            fraDato = LocalDate.of(2024, 1, 1),
            tilDato = LocalDate.of(2024, 3, 31),
        )

        repo.hentSakerForFnr("103").size shouldBe 2
    }

    // Seeder en HIST-sak som IKKE skal med: pinner ekskluderingsfilteret i spørringen.
    // Via route ville dette bare gitt `[]` og ikke skilt filteret fra «ingen data».
    @Test
    fun `historiserte saker filtreres bort`() {
        ArenaTestdata.leggTilPerson(personId = 104, fnr = "104")
        ArenaTestdata.leggTilSak(sakId = 1041, personId = 104, sakstatuskode = "HIST")
        ArenaTestdata.leggTilVedtak(vedtakId = 10411, sakId = 1041, personId = 104)

        repo.hentSakerForFnr("104") shouldBe emptyList()
    }

    // Seeder et NEI-vedtak som skal ekskluderes, og en sak som dermed står igjen uten vedtak og skal forsvinne.
    // Kombinert filter-/opprydningslogikk i spørringen, ikke observerbar via ett route-kall.
    @Test
    fun `vedtak med utfall NEI filtreres bort, og sak uten gjenværende vedtak forsvinner`() {
        ArenaTestdata.leggTilPerson(personId = 105, fnr = "105")
        ArenaTestdata.leggTilSak(sakId = 1051, personId = 105)
        ArenaTestdata.leggTilVedtak(vedtakId = 10511, sakId = 1051, personId = 105, utfallkode = "NEI")

        repo.hentSakerForFnr("105") shouldBe emptyList()
    }

    // Pinner periodefilteret (fom/tom) i spørringen: samme datasett gir 1 sak uten filter og 0 med et fom utenfor perioden.
    @Test
    fun `vedtak utenfor etterspurt periode filtreres bort`() {
        ArenaTestdata.leggTilPerson(personId = 106, fnr = "106")
        ArenaTestdata.leggTilSak(sakId = 1061, personId = 106)
        ArenaTestdata.leggTilVedtak(
            vedtakId = 10611,
            sakId = 1061,
            personId = 106,
            fraDato = LocalDate.of(2023, 1, 1),
            tilDato = LocalDate.of(2023, 3, 31),
        )

        repo.hentSakerForFnr("106", fom = LocalDate.of(2024, 1, 1)) shouldBe emptyList()
        repo.hentSakerForFnr("106").size shouldBe 1
    }
}
