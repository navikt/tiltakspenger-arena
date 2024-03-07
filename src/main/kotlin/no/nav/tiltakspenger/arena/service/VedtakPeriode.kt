package no.nav.tiltakspenger.arena.service

enum class Rettighet {
    TILTAKSPENGER,
    BARNETILLEGG,
    TILTAKSPENGER_OG_BARNETILLEGG,
    INGENTING,
}

data class VedtakDetaljer(
    val antallDager: Double,
    val dagsatsTiltakspenger: Int,
    val dagsatsBarnetillegg: Int,
    val antallBarn: Int,
    val relaterteTiltak: String,
    val rettighet: Rettighet,
)

data class VedtakDetaljerUtenBarnetillegg(
    val antallDager: Double,
    val dagsats: Int,
    val relaterteTiltak: String,
    val rettighet: Rettighet,
)

data class VedtakDetaljerBarnetillegg(
    val antallDager: Double,
    val dagsats: Int,
    val antallBarn: Int,
    val relaterteTiltak: String,
    val rettighet: Rettighet,
)
