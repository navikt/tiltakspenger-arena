package no.nav.tiltakspenger.arena.repository

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Dokumentasjon https://confluence.adeo.no/x/W4NQBw
 */
class ArenaMeldekortDTO(
    val meldekortId: String,
    val personId: Long,
    val datoInnkommet: LocalDate?,      // mottatt
    val statusArbeidet: String,         // J/N, J hvis arbeidetTimer > 0
    val statusKurs: String,             // J/N
    val statusFerie: String,            // J/N
    val statusSyk: String,              // J/N
    val statusAnnetFravaer: String,     // J/N
    val regDato: LocalDateTime,         // opprettet
    val modDato: LocalDateTime,         // sist endret
    val mksKortKode: MKSKortKode,       // Typen meldekort.. blant annet for å utlede om meldekort er korrigert (10 - med samme periode eller henvisning(?) til dette)
    val beregningstatusKode: BeregningStatusKode,
    val aar: Int,
    val periodekode: Int,
    val meldekortperiode: ArenaMeldekortperiodeDTO,
    val dager: List<ArenaMeldekortDagDTO>,
) {
    enum class BeregningStatusKode {
        FERDI,
        KLAR,
        OPPRE,
        FEIL,
        IKKE,
        NYKTR,
        OVERM,
        VENTE
    }

    enum class MKSKortKode(kode: String) {
        VANLIG_MELDEKORT("05"),
        UKJENT_FOR_MEG_2("07"), // Aner ikke, kan være Papir?
        ETTERREGISTRERING("09"),
        KORRIGERING("10"),
        ;
    }
}
