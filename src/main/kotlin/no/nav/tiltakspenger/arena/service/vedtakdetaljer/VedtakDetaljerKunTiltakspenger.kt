package no.nav.tiltakspenger.arena.service.vedtakdetaljer

data class VedtakDetaljerKunTiltakspenger(
    val antallDager: Double,
    val dagsats: Int,
    val relaterteTiltak: String,
    val rettighet: Rettighet,
    val vedtakId: Long,
    val sakId: Long,
)
