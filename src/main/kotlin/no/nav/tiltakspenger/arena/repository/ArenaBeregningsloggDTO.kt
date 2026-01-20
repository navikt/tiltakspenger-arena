package no.nav.tiltakspenger.arena.repository

import java.time.LocalDate

data class ArenaBeregningsloggDTO(
    val meldekortId: String,
    val regDato: LocalDate,
    val rettighetsnavn: String,
    val beregningstatusnavn: String,
    val vedtakId: Int?,
    val datoFra: LocalDate,
    val datoTil: LocalDate,
)
