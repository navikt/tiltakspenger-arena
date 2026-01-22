package no.nav.tiltakspenger.arena.service.meldekort

import no.nav.tiltakspenger.arena.repository.meldekort.ArenaMeldekortperiodeDTO
import java.time.LocalDate

class MeldekortperiodeDetaljer(
    val aar: Int,
    val periodekode: Int,
    val ukenrUke1: Int,
    val ukenrUke2: Int,
    val fraOgMed: LocalDate,
    val tilOgMed: LocalDate,
)

fun ArenaMeldekortperiodeDTO.tilMeldekortperiodeDetaljer(): MeldekortperiodeDetaljer {
    return MeldekortperiodeDetaljer(
        aar = this.aar,
        periodekode = this.periodekode,
        ukenrUke1 = this.ukenrUke1,
        ukenrUke2 = this.ukenrUke2,
        fraOgMed = this.datoFra,
        tilOgMed = this.datoTil,
    )
}
