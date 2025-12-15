package no.nav.tiltakspenger.arena.service.vedtakdetaljer

import java.time.LocalDate

data class VedtakDetaljerKunTiltakspenger(
    val antallDager: Double,
    val dagsats: Int,
    val tiltakGjennomføringsId: String,
    val rettighet: Rettighet,
    val vedtakId: Long,
    val sakId: Long,
    val beslutningsdato: LocalDate?,
    val saksnummer: String,
    val sakOpprettetDato: LocalDate,
    val sakStatus: String,
)
