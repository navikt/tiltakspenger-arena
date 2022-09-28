package no.nav.tiltakspenger.arena.ytelser

import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.Dagpengekontrakt
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.Ytelseskontrakt
import java.time.LocalDate
import java.time.LocalDateTime

data class YtelseSakDTO(
    val fomGyldighetsperiode: LocalDateTime,
    val tomGyldighetsperiode: LocalDateTime,
    val datoKravMottatt: LocalDate?,
    val dataKravMottatt: String? = null,
    val fagsystemSakId: Int? = null,
    val status: YtelseSakStatus? = null,
    val ytelsestype: YtelseSakYtelsetype? = null,
    val vedtak: List<YtelseVedtakDTO> = emptyList(),
    val antallDagerIgjen: Int? = null,
    val antallUkerIgjen: Int? = null,
) {
    companion object {
        fun map(ytelser: List<Ytelseskontrakt>): List<YtelseSakDTO> =
            ytelser.map { ytelse ->
                if (ytelse is Dagpengekontrakt) {
                    YtelseSakDTO(
                        fomGyldighetsperiode = ytelse.fomGyldighetsperiode,
                        tomGyldighetsperiode = ytelse.tomGyldighetsperiode,
                        datoKravMottatt = ytelse.datoKravMottatt,
                        dataKravMottatt = ytelse.ytelsestype,
                        fagsystemSakId = ytelse.fagsystemSakId,
                        status = ytelse.status?.let { YtelseSakStatus.valueOf(it.uppercase()) },
                        ytelsestype = ytelse.ytelsestype?.let { YtelseSakYtelsetype.valueOf(it.uppercase()) },
                        vedtak = YtelseVedtakDTO.of(ytelse.ihtVedtak),
                        antallDagerIgjen = ytelse.antallDagerIgjen,
                        antallUkerIgjen = ytelse.antallUkerIgjen,
                    )
                } else {
                    YtelseSakDTO(
                        fomGyldighetsperiode = ytelse.fomGyldighetsperiode,
                        tomGyldighetsperiode = ytelse.tomGyldighetsperiode,
                        datoKravMottatt = ytelse.datoKravMottatt,
                        dataKravMottatt = ytelse.ytelsestype,
                        fagsystemSakId = ytelse.fagsystemSakId,
                        status = ytelse.status?.let { YtelseSakStatus.valueOf(it.uppercase()) },
                        ytelsestype = ytelse.ytelsestype?.let { YtelseSakYtelsetype.valueOf(it.uppercase()) },
                        vedtak = YtelseVedtakDTO.of(ytelse.ihtVedtak),
                    )
                }
            }
    }


    enum class YtelseSakStatus(val navn: String) {
        AKTIV("Aktiv"),
        AVSLU("Lukket"),
        INAKT("Inaktiv")
    }

    enum class YtelseSakYtelsetype(val navn: String) {
        AA("Arbeidsavklaringspenger"),
        DAGP("Dagpenger"),
        INDIV("Individstønad"),
    }
}
