package no.nav.tiltakspenger.arena.repository.meldekort

import java.time.LocalDate

data class ArenaBeregnetMeldekortMedFeilDTO(
    val meldekortId: Long,
    val modDato: LocalDate,
    val meldekortkodenavn: String,
    val beregningstatusnavn: String,
    val datoFra: LocalDate,
    val datoTil: LocalDate,
)
