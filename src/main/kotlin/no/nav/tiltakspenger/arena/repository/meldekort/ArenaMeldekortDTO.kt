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
    val regDato: LocalDateTime, // opprettet
    val modDato: LocalDateTime, // sist endret
    val meldekortType: String,
    val beregningstatusnavn: String,
    val hendelsedato: LocalDate,
    val meldegruppenavn: String,
    val aar: Int,
    val meldekortperiode: ArenaMeldekortperiodeDTO,
    val dager: List<ArenaMeldekortDagDTO>,
)
