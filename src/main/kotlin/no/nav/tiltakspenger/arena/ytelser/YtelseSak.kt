package no.nav.tiltakspenger.arena.ytelser

import no.nav.tiltakspenger.arena.felles.toLocalDate
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.Ytelseskontrakt
import java.time.LocalDate

data class YtelseSak(
    val datoKravMottatt: LocalDate?,
    val dataKravMottatt: String? = null,
    val fagsystemSakId: Int? = null,
    val status: String? = null,
    val ytelsestype: String? = null,
    val vedtak: List<YtelseVedtak> = emptyList(),
) {
    companion object {
        fun of(ytelser: List<Ytelseskontrakt>): List<YtelseSak> =
            ytelser.map { ytelse ->
                YtelseSak(
                    datoKravMottatt = ytelse.datoKravMottatt.toLocalDate(),
                    dataKravMottatt = ytelse.ytelsestype,
                    fagsystemSakId = ytelse.fagsystemSakId,
                    status = ytelse.status,
                    ytelsestype = ytelse.ytelsestype,
                    vedtak = YtelseVedtak.of(ytelse.ihtVedtak)
                )
            }
    }
}
