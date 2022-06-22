package no.nav.tiltakspenger.arena.ytelser

import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.Dagpengekontrakt
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.Ytelseskontrakt
import java.time.LocalDate
import java.time.LocalDateTime

data class YtelseSak(
    val fomGyldighetsperiode: LocalDateTime,
    val tomGyldighetsperiode: LocalDateTime,
    val datoKravMottatt: LocalDate?,
    val dataKravMottatt: String? = null,
    val fagsystemSakId: Int? = null,
    val status: String? = null,
    val ytelsestype: String? = null,
    val vedtak: List<YtelseVedtak> = emptyList(),
    val antallDagerIgjen: Int? = null,
    val antallUkerIgjen: Int? = null,
) {
    companion object {
        fun map(ytelser: List<Ytelseskontrakt>): List<YtelseSak> =
            ytelser.map { ytelse ->
                if (ytelse is Dagpengekontrakt) {
                    YtelseSak(
                        fomGyldighetsperiode = ytelse.fomGyldighetsperiode,
                        tomGyldighetsperiode = ytelse.tomGyldighetsperiode,
                        datoKravMottatt = ytelse.datoKravMottatt,
                        dataKravMottatt = ytelse.ytelsestype,
                        fagsystemSakId = ytelse.fagsystemSakId,
                        status = ytelse.status,
                        ytelsestype = ytelse.ytelsestype,
                        vedtak = YtelseVedtak.of(ytelse.ihtVedtak),
                        antallDagerIgjen = ytelse.antallDagerIgjen,
                        antallUkerIgjen = ytelse.antallUkerIgjen,
                    )
                } else {
                    YtelseSak(
                        fomGyldighetsperiode = ytelse.fomGyldighetsperiode,
                        tomGyldighetsperiode = ytelse.tomGyldighetsperiode,
                        datoKravMottatt = ytelse.datoKravMottatt,
                        dataKravMottatt = ytelse.ytelsestype,
                        fagsystemSakId = ytelse.fagsystemSakId,
                        status = ytelse.status,
                        ytelsestype = ytelse.ytelsestype,
                        vedtak = YtelseVedtak.of(ytelse.ihtVedtak),
                    )
                }
            }
    }
}
