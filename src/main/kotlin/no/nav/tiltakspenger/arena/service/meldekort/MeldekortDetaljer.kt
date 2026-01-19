package no.nav.tiltakspenger.arena.service.meldekort

import no.nav.tiltakspenger.arena.repository.ArenaMeldekortDTO
import java.time.LocalDate
import java.time.LocalDateTime

class MeldekortDetaljer(
    val meldekortId: String,
    val mottatt: LocalDate?,
    val arbeidet: Boolean,
    val kurs: Boolean,
    val ferie: Boolean,
    val syk: Boolean,
    val annetFravaer: Boolean,
    val registrert: LocalDateTime,
    val sistEndret: LocalDateTime,
    val type: String,
    val status: String,
    val statusDato: LocalDate,
    val meldegruppe: String,
    val aar: Int,
    val totaltArbeidetTimer: Int,
    val periode: MeldekortperiodeDetaljer,
    val dager: List<MeldekortDagDetaljer>,
)

fun ArenaMeldekortDTO.tilMeldekortDetaljer(): MeldekortDetaljer {
    val totaltArbeidetTimer = this.dager.sumOf { it.arbeidetTimer }

    return MeldekortDetaljer(
        meldekortId = this.meldekortId,
        mottatt = this.datoInnkommet,
        arbeidet = this.statusArbeidet.tilBooleanArena(),
        kurs = this.statusKurs.tilBooleanArena(),
        ferie = this.statusFerie.tilBooleanArena(),
        syk = this.statusSyk.tilBooleanArena(),
        annetFravaer = this.statusAnnetFravaer.tilBooleanArena(),
        registrert = this.regDato,
        sistEndret = this.modDato,
        type = this.meldekortType,
        status = this.beregningstatusnavn,
        statusDato = this.hendelsedato,
        meldegruppe = this.meldegruppenavn,
        aar = this.aar,
        totaltArbeidetTimer = totaltArbeidetTimer,
        periode = this.meldekortperiode.tilMeldekortperiodeDetaljer(),
        dager = this.dager.map { it.tilMeldekortDagDetaljer() },
    )
}
