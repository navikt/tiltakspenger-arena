package no.nav.tiltakspenger.arena.repository.meldekort

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Dokumentasjon https://confluence.adeo.no/x/W4NQBw
 */
class ArenaMeldekortDTO(
    val meldekortId: String,
    val datoInnkommet: LocalDate?, // mottatt
    val statusArbeidet: String, // J/N, J hvis arbeidetTimer > 0
    val statusKurs: String, // J/N
    val statusFerie: String, // J/N
    val statusSyk: String, // J/N
    val statusAnnetFravaer: String, // J/N
    val statusFortsattArbeidsoker: String, // J/N
    val regDato: LocalDateTime, // opprettet
    // TODO: MOD_DATO er nullbar i Arena, men mappes som non-null her - gjør feltet nullbart
    //  gjennom hele kjeden (jf. TODO-en på hendelsedato).
    val modDato: LocalDateTime, // sist endret
    val meldekortType: String,
    val beregningstatusnavn: String,
    // TODO: HENDELSEDATO kommer fra en LEFT JOIN mot MELDELOGG og kan mangle - da feiler
    //  mappingen med NPE (dokumentert i MeldekortRepositoryTest). arena-api, som dette API-et
    //  er modellert etter, har statusDato som valgfritt felt. Planen er å gjøre både denne og
    //  modDato nullbare gjennom hele kjeden (MeldekortDetaljer -> datadeling-klienten ->
    //  ArenaMeldekortResponse) og endre kontrakten mot NKS (statusDato ut av required i
    //  datadelings ArenaMeldekort.yaml) rett før.
    val hendelsedato: LocalDate,
    val meldegruppenavn: String,
    val aar: Int,
    val meldekortperiode: ArenaMeldekortperiodeDTO,
    val dager: List<ArenaMeldekortDagDTO>,
)
