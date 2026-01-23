package no.nav.tiltakspenger.arena.service.meldekort

import no.nav.tiltakspenger.arena.repository.meldekort.ArenaMeldekortDagDTO
import java.time.LocalDateTime

class MeldekortDagDetaljer(
    val ukeNr: Int,
    val dagNr: Int,
    val arbeidsdag: Boolean,
    val ferie: Boolean?,
    val kurs: Boolean,
    val syk: Boolean,
    val annetfravaer: Boolean,
    val registrertAv: String,
    val registrert: LocalDateTime,
    val arbeidetTimer: Int,
)

fun ArenaMeldekortDagDTO.tilMeldekortDagDetaljer(): MeldekortDagDetaljer {
    return MeldekortDagDetaljer(
        ukeNr = this.ukeNr,
        dagNr = this.dagNr,
        arbeidsdag = this.statusArbeidsdag.tilBooleanArena(),
        ferie = this.statusFerie?.tilBooleanArena(),
        kurs = this.statusKurs.tilBooleanArena(),
        syk = this.statusSyk.tilBooleanArena(),
        annetfravaer = this.statusAnnetfravaer.tilBooleanArena(),
        registrertAv = this.regUser,
        registrert = this.regDato,
        arbeidetTimer = this.arbeidetTimer,
    )
}
