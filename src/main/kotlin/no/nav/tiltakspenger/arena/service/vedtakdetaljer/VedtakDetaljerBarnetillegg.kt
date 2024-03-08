package no.nav.tiltakspenger.arena.service.vedtakdetaljer

data class VedtakDetaljerBarnetillegg(
    val antallDager: Double,
    val dagsats: Int,
    val antallBarn: Int,
    val relaterteTiltak: String,
    val rettighet: Rettighet,
)
