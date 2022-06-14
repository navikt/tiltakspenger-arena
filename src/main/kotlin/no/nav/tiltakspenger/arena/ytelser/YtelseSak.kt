package no.nav.tiltakspenger.arena.ytelser

import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.Ytelseskontrakt

data class YtelseSak(
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
                    dataKravMottatt = ytelse.ytelsestype,
                    fagsystemSakId = ytelse.fagsystemSakId,
                    status = ytelse.status,
                    ytelsestype = ytelse.ytelsestype,
                    vedtak = YtelseVedtak.of(ytelse.ihtVedtak)
                )
            }
    }
}
