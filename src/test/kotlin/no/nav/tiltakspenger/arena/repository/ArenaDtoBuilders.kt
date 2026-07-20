package no.nav.tiltakspenger.arena.repository

import no.nav.tiltakspenger.arena.repository.sak.ArenaSakDTO
import no.nav.tiltakspenger.arena.repository.vedtak.ArenaBarnetilleggVedtakDTO
import no.nav.tiltakspenger.arena.repository.vedtak.ArenaTiltakspengerVedtakDTO
import java.time.LocalDate

/**
 * Komponerbare in-memory-buildere for repository-DTO-ene (ikke DB-seeding — det ligger i [ArenaTestdata]).
 * Defaultene beskriver et standard, gjeldende tiltakspenger-vedtak; en test overstyrer kun det den faktisk verifiserer (typisk fom/tom).
 */
fun arenaTiltakspengerVedtakDTO(
    vedtakId: Long,
    tilhørendeSakId: Long = 0L,
    fom: LocalDate = LocalDate.of(2023, 11, 14),
    tom: LocalDate? = LocalDate.of(2023, 11, 15),
    vedtakType: ArenaVedtakType = ArenaVedtakType.O,
    status: ArenaVedtakStatus = ArenaVedtakStatus.IVERK,
    rettighettype: ArenaRettighet = ArenaRettighet.BASI,
    utfall: ArenaUtfall = ArenaUtfall.JA,
    dagsats: Int? = 255,
    antallDager: Double? = 5.0,
): ArenaTiltakspengerVedtakDTO =
    ArenaTiltakspengerVedtakDTO(
        vedtakId = vedtakId,
        tilhørendeSakId = tilhørendeSakId,
        vedtakType = vedtakType,
        uttaksgrad = 100,
        fomVedtaksperiode = fom,
        tomVedtaksperiode = tom,
        status = status,
        rettighettype = rettighettype,
        aktivitetsfase = ArenaAktivitetFase.UGJEN,
        dagsats = dagsats,
        beslutningsdato = LocalDate.of(2023, 11, 10),
        mottattDato = LocalDate.of(2023, 11, 7),
        registrertDato = LocalDate.of(2023, 11, 7),
        utfall = utfall,
        antallDager = antallDager,
        opprinneligTomVedtaksperiode = LocalDate.of(2023, 11, 11),
        relatertTiltak = "Tiltak",
    )

fun arenaBarnetilleggVedtakDTO(
    vedtakId: Long,
    tilhørendeSakId: Long = 0L,
    fom: LocalDate = LocalDate.of(2023, 11, 14),
    tom: LocalDate? = LocalDate.of(2023, 11, 15),
    dagsats: Int? = 53,
    antallDager: Double? = 5.0,
    antallBarn: Int? = 1,
): ArenaBarnetilleggVedtakDTO =
    ArenaBarnetilleggVedtakDTO(
        vedtakId = vedtakId,
        tilhørendeSakId = tilhørendeSakId,
        vedtakType = ArenaVedtakType.O,
        uttaksgrad = 100,
        fomVedtaksperiode = fom,
        tomVedtaksperiode = tom,
        status = ArenaVedtakStatus.IVERK,
        rettighettype = ArenaRettighet.BTIL,
        aktivitetsfase = ArenaAktivitetFase.UGJEN,
        dagsats = dagsats,
        beslutningsdato = LocalDate.of(2023, 11, 10),
        mottattDato = LocalDate.of(2023, 11, 7),
        registrertDato = LocalDate.of(2023, 11, 7),
        utfall = ArenaUtfall.JA,
        antallDager = antallDager,
        opprinneligTomVedtaksperiode = LocalDate.of(2023, 11, 11),
        tiltakGjennomføringsId = "Tiltak",
        antallBarn = antallBarn,
    )

fun arenaSakDTO(
    sakId: Long,
    aar: Int = 2022,
    lopenrSak: Long = 11L,
    status: ArenaSakStatus = ArenaSakStatus.AKTIV,
    ytelsestype: ArenaYtelse = ArenaYtelse.INDIV,
    opprettetDato: LocalDate = LocalDate.of(2022, 1, 8),
    tiltakspengerVedtak: List<ArenaTiltakspengerVedtakDTO> = emptyList(),
    barnetilleggVedtak: List<ArenaBarnetilleggVedtakDTO> = emptyList(),
): ArenaSakDTO =
    ArenaSakDTO(
        sakId = sakId,
        aar = aar,
        lopenrSak = lopenrSak,
        status = status,
        ytelsestype = ytelsestype,
        opprettetDato = opprettetDato,
        tiltakspengerVedtak = tiltakspengerVedtak,
        barnetilleggVedtak = barnetilleggVedtak,
    )
