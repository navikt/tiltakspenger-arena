package no.nav.tiltakspenger.arena.repository.meldekort

import java.time.LocalDateTime

class ArenaMeldekortDagDTO(
    val meldekortId: String,
    val ukeNr: Int,
    val dagNr: Int, // Hvilken dag i uka (1-7)
    val statusArbeidsdag: String, // J/N
    val statusFerie: String?, // J/N/NULL
    val statusKurs: String, // J/N
    val statusSyk: String, // J/N
    val statusAnnetfravaer: String, // J/N
    val regUser: String, // opprettetAv (GRENSESN = bruker).
    val regDato: LocalDateTime, // opprettetDato.
    val arbeidetTimer: Int,
)
