package no.nav.tiltakspenger.arena.service.vedtakdetaljer

import java.time.LocalDate

data class VedtakDetaljer(
    val antallDager: Double,
    val dagsatsTiltakspenger: Int,
    val dagsatsBarnetillegg: Int,
    val antallBarn: Int,
    val tiltakGjennomføringsId: String,
    val rettighet: Rettighet,
    val vedtakId: Long,
    val sakId: Long,
    val beslutningsdato: LocalDate?,
    val sak: Sak,
) {
    data class Sak(
        val saksnummer: String,
        val opprettetDato: LocalDate,
        val status: String,
    )
}
