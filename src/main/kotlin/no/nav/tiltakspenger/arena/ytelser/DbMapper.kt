package no.nav.tiltakspenger.arena.ytelser

import no.nav.tiltakspenger.arena.repository.ArenaSakDTO
import no.nav.tiltakspenger.arena.repository.ArenaVedtakDTO
import no.nav.tiltakspenger.libs.arena.ytelse.ArenaYtelseResponsDTO

fun mapArenaYtelserFraDB(saker: List<ArenaSakDTO>): ArenaYtelseResponsDTO =
    ArenaYtelseResponsDTO(saker = saker.map { mapSakFraDB(it) })

fun mapSakFraDB(sak: ArenaSakDTO): ArenaYtelseResponsDTO.SakDTO =
    ArenaYtelseResponsDTO.SakDTO(
        gyldighetsperiodeFom = sak.fomGyldighetsperiode,
        gyldighetsperiodeTom = sak.tomGyldighetsperiode,
        kravMottattDato = sak.datoKravMottatt,
        fagsystemSakId = sak.fagsystemSakId,
        status = ArenaYtelseResponsDTO.SakStatusType.valueOf(sak.status.name),
        sakType = ArenaYtelseResponsDTO.SakType.valueOf(sak.ytelsestype.name),
        vedtak = mapVedtakFraDB(sak.ihtVedtak),
    )

fun mapVedtakFraDB(vedtakListe: List<ArenaVedtakDTO>): List<ArenaYtelseResponsDTO.VedtakDTO> =
    vedtakListe.map { vedtak ->
        ArenaYtelseResponsDTO.VedtakDTO(
            beslutningsDato = vedtak.beslutningsdato,
            vedtakType = ArenaYtelseResponsDTO.VedtakType.valueOf(vedtak.vedtakType.name),
            vedtaksperiodeFom = vedtak.fomVedtaksperiode,
            vedtaksperiodeTom = vedtak.tomVedtaksperiode,
            rettighetType = ArenaYtelseResponsDTO.RettighetType.valueOf(vedtak.rettighettype.name),
            status = ArenaYtelseResponsDTO.VedtakStatusType.valueOf(vedtak.status.name),
        )
    }
