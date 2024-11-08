package no.nav.tiltakspenger.arena.service.vedtakdetaljer

data class VedtakDetaljerKunTiltakspenger(
    val antallDager: Double,
    val dagsats: Int,
    val tiltakGjennomføringsId: String,
    val rettighet: Rettighet,
    val vedtakId: Long,
    val sakId: Long,
)
