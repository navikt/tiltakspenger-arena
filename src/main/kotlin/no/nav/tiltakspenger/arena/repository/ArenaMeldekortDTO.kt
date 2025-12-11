package no.nav.tiltakspenger.arena.repository

import kotlinx.datetime.LocalDate

/**
 * Dokumentasjon https://confluence.adeo.no/x/W4NQBw
 */
class ArenaMeldekortDTO(
    val meldekortId: String,
    val personId: String,
    val datoInnkommet: LocalDate,       // mottatt
    val datoFra: LocalDate,             // fra og med
    val datoTil: LocalDate,             // til og med
    val statusArbeidet: Boolean,        // true hvis arbeidetTimer > 0
    val statusKurs: Boolean,
    val statusFerie: Boolean,
    val statusSyk: Boolean,
    val statusAnnetFravaer: Boolean,
    val regDato: LocalDate,             // opprettet
    val modDato: LocalDate,             // sist endret
    val mksKortKode: String,            // Typen meldekort.. blant annet for å utlede om meldekort er korrigert (10 - med samme periode eller henvisning(?) til dette)
    val beregningstatusKode: String,    // KLAR, FERDI/...?
    ) {
}
