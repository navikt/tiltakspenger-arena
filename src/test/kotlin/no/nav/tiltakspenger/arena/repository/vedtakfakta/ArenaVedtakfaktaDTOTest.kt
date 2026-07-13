package no.nav.tiltakspenger.arena.repository.vedtakfakta

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotContain
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ArenaVedtakfaktaDTOTest {

    private val kontekst = VedtakfaktaLoggkontekst(
        fnr = "01234567891",
        sakId = 13297369L,
        saksnummer = "202229331",
    )

    private fun vedtakfakta(fakta: ArenaVedtakFakta, verdi: String?) =
        ArenaVedtakfaktaDTO(
            vedtakId = 36475317L,
            vedtakfaktaKode = fakta.name,
            vedtakfaktaVerdi = verdi,
        )

    @Test
    fun `mapper alle tiltakspenger-vedtakfakta`() {
        val dto = listOf(
            vedtakfakta(ArenaVedtakFakta.INNVF, "05-01-2022"),
            vedtakfakta(ArenaVedtakFakta.DAGS, "285"),
            vedtakfakta(ArenaVedtakFakta.DAGUTBTILT, "10"),
            vedtakfakta(ArenaVedtakFakta.KODETILTAK, "133924438"),
            vedtakfakta(ArenaVedtakFakta.TILTAKNAVN, "Arbeidstrening"),
            vedtakfakta(ArenaVedtakFakta.OPPRTDATO, "31-12-2022"),
            vedtakfakta(ArenaVedtakFakta.FDATO, "01-01-2022"),
            vedtakfakta(ArenaVedtakFakta.TDATO, "31-12-2022"),
            vedtakfakta(ArenaVedtakFakta.SATSKODE, "HOY"),
            vedtakfakta(ArenaVedtakFakta.MASKVEDTAK, "N"),
        ).toArenaTiltakspengerVedtakfaktaDTO(kontekst)

        dto shouldBe ArenaTiltakspengerVedtakfaktaDTO(
            beslutningsdato = LocalDate.of(2022, 1, 5),
            dagsats = 285,
            antallDager = 10.0,
            relatertTiltak = "133924438",
            relatertTiltakNavn = "Arbeidstrening",
            opprinneligTilDato = LocalDate.of(2022, 12, 31),
            gjelderFra = LocalDate.of(2022, 1, 1),
            gjelderTil = LocalDate.of(2022, 12, 31),
            satsKode = "HOY",
            maskineltVedtak = "N",
        )
    }

    @Test
    fun `mapper alle barnetillegg-vedtakfakta`() {
        val dto = listOf(
            vedtakfakta(ArenaVedtakFakta.INNVF, "05-01-2022"),
            vedtakfakta(ArenaVedtakFakta.DAGS, "53"),
            vedtakfakta(ArenaVedtakFakta.DAGUTBTILT, "5"),
            vedtakfakta(ArenaVedtakFakta.KODETILTAK, "133924438"),
            vedtakfakta(ArenaVedtakFakta.TILTAKNAVN, "Arbeidstrening"),
            vedtakfakta(ArenaVedtakFakta.OPPRTDATO, "31-12-2022"),
            vedtakfakta(ArenaVedtakFakta.BARNMSTON, "2"),
            vedtakfakta(ArenaVedtakFakta.FDATO, "01-01-2022"),
            vedtakfakta(ArenaVedtakFakta.TDATO, "31-12-2022"),
            vedtakfakta(ArenaVedtakFakta.SATSKODE, "HOY"),
            vedtakfakta(ArenaVedtakFakta.MASKVEDTAK, "N"),
        ).toArenaBarnetilleggVedtakfaktaDTO(kontekst)

        dto shouldBe ArenaBarnetilleggVedtakfaktaDTO(
            beslutningsdato = LocalDate.of(2022, 1, 5),
            dagsats = 53,
            antallDager = 5.0,
            tiltakGjennomføringsId = "133924438",
            relatertTiltakNavn = "Arbeidstrening",
            opprinneligTilDato = LocalDate.of(2022, 12, 31),
            antallBarn = 2,
            gjelderFra = LocalDate.of(2022, 1, 1),
            gjelderTil = LocalDate.of(2022, 12, 31),
            satsKode = "HOY",
            maskineltVedtak = "N",
        )
    }

    @Test
    fun `mapper alle utbetalingshistorikk-vedtakfakta`() {
        val dto = listOf(
            vedtakfakta(ArenaVedtakFakta.DAGS, "285"),
            vedtakfakta(ArenaVedtakFakta.FDATO, "01-03-2021"),
            vedtakfakta(ArenaVedtakFakta.TDATO, "14-03-2021"),
            vedtakfakta(ArenaVedtakFakta.ANTALL, "2"),
            vedtakfakta(ArenaVedtakFakta.BEL, "1995"),
            vedtakfakta(ArenaVedtakFakta.EKSTID, "NAV Oslo"),
        ).tilArenaUtbetalingshistorikkVedtakfaktaDTO(kontekst)

        dto shouldBe ArenaUtbetalingshistorikkVedtakfaktaDTO(
            dagsats = 285,
            gjelderFra = LocalDate.of(2021, 3, 1),
            gjelderTil = LocalDate.of(2021, 3, 14),
            antallUtbetalinger = 2,
            belopPerUtbetalinger = 1995,
            alternativBetalingsmottaker = "NAV Oslo",
        )
    }

    @Test
    fun `heltallsfelter med desimalverdi rundes av til nærmeste heltall`() {
        val dto = listOf(
            vedtakfakta(ArenaVedtakFakta.DAGS, "284.5"),
            vedtakfakta(ArenaVedtakFakta.BARNMSTON, "0.961538461538462"),
        ).toArenaBarnetilleggVedtakfaktaDTO(kontekst)

        dto.dagsats shouldBe 285
        dto.antallBarn shouldBe 1
    }

    @Test
    fun `desimalverdi under en halv rundes ned`() {
        val dto = listOf(
            vedtakfakta(ArenaVedtakFakta.BARNMSTON, "0.4"),
        ).toArenaBarnetilleggVedtakfaktaDTO(VedtakfaktaLoggkontekst())

        dto.antallBarn shouldBe 0
    }

    @Test
    fun `toString på loggkonteksten maskerer fnr`() {
        kontekst.toString() shouldBe "VedtakfaktaLoggkontekst(fnr=***********, sakId=13297369, saksnummer=202229331)"
        kontekst.toString() shouldNotContain "01234567891"
    }

    @Test
    fun `manglende vedtakfakta gir null for alle felter`() {
        val tomKontekst = VedtakfaktaLoggkontekst()
        val ingenFakta = emptyList<ArenaVedtakfaktaDTO>()

        ingenFakta.toArenaTiltakspengerVedtakfaktaDTO(tomKontekst) shouldBe ArenaTiltakspengerVedtakfaktaDTO(
            beslutningsdato = null,
            dagsats = null,
            antallDager = null,
            relatertTiltak = null,
            relatertTiltakNavn = null,
            opprinneligTilDato = null,
            gjelderFra = null,
            gjelderTil = null,
            satsKode = null,
            maskineltVedtak = null,
        )
        ingenFakta.toArenaBarnetilleggVedtakfaktaDTO(tomKontekst) shouldBe ArenaBarnetilleggVedtakfaktaDTO(
            beslutningsdato = null,
            dagsats = null,
            antallDager = null,
            tiltakGjennomføringsId = null,
            relatertTiltakNavn = null,
            opprinneligTilDato = null,
            antallBarn = null,
            gjelderFra = null,
            gjelderTil = null,
            satsKode = null,
            maskineltVedtak = null,
        )
        ingenFakta.tilArenaUtbetalingshistorikkVedtakfaktaDTO(tomKontekst) shouldBe ArenaUtbetalingshistorikkVedtakfaktaDTO(
            dagsats = null,
            gjelderFra = null,
            gjelderTil = null,
            antallUtbetalinger = null,
            belopPerUtbetalinger = null,
            alternativBetalingsmottaker = null,
        )
    }
}
