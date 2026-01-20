package no.nav.tiltakspenger.arena.repository

import java.time.LocalDate

data class ArenaAnmerkningMedPeriodeDTO(
    val meldekortId: String,
    val regDato: LocalDate,
    val rettighetnavn: String,
    val beregningstatusnavn: String,
    val vedtakId: Int?,
    val datoFra: LocalDate,
    val datoTil: LocalDate,
)
