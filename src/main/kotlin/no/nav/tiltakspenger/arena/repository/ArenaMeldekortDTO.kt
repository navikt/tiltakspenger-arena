package no.nav.tiltakspenger.arena.repository

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
    val mksKortKode: MKSKortKode,
    val beregningstatusKode: BeregningStatusKode,
    val aar: Int,
    val totaltArbeidetTimer: Int,
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
        VENTE,
    }

    /**
     * 05 - Elektronisk (Elektronisk kort)
     * 06 - Automatisk utfylt (Automatisk kort)
     * 07 - Manuelt - ordinær (Manuelt kort)
     * 09 - Manuelt - Korrigering (Manuelt kort – opprettet av saksbehandler eller kort som opprettes tilbake i tid)
     * 10 - Elektronisk - Korrigering (Elektronisk kort - korrigert av bruker)
     *
     * Det er 05, 06, 07 som stammer fra meldeformtypene EMELD - Elektronisk, AUTO - Automatisk, MANU - Manuell
     * Papirkort ble sist benyttet i 2020, er av type 01 , 03 og  04. 01 gjelder meldeform PAPIR - Papir
     *
     * https://nav-it.slack.com/archives/C09TZJD8UN5/p1767786698799009
     */
    enum class MKSKortKode(kode: String) {
        ELEKTRONISK("05"),
        AUTOMATISK_UTFYLT("06"),
        MANUELT_ORDINÆR("07"),
        MANUELT_KORRIGERING("09"),
        ELEKTRONISK_KORRIGERING("10"),
    }
}
