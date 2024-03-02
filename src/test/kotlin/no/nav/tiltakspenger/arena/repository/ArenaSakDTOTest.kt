package no.nav.tiltakspenger.arena.repository

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ArenaSakDTOTest {

    private val sakHvorVedtakIkkeErIRekkefølge = ArenaSakDTO(
        aar = 2022,
        lopenrSak = 11,
        status = ArenaSakStatus.AKTIV,
        ytelsestype = ArenaYtelse.INDIV,
        vedtak = listOf(
            ArenaVedtakDTO(
                vedtakType = ArenaVedtakType.O,
                uttaksgrad = 100,
                fomVedtaksperiode = LocalDate.of(2023, 11, 11),
                tomVedtaksperiode = LocalDate.of(2023, 11, 13),
                status = ArenaVedtakStatus.IVERK,
                rettighettype = ArenaRettighet.BASI,
                aktivitetsfase = ArenaAktivitetFase.UGJEN,
                dagsats = 255,
                beslutningsdato = LocalDate.of(2023, 11, 10),
                mottattDato = LocalDate.of(2023, 11, 7),
                registrertDato = LocalDate.of(2023, 11, 7),
                utfall = ArenaUtfall.JA,
                antallDager = 5.0,
                opprinneligTomVedtaksperiode = LocalDate.of(2023, 11, 11),
                relatertTiltak = "Tiltak",
                antallBarn = 0,
            ),
            ArenaVedtakDTO(
                vedtakType = ArenaVedtakType.O,
                uttaksgrad = 100,
                fomVedtaksperiode = LocalDate.of(2023, 11, 14),
                tomVedtaksperiode = LocalDate.of(2023, 11, 15),
                status = ArenaVedtakStatus.IVERK,
                rettighettype = ArenaRettighet.BASI,
                aktivitetsfase = ArenaAktivitetFase.UGJEN,
                dagsats = 255,
                beslutningsdato = LocalDate.of(2023, 11, 10),
                mottattDato = LocalDate.of(2023, 11, 7),
                registrertDato = LocalDate.of(2023, 11, 7),
                utfall = ArenaUtfall.JA,
                antallDager = 5.0,
                opprinneligTomVedtaksperiode = LocalDate.of(2023, 11, 11),
                relatertTiltak = "Tiltak",
                antallBarn = 0,
            ),
            ArenaVedtakDTO(
                vedtakType = ArenaVedtakType.O,
                uttaksgrad = 100,
                fomVedtaksperiode = LocalDate.of(2023, 11, 7),
                tomVedtaksperiode = LocalDate.of(2023, 11, 10),
                status = ArenaVedtakStatus.IVERK,
                rettighettype = ArenaRettighet.BASI,
                aktivitetsfase = ArenaAktivitetFase.UGJEN,
                dagsats = 255,
                beslutningsdato = LocalDate.of(2023, 11, 10),
                mottattDato = LocalDate.of(2023, 11, 7),
                registrertDato = LocalDate.of(2023, 11, 7),
                utfall = ArenaUtfall.JA,
                antallDager = 5.0,
                opprinneligTomVedtaksperiode = LocalDate.of(2023, 11, 11),
                relatertTiltak = "Tiltak",
                antallBarn = 0,
            ),
        ),
    )

    private val sakMedVedtakSomAlleHarSluttDato = ArenaSakDTO(
        aar = 2022,
        lopenrSak = 11,
        status = ArenaSakStatus.AKTIV,
        ytelsestype = ArenaYtelse.INDIV,
        vedtak = listOf(
            ArenaVedtakDTO(
                vedtakType = ArenaVedtakType.O,
                uttaksgrad = 100,
                fomVedtaksperiode = LocalDate.of(2023, 11, 11),
                tomVedtaksperiode = LocalDate.of(2023, 11, 13),
                status = ArenaVedtakStatus.IVERK,
                rettighettype = ArenaRettighet.BASI,
                aktivitetsfase = ArenaAktivitetFase.UGJEN,
                dagsats = 255,
                beslutningsdato = LocalDate.of(2023, 11, 10),
                mottattDato = LocalDate.of(2023, 11, 7),
                registrertDato = LocalDate.of(2023, 11, 7),
                utfall = ArenaUtfall.JA,
                antallDager = 5.0,
                opprinneligTomVedtaksperiode = LocalDate.of(2023, 11, 11),
                relatertTiltak = "Tiltak",
                antallBarn = 0,
            ),
            ArenaVedtakDTO(
                vedtakType = ArenaVedtakType.O,
                uttaksgrad = 100,
                fomVedtaksperiode = LocalDate.of(2023, 11, 14),
                tomVedtaksperiode = LocalDate.of(2023, 11, 15),
                status = ArenaVedtakStatus.IVERK,
                rettighettype = ArenaRettighet.BASI,
                aktivitetsfase = ArenaAktivitetFase.UGJEN,
                dagsats = 255,
                beslutningsdato = LocalDate.of(2023, 11, 10),
                mottattDato = LocalDate.of(2023, 11, 7),
                registrertDato = LocalDate.of(2023, 11, 7),
                utfall = ArenaUtfall.JA,
                antallDager = 5.0,
                opprinneligTomVedtaksperiode = LocalDate.of(2023, 11, 11),
                relatertTiltak = "Tiltak",
                antallBarn = 0,
            ),
            ArenaVedtakDTO(
                vedtakType = ArenaVedtakType.O,
                uttaksgrad = 100,
                fomVedtaksperiode = LocalDate.of(2023, 11, 7),
                tomVedtaksperiode = LocalDate.of(2023, 11, 10),
                status = ArenaVedtakStatus.IVERK,
                rettighettype = ArenaRettighet.BASI,
                aktivitetsfase = ArenaAktivitetFase.UGJEN,
                dagsats = 255,
                beslutningsdato = LocalDate.of(2023, 11, 10),
                mottattDato = LocalDate.of(2023, 11, 7),
                registrertDato = LocalDate.of(2023, 11, 7),
                utfall = ArenaUtfall.JA,
                antallDager = 5.0,
                opprinneligTomVedtaksperiode = LocalDate.of(2023, 11, 11),
                relatertTiltak = "Tiltak",
                antallBarn = 0,
            ),
        ),
    )

    private val sakMedVedtakHvorEttErÅpent = ArenaSakDTO(
        aar = 2022,
        lopenrSak = 11,
        status = ArenaSakStatus.AKTIV,
        ytelsestype = ArenaYtelse.INDIV,
        vedtak = listOf(
            ArenaVedtakDTO(
                vedtakType = ArenaVedtakType.O,
                uttaksgrad = 100,
                fomVedtaksperiode = LocalDate.of(2023, 11, 11),
                tomVedtaksperiode = LocalDate.of(2023, 11, 13),
                status = ArenaVedtakStatus.IVERK,
                rettighettype = ArenaRettighet.BASI,
                aktivitetsfase = ArenaAktivitetFase.UGJEN,
                dagsats = 255,
                beslutningsdato = LocalDate.of(2023, 11, 10),
                mottattDato = LocalDate.of(2023, 11, 7),
                registrertDato = LocalDate.of(2023, 11, 7),
                utfall = ArenaUtfall.JA,
                antallDager = 5.0,
                opprinneligTomVedtaksperiode = LocalDate.of(2023, 11, 11),
                relatertTiltak = "Tiltak",
                antallBarn = 0,
            ),
            ArenaVedtakDTO(
                vedtakType = ArenaVedtakType.O,
                uttaksgrad = 100,
                fomVedtaksperiode = LocalDate.of(2023, 11, 14),
                tomVedtaksperiode = LocalDate.of(2023, 11, 15),
                status = ArenaVedtakStatus.IVERK,
                rettighettype = ArenaRettighet.BASI,
                aktivitetsfase = ArenaAktivitetFase.UGJEN,
                dagsats = 255,
                beslutningsdato = LocalDate.of(2023, 11, 10),
                mottattDato = LocalDate.of(2023, 11, 7),
                registrertDato = LocalDate.of(2023, 11, 7),
                utfall = ArenaUtfall.JA,
                antallDager = 5.0,
                opprinneligTomVedtaksperiode = LocalDate.of(2023, 11, 11),
                relatertTiltak = "Tiltak",
                antallBarn = 0,
            ),
            ArenaVedtakDTO(
                vedtakType = ArenaVedtakType.O,
                uttaksgrad = 100,
                fomVedtaksperiode = LocalDate.of(2023, 11, 7),
                tomVedtaksperiode = null,
                status = ArenaVedtakStatus.IVERK,
                rettighettype = ArenaRettighet.BASI,
                aktivitetsfase = ArenaAktivitetFase.UGJEN,
                dagsats = 255,
                beslutningsdato = LocalDate.of(2023, 11, 10),
                mottattDato = LocalDate.of(2023, 11, 7),
                registrertDato = LocalDate.of(2023, 11, 7),
                utfall = ArenaUtfall.JA,
                antallDager = 5.0,
                opprinneligTomVedtaksperiode = LocalDate.of(2023, 11, 11),
                relatertTiltak = "Tiltak",
                antallBarn = 0,
            ),
        ),
    )

    private val sakUtenVedtak = ArenaSakDTO(
        aar = 2022,
        lopenrSak = 11,
        status = ArenaSakStatus.AKTIV,
        ytelsestype = ArenaYtelse.INDIV,
        vedtak = emptyList(),
    )

    @Test
    fun `harVedtakMedÅpenPeriode skal fungere for sak med vedtak som alle har sluttdato`() {
        sakMedVedtakSomAlleHarSluttDato.harVedtakMedÅpenPeriode() shouldBe false
    }

    @Test
    fun `harVedtakMedÅpenPeriode skal fungere for sak med vedtak hvor ett har åpen sluttdato`() {
        sakMedVedtakHvorEttErÅpent.harVedtakMedÅpenPeriode() shouldBe true
    }

    @Test
    fun `harVedtakMedÅpenPeriode skal fungere for sak uten vedtak`() {
        sakUtenVedtak.harVedtakMedÅpenPeriode() shouldBe false
    }

    @Test
    fun `filtrering på liste av saker skal fungere`() {
        val liste = listOf(
            sakUtenVedtak,
            sakHvorVedtakIkkeErIRekkefølge,
            sakMedVedtakHvorEttErÅpent,
            sakMedVedtakSomAlleHarSluttDato,
        )

        liste.kunSakerMedVedtakInnenforPeriode(
            fom = LocalDate.of(2023, 11, 2),
            tom = LocalDate.of(2023, 11, 6),
        ).size shouldBe 0

        liste.kunSakerMedVedtakInnenforPeriode(
            fom = LocalDate.of(2023, 11, 2),
            tom = LocalDate.of(2023, 11, 7),
        ).size shouldBe 3

        liste.kunSakerMedVedtakInnenforPeriode(
            fom = LocalDate.of(2023, 11, 20),
            tom = LocalDate.of(2023, 11, 29),
        ).size shouldBe 1
    }

    @Test
    fun `førsteFomVedtaksperiodeIsBefore skal fungere også når vedtak ikke er i rekkefølge`() {
        sakHvorVedtakIkkeErIRekkefølge.førsteFomVedtaksperiodeIsBeforeOrEqualTo(
            LocalDate.of(2023, 11, 11),
        ) shouldBe true
        sakHvorVedtakIkkeErIRekkefølge.førsteFomVedtaksperiodeIsBeforeOrEqualTo(
            LocalDate.of(2023, 11, 8),
        ) shouldBe true
        sakHvorVedtakIkkeErIRekkefølge.førsteFomVedtaksperiodeIsBeforeOrEqualTo(
            LocalDate.of(2023, 11, 15),
        ) shouldBe true
        sakHvorVedtakIkkeErIRekkefølge.førsteFomVedtaksperiodeIsBeforeOrEqualTo(
            LocalDate.of(2023, 11, 6),
        ) shouldBe false
        sakHvorVedtakIkkeErIRekkefølge.førsteFomVedtaksperiodeIsBeforeOrEqualTo(
            LocalDate.of(2023, 11, 7),
        ) shouldBe true
    }
}
