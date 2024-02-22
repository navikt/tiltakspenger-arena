package no.nav.tiltakspenger.arena.repository

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ArenaVedtakDTOTest {

    val vanligVedtak = ArenaVedtakDTO(
        vedtakType = ArenaVedtakType.O,
        uttaksgrad = 100,
        fomVedtaksperiode = LocalDate.of(2023, 11, 14),
        tomVedtaksperiode = LocalDate.of(2023, 11, 15),
        status = ArenaVedtakStatus.IVERK,
        rettighettype = ArenaRettighet.BASI,
        aktivitetsfase = ArenaAktivitetFase.UVUP,
        dagsats = 255,
        beslutningsdato = LocalDate.of(2023, 11, 10),
        mottattDato = LocalDate.of(2023, 11, 7),
        registrertDato = LocalDate.of(2023, 11, 7),
        utfall = ArenaUtfall.JA,
        antallDager = 5.0,
        opprinneligTomVedtaksperiode = LocalDate.of(2023, 11, 11),
        relatertTiltak = "Tiltak",
        antallBarn = 0,
    )

    val vedtakMedÅpenSluttdato = ArenaVedtakDTO(
        vedtakType = ArenaVedtakType.O,
        uttaksgrad = 100,
        fomVedtaksperiode = LocalDate.of(2023, 11, 14),
        tomVedtaksperiode = null,
        status = ArenaVedtakStatus.IVERK,
        rettighettype = ArenaRettighet.BASI,
        aktivitetsfase = ArenaAktivitetFase.UVUP,
        dagsats = 255,
        beslutningsdato = LocalDate.of(2023, 11, 10),
        mottattDato = LocalDate.of(2023, 11, 7),
        registrertDato = LocalDate.of(2023, 11, 7),
        utfall = ArenaUtfall.JA,
        antallDager = 5.0,
        opprinneligTomVedtaksperiode = LocalDate.of(2023, 11, 11),
        relatertTiltak = "Tiltak",
        antallBarn = 0,
    )

    val vedtakEngangsutbetaling = ArenaVedtakDTO(
        vedtakType = ArenaVedtakType.O,
        uttaksgrad = 100,
        fomVedtaksperiode = LocalDate.of(2023, 11, 14),
        tomVedtaksperiode = LocalDate.of(2023, 11, 13),
        status = ArenaVedtakStatus.IVERK,
        rettighettype = ArenaRettighet.BASI,
        aktivitetsfase = ArenaAktivitetFase.UVUP,
        dagsats = 255,
        beslutningsdato = LocalDate.of(2023, 11, 10),
        mottattDato = LocalDate.of(2023, 11, 7),
        registrertDato = LocalDate.of(2023, 11, 7),
        utfall = ArenaUtfall.JA,
        antallDager = 5.0,
        opprinneligTomVedtaksperiode = LocalDate.of(2023, 11, 11),
        relatertTiltak = "Tiltak",
        antallBarn = 0,
    )

    @Test
    fun `vanligeVedtak fungerer`() {
        vanligVedtak.isTiltakspenger() shouldBe true
        vanligVedtak.isNyRettighetOrGjenopptakOrEndring() shouldBe true
        vanligVedtak.isNotAvbruttOrNei() shouldBe true
        vanligVedtak.isVedtaksperiodeÅpen() shouldBe false
        vanligVedtak.isFraDatoNotNull() shouldBe true
        vanligVedtak.isNotEngangsutbetaling() shouldBe true
    }

    @Test
    fun `vedtakk med åpen sluttdato fungerer`() {
        vedtakMedÅpenSluttdato.isTiltakspenger() shouldBe true
        vedtakMedÅpenSluttdato.isNyRettighetOrGjenopptakOrEndring() shouldBe true
        vedtakMedÅpenSluttdato.isNotAvbruttOrNei() shouldBe true
        vedtakMedÅpenSluttdato.isVedtaksperiodeÅpen() shouldBe true
        vedtakMedÅpenSluttdato.isFraDatoNotNull() shouldBe true
        vedtakMedÅpenSluttdato.isNotEngangsutbetaling() shouldBe true
    }

    @Test
    fun `vedtak som er spesialutbetalinger fungerer`() {
        vedtakEngangsutbetaling.isTiltakspenger() shouldBe true
        vedtakEngangsutbetaling.isNyRettighetOrGjenopptakOrEndring() shouldBe true
        vedtakEngangsutbetaling.isNotAvbruttOrNei() shouldBe true
        vedtakEngangsutbetaling.isVedtaksperiodeÅpen() shouldBe false
        vedtakEngangsutbetaling.isFraDatoNotNull() shouldBe true
        vedtakEngangsutbetaling.isNotEngangsutbetaling() shouldBe false
    }
}
