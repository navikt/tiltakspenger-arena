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
)
