package no.nav.tiltakspenger.arena.repository.beregningslogg

import java.time.LocalDate

data class ArenaBeregningsloggDTO(
    val meldekortId: Long?,
    val regDato: LocalDate,
    val rettighetsnavn: String,
    val beregningstatusnavn: String,
    val vedtakId: Long?,
    val datoFra: LocalDate,
    val datoTil: LocalDate,
)
