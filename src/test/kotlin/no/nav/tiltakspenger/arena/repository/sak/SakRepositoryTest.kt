package no.nav.tiltakspenger.arena.repository.sak

import io.kotest.matchers.shouldBe
import no.nav.tiltakspenger.arena.db.OracleTestbase
import no.nav.tiltakspenger.arena.repository.ArenaTestdata
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.time.LocalDate

/**
 * Kjører mot delt Oracle-testcontainer, se [OracleTestbase]. Hver test eier sine egne data og
 * bruker unike id-er/fnr (1xx-serien).
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

    @Test
    fun `person som ikke finnes i Arena gir tom liste`() {
        repo.hentSakerForFnr("100") shouldBe emptyList()
    }

    @Test
    fun `person uten saker gir tom liste`() {
        ArenaTestdata.leggTilPerson(personId = 101, fnr = "101")

        repo.hentSakerForFnr("101") shouldBe emptyList()
    }

    @Test
    fun `sak med tiltakspengervedtak hentes med verdier fra vedtak og vedtakfakta`() {
        ArenaTestdata.leggTilPerson(personId = 102, fnr = "102")
        ArenaTestdata.leggTilSak(sakId = 1021, personId = 102, aar = 2023, lopenrSak = 21)
        ArenaTestdata.leggTilVedtak(
            vedtakId = 10211,
            sakId = 1021,
            personId = 102,
            fraDato = LocalDate.of(2023, 1, 1),
            tilDato = LocalDate.of(2023, 3, 31),
        )
        ArenaTestdata.leggTilVedtakfakta(vedtakId = 10211, kode = "DAGS", verdi = "285")
        ArenaTestdata.leggTilVedtakfakta(vedtakId = 10211, kode = "DAGUTBTILT", verdi = "10")
        ArenaTestdata.leggTilVedtakfakta(vedtakId = 10211, kode = "KODETILTAK", verdi = "133924438")

        val saker = repo.hentSakerForFnr("102")

        saker.size shouldBe 1
        val sak = saker.single()
        sak.fagsystemSakId shouldBe "202321"
        val vedtak = sak.tiltakspengerVedtak.single()
        vedtak.dagsats shouldBe 285
        vedtak.antallDager shouldBe 10.0
        vedtak.relatertTiltak shouldBe "133924438"
        vedtak.fomVedtaksperiode shouldBe LocalDate.of(2023, 1, 1)
        vedtak.tomVedtaksperiode shouldBe LocalDate.of(2023, 3, 31)
    }

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

    @Test
    fun `historiserte saker filtreres bort`() {
        ArenaTestdata.leggTilPerson(personId = 104, fnr = "104")
        ArenaTestdata.leggTilSak(sakId = 1041, personId = 104, sakstatuskode = "HIST")
        ArenaTestdata.leggTilVedtak(vedtakId = 10411, sakId = 1041, personId = 104)

        repo.hentSakerForFnr("104") shouldBe emptyList()
    }

    @Test
    fun `vedtak med utfall NEI filtreres bort, og sak uten gjenværende vedtak forsvinner`() {
        ArenaTestdata.leggTilPerson(personId = 105, fnr = "105")
        ArenaTestdata.leggTilSak(sakId = 1051, personId = 105)
        ArenaTestdata.leggTilVedtak(vedtakId = 10511, sakId = 1051, personId = 105, utfallkode = "NEI")

        repo.hentSakerForFnr("105") shouldBe emptyList()
    }

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

    @Test
    fun `barnetillegg med desimalt antall barn rundes av til nærmeste heltall`() {
        // Reell prod-case: BARNMSTON kan inneholde desimaltall, f.eks. 0.961538461538462
        ArenaTestdata.leggTilPerson(personId = 107, fnr = "107")
        ArenaTestdata.leggTilSak(sakId = 1071, personId = 107)
        ArenaTestdata.leggTilVedtak(vedtakId = 10711, sakId = 1071, personId = 107)
        ArenaTestdata.leggTilVedtak(vedtakId = 10712, sakId = 1071, personId = 107, rettighetkode = "BTIL")
        ArenaTestdata.leggTilVedtakfakta(vedtakId = 10712, kode = "BARNMSTON", verdi = "0.961538461538462")
        ArenaTestdata.leggTilVedtakfakta(vedtakId = 10712, kode = "DAGS", verdi = "53")

        val sak = repo.hentSakerForFnr("107").single()

        val barnetillegg = sak.barnetilleggVedtak.single()
        barnetillegg.antallBarn shouldBe 1
        barnetillegg.dagsats shouldBe 53
    }
}
