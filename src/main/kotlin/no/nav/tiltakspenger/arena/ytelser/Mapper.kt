package no.nav.tiltakspenger.arena.ytelser

import no.nav.tiltakspenger.arena.ytelse.ArenaYtelseResponsDTO
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.Dagpengekontrakt
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.Vedtak
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.Ytelseskontrakt

fun mapRespons(ytelser: List<Ytelseskontrakt>): ArenaYtelseResponsDTO =
    ArenaYtelseResponsDTO(saker = ytelser.map { mapSak(it) })

fun mapSak(ytelse: Ytelseskontrakt): ArenaYtelseResponsDTO.SakDTO =
    if (ytelse is Dagpengekontrakt) {
        ArenaYtelseResponsDTO.SakDTO(
            gyldighetsperiodeFom = ytelse.fomGyldighetsperiode,
            gyldighetsperiodeTom = ytelse.tomGyldighetsperiode,
            kravMottattDato = ytelse.datoKravMottatt,
            fagsystemSakId = ytelse.fagsystemSakId,
            status = ytelse.status?.let { mapSakStatusType(it) },
            sakType = ytelse.ytelsestype?.let { mapSakType(it) },
            vedtak = mapVedtak(ytelse.ihtVedtak),
            antallDagerIgjen = ytelse.antallDagerIgjen,
            antallUkerIgjen = ytelse.antallUkerIgjen,
        )
    } else {
        ArenaYtelseResponsDTO.SakDTO(
            gyldighetsperiodeFom = ytelse.fomGyldighetsperiode,
            gyldighetsperiodeTom = ytelse.tomGyldighetsperiode,
            kravMottattDato = ytelse.datoKravMottatt,
            fagsystemSakId = ytelse.fagsystemSakId,
            status = ytelse.status?.let { mapSakStatusType(it) },
            sakType = ytelse.ytelsestype?.let { mapSakType(it) },
            vedtak = mapVedtak(ytelse.ihtVedtak),
        )
    }


fun mapVedtak(vedtakListe: List<Vedtak>): List<ArenaYtelseResponsDTO.VedtakDTO> =
    vedtakListe.map { vedtak ->
        ArenaYtelseResponsDTO.VedtakDTO(
            beslutningsDato = vedtak.beslutningsdato,
            vedtakType = vedtak.periodetypeForYtelse?.let { mapVedtakType(it) },
            vedtaksperiodeFom = vedtak.vedtaksperiode.fom,
            vedtaksperiodeTom = vedtak.vedtaksperiode.tom,
            rettighetType = vedtak.vedtakstype?.let { mapRettighetType(it) },
            status = vedtak.status?.let { mapVedtakStatusType(it) }
        )
    }


fun mapVedtakStatusType(n: String): ArenaYtelseResponsDTO.VedtakStatusType =
    ArenaYtelseResponsDTO.VedtakStatusType.values().firstOrNull { it.navn == n }
        ?: throw IllegalArgumentException("Ukjent VedtakStatusType $n")

fun mapRettighetType(n: String): ArenaYtelseResponsDTO.RettighetType {
    val faktiskNavn = n.substringBeforeLast('/').trim()
    return ArenaYtelseResponsDTO.RettighetType.values().firstOrNull { it.navn == faktiskNavn }
        ?: throw IllegalArgumentException("Ukjent RettighetType $n (trodde det var $faktiskNavn")
}

fun mapVedtakType(n: String): ArenaYtelseResponsDTO.VedtakType =
    ArenaYtelseResponsDTO.VedtakType.values().firstOrNull { it.navn == n }
        ?: throw IllegalArgumentException("Ukjent VedtakType $n")

fun mapSakStatusType(n: String): ArenaYtelseResponsDTO.SakStatusType =
    ArenaYtelseResponsDTO.SakStatusType.values().firstOrNull { it.navn == n }
        ?: throw IllegalArgumentException("Ukjent SakStatusType $n")

fun mapSakType(n: String): ArenaYtelseResponsDTO.SakType =
    ArenaYtelseResponsDTO.SakType.values().firstOrNull { it.navn == n }
        ?: throw IllegalArgumentException("Ukjent SakType $n")
