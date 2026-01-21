package no.nav.tiltakspenger.arena.repository

import java.time.LocalDate

data class ArenaAnmerkningMedPeriodeDTO(
    val meldekortId: Long?,
    val regDato: LocalDate,
    val rettighetnavn: String,
    val beregningstatusnavn: String,
    val vedtakId: Long?,
    val datoFra: LocalDate,
    val datoTil: LocalDate,
)
