package no.nav.tiltakspenger.arena.repository

import java.time.LocalDate

data class ArenaBeregnetMeldekortMedFeilDTO(
    val meldekortId: String,
    val modDato: LocalDate,
    val meldekortkodenavn: String,
    val beregningstatusnavn: String,
    val datoFra: LocalDate,
    val datoTil: LocalDate,
)
