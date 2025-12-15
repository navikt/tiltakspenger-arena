package no.nav.tiltakspenger.arena.repository

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ArenaSakDTOTest {

    private val sakHvorVedtakIkkeErIRekkefølge = ArenaSakDTO(
        sakId = 1000L,
        aar = 2022,
        lopenrSak = 11,
        status = ArenaSakStatus.AKTIV,
        ytelsestype = ArenaYtelse.INDIV,
        opprettetDato = LocalDate.of(2022, 1, 8),
        tiltakspengerVedtak = listOf(
            ArenaTiltakspengerVedtakDTO(
                vedtakId = 1L,
                tilhørendeSakId = 1000L,
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
            ),
            ArenaTiltakspengerVedtakDTO(
                vedtakId = 2L,
                tilhørendeSakId = 1000L,
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
            ),
            ArenaTiltakspengerVedtakDTO(
                vedtakId = 3L,
                tilhørendeSakId = 1000L,
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
            ),
        ),
        barnetilleggVedtak = emptyList(),
    )

    private val sakMedVedtakSomAlleHarSluttDato = ArenaSakDTO(
        sakId = 1001L,
        aar = 2022,
        lopenrSak = 11,
        status = ArenaSakStatus.AKTIV,
        ytelsestype = ArenaYtelse.INDIV,
        opprettetDato = LocalDate.of(2022, 1, 8),
        tiltakspengerVedtak = listOf(
            ArenaTiltakspengerVedtakDTO(
                vedtakId = 11L,
                tilhørendeSakId = 1001L,
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
            ),
            ArenaTiltakspengerVedtakDTO(
                vedtakId = 12L,
                tilhørendeSakId = 1001L,
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
            ),
            ArenaTiltakspengerVedtakDTO(
                vedtakId = 13L,
                tilhørendeSakId = 1001L,
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
            ),
        ),
        barnetilleggVedtak = emptyList(),
    )

    private val sakMedVedtakHvorEttErÅpent = ArenaSakDTO(
        sakId = 1002L,
        aar = 2022,
        lopenrSak = 11,
        status = ArenaSakStatus.AKTIV,
        ytelsestype = ArenaYtelse.INDIV,
        opprettetDato = LocalDate.of(2022, 1, 8),
        tiltakspengerVedtak = listOf(
            ArenaTiltakspengerVedtakDTO(
                vedtakId = 20L,
                tilhørendeSakId = 1003L,
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
            ),
            ArenaTiltakspengerVedtakDTO(
                vedtakId = 21L,
                tilhørendeSakId = 1003L,
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
            ),
            ArenaTiltakspengerVedtakDTO(
                vedtakId = 22L,
                tilhørendeSakId = 1003L,
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
            ),
        ),
        barnetilleggVedtak = emptyList(),
    )

    private val sakUtenVedtak = ArenaSakDTO(
        sakId = 1004L,
        aar = 2022,
        lopenrSak = 11,
        status = ArenaSakStatus.AKTIV,
        ytelsestype = ArenaYtelse.INDIV,
        opprettetDato = LocalDate.of(2022, 1, 8),
        tiltakspengerVedtak = emptyList(),
        barnetilleggVedtak = emptyList(),
    )

    @Test
    fun `harVedtakMedÅpenPeriode skal fungere for sak med vedtak som alle har sluttdato`() {
        sakMedVedtakSomAlleHarSluttDato.harTiltakspengerVedtakMedÅpenPeriode() shouldBe false
    }

    @Test
    fun `harVedtakMedÅpenPeriode skal fungere for sak med vedtak hvor ett har åpen sluttdato`() {
        sakMedVedtakHvorEttErÅpent.harTiltakspengerVedtakMedÅpenPeriode() shouldBe true
    }

    @Test
    fun `harVedtakMedÅpenPeriode skal fungere for sak uten vedtak`() {
        sakUtenVedtak.harTiltakspengerVedtakMedÅpenPeriode() shouldBe false
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
}
