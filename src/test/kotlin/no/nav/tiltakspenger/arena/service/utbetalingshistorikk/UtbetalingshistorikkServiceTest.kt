package no.nav.tiltakspenger.arena.service.utbetalingshistorikk

import io.kotest.matchers.shouldBe
import no.nav.tiltakspenger.arena.db.OracleTestbase
import no.nav.tiltakspenger.arena.repository.ArenaTestdata
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.time.LocalDate

/**
 * Kjører mot delt Oracle-testcontainer, se [OracleTestbase]. Hver test eier sine egne data og
 * bruker unike id-er/fnr (3xx-serien).
 *
 * Utbetalingshistorikken settes sammen av fem kilder med prioritering via NOT EXISTS:
 * posteringer og utbetalingsgrunnlag er primærkildene, beregningslogg og anmerkninger er
 * fallback for meldekort uten utbetaling, og til slutt beregnede meldekort uten spor i noen
 * av de andre kildene.
 */
class UtbetalingshistorikkServiceTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            OracleTestbase.start()
        }
    }

    private val service = UtbetalingshistorikkService()

    private val fraOgMed = LocalDate.of(2023, 1, 1)
    private val tilOgMed = LocalDate.of(2023, 12, 31)

    @Test
    fun `person som ikke finnes i Arena gir tom liste`() {
        service.hentUtbetalingshistorikkForFnr("300", fraOgMed, tilOgMed) shouldBe emptyList()
    }

    @Test
    fun `postering vises som overført utbetaling`() {
        ArenaTestdata.leggTilPerson(personId = 301, fnr = "301")
        ArenaTestdata.leggTilPostering(
            personId = 301,
            vedtakId = 30111,
            meldekortId = 3011,
            belop = 2850.0,
            posteringsats = 285.0,
            datoPostert = LocalDate.of(2023, 1, 20),
        )

        val historikk = service.hentUtbetalingshistorikkForFnr("301", fraOgMed, tilOgMed)

        val innslag = historikk.single()
        innslag.status shouldBe "Overført utbetaling"
        innslag.transaksjonstype shouldBe "Utbetaling"
        innslag.belop shouldBe 2850.0
        innslag.sats shouldBe 285.0
        innslag.meldekortId shouldBe 3011
        innslag.vedtakId shouldBe 30111
        innslag.dato shouldBe LocalDate.of(2023, 1, 20)
    }

    @Test
    fun `utbetalingsgrunnlag vises som ikke overført utbetaling`() {
        ArenaTestdata.leggTilPerson(personId = 302, fnr = "302")
        ArenaTestdata.leggTilUtbetalingsgrunnlag(
            personId = 302,
            vedtakId = 30211,
            meldekortId = 3021,
            belop = 1425.0,
        )

        val innslag = service.hentUtbetalingshistorikkForFnr("302", fraOgMed, tilOgMed).single()

        innslag.status shouldBe "Ikke overført utbetaling"
        innslag.belop shouldBe 1425.0
        innslag.meldekortId shouldBe 3021
    }

    @Test
    fun `beregningslogg er fallback for meldekort uten utbetaling`() {
        ArenaTestdata.leggTilPerson(personId = 303, fnr = "303")
        ArenaTestdata.leggTilSak(sakId = 3031, personId = 303)
        ArenaTestdata.leggTilVedtak(vedtakId = 30311, sakId = 3031, personId = 303)
        ArenaTestdata.leggTilMeldekortperiode(
            aar = 2023,
            periodekode = "61",
            datoFra = LocalDate.of(2023, 1, 2),
            datoTil = LocalDate.of(2023, 1, 15),
        )
        ArenaTestdata.leggTilMeldekort(
            meldekortId = 3032,
            personId = 303,
            aar = 2023,
            periodekode = "61",
            beregningstatuskode = "FEIL",
        )
        ArenaTestdata.leggTilBeregningslogg(objektId = 3032, personId = 303, vedtakId = 30311)

        val innslag = service.hentUtbetalingshistorikkForFnr("303", fraOgMed, tilOgMed).single()

        innslag.status shouldBe "Feil i beregning"
        innslag.transaksjonstype shouldBe "Tiltakspenger"
        innslag.belop shouldBe 0.0
        innslag.meldekortId shouldBe 3032
        innslag.vedtakId shouldBe 30311
    }

    @Test
    fun `beregningslogg vises ikke når meldekortet har postering`() {
        ArenaTestdata.leggTilPerson(personId = 304, fnr = "304")
        ArenaTestdata.leggTilSak(sakId = 3041, personId = 304)
        ArenaTestdata.leggTilVedtak(vedtakId = 30411, sakId = 3041, personId = 304)
        ArenaTestdata.leggTilMeldekortperiode(
            aar = 2023,
            periodekode = "62",
            datoFra = LocalDate.of(2023, 1, 16),
            datoTil = LocalDate.of(2023, 1, 29),
        )
        ArenaTestdata.leggTilMeldekort(meldekortId = 3042, personId = 304, aar = 2023, periodekode = "62")
        ArenaTestdata.leggTilBeregningslogg(objektId = 3042, personId = 304, vedtakId = 30411)
        ArenaTestdata.leggTilPostering(personId = 304, vedtakId = 30411, meldekortId = 3042)

        val historikk = service.hentUtbetalingshistorikkForFnr("304", fraOgMed, tilOgMed)

        // Kun posteringen - beregningsloggen og det beregnede meldekortet filtreres av NOT EXISTS
        val innslag = historikk.single()
        innslag.status shouldBe "Overført utbetaling"
    }

    @Test
    fun `anmerkning er fallback når verken utbetaling eller beregningslogg finnes`() {
        ArenaTestdata.leggTilPerson(personId = 305, fnr = "305")
        ArenaTestdata.leggTilSak(sakId = 3051, personId = 305)
        ArenaTestdata.leggTilVedtak(vedtakId = 30511, sakId = 3051, personId = 305)
        ArenaTestdata.leggTilMeldekortperiode(
            aar = 2023,
            periodekode = "63",
            datoFra = LocalDate.of(2023, 1, 30),
            datoTil = LocalDate.of(2023, 2, 12),
        )
        ArenaTestdata.leggTilMeldekort(
            meldekortId = 3052,
            personId = 305,
            aar = 2023,
            periodekode = "63",
            beregningstatuskode = "FEIL",
        )
        ArenaTestdata.leggTilAnmerkning(anmerkningId = 30521, objektId = 3052, vedtakId = 30511)

        val innslag = service.hentUtbetalingshistorikkForFnr("305", fraOgMed, tilOgMed).single()

        innslag.status shouldBe "Feil i beregning"
        innslag.transaksjonstype shouldBe "Tiltakspenger"
        innslag.meldekortId shouldBe 3052
        innslag.vedtakId shouldBe 30511
        innslag.fraOgMedDato shouldBe LocalDate.of(2023, 1, 30)
        innslag.tilOgMedDato shouldBe LocalDate.of(2023, 2, 12)
    }

    @Test
    fun `beregnet meldekort uten spor i andre kilder vises som eget innslag`() {
        ArenaTestdata.leggTilPerson(personId = 306, fnr = "306")
        ArenaTestdata.leggTilMeldekortperiode(
            aar = 2023,
            periodekode = "64",
            datoFra = LocalDate.of(2023, 2, 13),
            datoTil = LocalDate.of(2023, 2, 26),
        )
        ArenaTestdata.leggTilMeldekort(meldekortId = 3061, personId = 306, aar = 2023, periodekode = "64")

        val innslag = service.hentUtbetalingshistorikkForFnr("306", fraOgMed, tilOgMed).single()

        innslag.status shouldBe "Ferdig beregnet"
        innslag.transaksjonstype shouldBe "Meldekort"
        innslag.meldekortId shouldBe 3061
        innslag.vedtakId shouldBe null
    }

    @Test
    fun `meldekort med beregningstatus OPPRE vises ikke som beregnet meldekort`() {
        ArenaTestdata.leggTilPerson(personId = 307, fnr = "307")
        ArenaTestdata.leggTilMeldekortperiode(
            aar = 2023,
            periodekode = "65",
            datoFra = LocalDate.of(2023, 2, 27),
            datoTil = LocalDate.of(2023, 3, 12),
        )
        ArenaTestdata.leggTilMeldekort(
            meldekortId = 3071,
            personId = 307,
            aar = 2023,
            periodekode = "65",
            beregningstatuskode = "OPPRE",
        )

        service.hentUtbetalingshistorikkForFnr("307", fraOgMed, tilOgMed) shouldBe emptyList()
    }

    @Test
    fun `henter vedtakfakta og anmerkninger for detaljvisningen`() {
        ArenaTestdata.leggTilPerson(personId = 308, fnr = "308")
        ArenaTestdata.leggTilSak(sakId = 3081, personId = 308)
        ArenaTestdata.leggTilVedtak(vedtakId = 30811, sakId = 3081, personId = 308)
        ArenaTestdata.leggTilVedtakfakta(vedtakId = 30811, kode = "DAGS", verdi = "285")
        ArenaTestdata.leggTilVedtakfakta(vedtakId = 30811, kode = "ANTALL", verdi = "2")
        ArenaTestdata.leggTilVedtakfakta(vedtakId = 30811, kode = "BEL", verdi = "1995")
        ArenaTestdata.leggTilAnmerkning(anmerkningId = 30821, objektId = 3082, vedtakId = 30811, verdi = 5)

        val vedtakfakta = service.hentVedtakfaktaForVedtak(30811)
        vedtakfakta?.dagsats shouldBe 285
        vedtakfakta?.antallUtbetalinger shouldBe 2
        vedtakfakta?.belopPerUtbetalinger shouldBe 1995

        val anmerkninger = service.hentAnmerkningerForMeldekort(3082)
        anmerkninger.single().beskrivelse shouldBe "Meldekort avvist, mangler 5 dager"
    }

    @Test
    fun `detaljer uten id-er gir tomme svar`() {
        service.hentVedtakfaktaForVedtak(null) shouldBe null
        service.hentAnmerkningerForMeldekort(null) shouldBe emptyList()
    }
}
