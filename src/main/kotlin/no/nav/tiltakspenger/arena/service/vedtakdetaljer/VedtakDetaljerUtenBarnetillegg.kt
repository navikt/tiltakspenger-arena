package no.nav.tiltakspenger.arena.service.vedtakdetaljer

data class VedtakDetaljerUtenBarnetillegg(
    val antallDager: Double,
    val dagsats: Int,
    val relaterteTiltak: String,
    val rettighet: Rettighet,
)
