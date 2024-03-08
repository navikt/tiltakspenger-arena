package no.nav.tiltakspenger.arena.service.vedtakdetaljer

data class VedtakDetaljer(
    val antallDager: Double,
    val dagsatsTiltakspenger: Int,
    val dagsatsBarnetillegg: Int,
    val antallBarn: Int,
    val relaterteTiltak: String,
    val rettighet: Rettighet,
)
