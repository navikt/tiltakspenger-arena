package no.nav.tiltakspenger.arena.repository

import io.github.oshai.kotlinlogging.KotlinLogging
import kotliquery.Row
import kotliquery.TransactionalSession
import kotliquery.queryOf
import java.time.LocalDate

class MeldekortDAO(
    private val meldekortdagDAO: MeldekortdagDAO = MeldekortdagDAO(),
) {
    private val logger = KotlinLogging.logger {}

    fun findByPersonId(
        personId: Long,
        fraOgMedDato: LocalDate,
        tilOgMedDato: LocalDate,
        txSession: TransactionalSession,
    ): List<ArenaMeldekortDTO> {
        return txSession.run(
            action = queryOf(
                //language=SQL
                statement =
                """
                        SELECT 
                            m.MELDEKORT_ID            AS MELDEKORT_ID,
                            m.DATO_INNKOMMET          AS DATO_INNKOMMET,
                            m.STATUS_ARBEIDET         AS STATUS_ARBEIDET,
                            m.STATUS_KURS             AS STATUS_KURS,
                            m.STATUS_FERIE            AS STATUS_FERIE,
                            m.STATUS_SYK              AS STATUS_SYK,
                            m.STATUS_ANNETFRAVAER     AS STATUS_ANNETFRAVAER,
                            m.REG_DATO                AS REG_DATO,
                            m.MOD_DATO                AS MOD_DATO,
                            mt.MKSKORTTYPENAVN        AS MKSKORTTYPENAVN,
                            be.BEREGNINGSTATUSNAVN    AS BEREGNINGSTATUSNAVN,
                            ml.HENDELSEDATO           AS HENDELSEDATO,
                            mg.MELDEGRUPPENAVN        AS MELDEGRUPPENAVN,
                            mp.AAR                    AS AAR,
                            mp.PERIODEKODE            AS PERIODEKODE,
                            mp.UKENR_UKE1             AS UKENR_UKE1,
                            mp.UKENR_UKE2             AS UKENR_UKE2,
                            mp.DATO_FRA               AS DATO_FRA,
                            mp.DATO_TIL               AS DATO_TIL
                        FROM MELDEKORT m
                            INNER JOIN MELDEKORTPERIODE mp on m.AAR = mp.AAR AND m.PERIODEKODE = mp.PERIODEKODE
                            INNER JOIN BEREGNINGSTATUS be on be.BEREGNINGSTATUSKODE = m.BEREGNINGSTATUSKODE
                            INNER JOIN MKSKORTTYPE mt ON m.MKSKORTKODE = mt.MKSKORTKODE
                            INNER JOIN MELDEGRUPPETYPE mg ON m.MELDEGRUPPEKODE = mg.MELDEGRUPPEKODE 
                            LEFT JOIN MELDELOGG ml ON m.MELDEKORT_ID = ml.MELDEKORT_ID
                                AND ml.HENDELSETYPEKODE = m.BEREGNINGSTATUSKODE
                                AND ml.HENDELSEDATO = (
                                    SELECT MAX(ml2.HENDELSEDATO)
                                    FROM MELDELOGG ml2
                                    WHERE ml2.MELDEKORT_ID = m.MELDEKORT_ID
                                      AND ml2.HENDELSETYPEKODE = m.BEREGNINGSTATUSKODE
                                )
                        WHERE m.PERSON_ID = :personId
                        AND (   
                            mp.DATO_FRA <= :tilOgMedDato 
                            AND mp.DATO_TIL >= :fraOgMedDato
                        )
                """.trimIndent(),
                paramMap = mapOf(
                    "personId" to personId,
                    "fraOgMedDato" to fraOgMedDato,
                    "tilOgMedDato" to tilOgMedDato,
                ),
            ).map { row -> row.toMeldekort(txSession) }
                .asList,
        )
    }

    /**
     * Finner meldekort som er beregnet, men som det ikke finnes posteringer, utbetalingsgrunnlag, beregningslogg eller
     * anmerkninger for. Dette er da meldekort som er forsøkt beregnet, men som har feilet uten at det har blitt laget
     * en anmerkning.
     */
    fun hentVedtakForUtbetalingshistorikk(
        personId: Long,
        fraOgMedDato: LocalDate,
        tilOgMedDato: LocalDate,
        txSession: TransactionalSession,
    ): List<ArenaBeregnetMeldekortMedFeilDTO> {
        return txSession.run(
            action = queryOf(
                //language=SQL
                statement =
                """
                        SELECT 
                            m.MELDEKORT_ID            AS MELDEKORT_ID,
                            m.MOD_DATO                AS MOD_DATO,
                            m.MELDEKORTKODE           AS MELDEKORTKODE,
                            be.BEREGNINGSTATUSNAVN    AS BEREGNINGSTATUSNAVN,
                            mp.DATO_FRA               AS DATO_FRA,
                            mp.DATO_TIL               AS DATO_TIL
                        FROM MELDEKORT m
                            INNER JOIN MELDEKORTPERIODE mp ON m.AAR = mp.AAR AND m.PERIODEKODE = mp.PERIODEKODE
                            INNER JOIN BEREGNINGSTATUS be ON be.BEREGNINGSTATUSKODE = m.BEREGNINGSTATUSKODE 
                                AND m.BEREGNINGSTATUSKODE IN ('FERDI','FEIL','VENTE','KLAR')
                        WHERE m.PERSON_ID = :personId
                        AND (
                            mp.DATO_FRA <= :tilOgMedDato 
                            AND mp.DATO_TIL >= :fraOgMedDato
                        )
                        AND NOT EXISTS (
                            SELECT 1
                            FROM  UTBETALINGSGRUNNLAG u
                            WHERE u.MELDEKORT_ID = m.MELDEKORT_ID
                        )
                        AND NOT EXISTS (
                            SELECT 1
                          	FROM  POSTERING p
                          	WHERE p.MELDEKORT_ID = m.MELDEKORT_ID
                        )
                        AND NOT EXISTS (
                            SELECT 1
                            FROM  BEREGNINGSLOGG b
                            WHERE b.OBJEKT_ID = m.MELDEKORT_ID
                        )
                        AND NOT EXISTS (
                            SELECT 1
                            FROM  ANMERKNING a
                            WHERE a.OBJEKT_ID = m.MELDEKORT_ID
                          	    AND a.VEDTAK_ID IS NOT NULL
                        )
                """.trimIndent(),
                paramMap = mapOf(
                    "personId" to personId,
                    "fraOgMedDato" to fraOgMedDato,
                    "tilOgMedDato" to tilOgMedDato,
                ),
            ).map { row -> row.tilUtbetalingshistorikk() }
                .asList,
        )
    }

    private fun Row.toMeldekort(txSession: TransactionalSession): ArenaMeldekortDTO {
        val meldekortId = string("MELDEKORT_ID")
        val aar = int("AAR")
        val dager = meldekortdagDAO.findByMeldekortId(meldekortId, txSession)

        return ArenaMeldekortDTO(
            meldekortId = meldekortId,
            datoInnkommet = localDateOrNull("DATO_INNKOMMET"),
            statusArbeidet = string("STATUS_ARBEIDET"),
            statusKurs = string("STATUS_KURS"),
            statusFerie = string("STATUS_FERIE"),
            statusSyk = string("STATUS_SYK"),
            statusAnnetFravaer = string("STATUS_ANNETFRAVAER"),
            regDato = localDateTime("REG_DATO"),
            modDato = localDateTime("MOD_DATO"),
            meldekortType = string("MKSKORTTYPENAVN"),
            beregningstatusnavn = string("BEREGNINGSTATUSNAVN"),
            hendelsedato = localDate("HENDELSEDATO"),
            meldegruppenavn = string("MELDEGRUPPENAVN"),
            aar = aar,
            dager = dager,
            meldekortperiode = ArenaMeldekortperiodeDTO(
                aar = aar,
                periodekode = int("PERIODEKODE"),
                ukenrUke1 = int("UKENR_UKE1"),
                ukenrUke2 = int("UKENR_UKE2"),
                datoFra = localDate("DATO_FRA"),
                datoTil = localDate("DATO_TIL"),
            ),
        )
    }

    private fun Row.tilUtbetalingshistorikk(): ArenaBeregnetMeldekortMedFeilDTO {
        val meldekortkode = string("MELDEKORTKODE")
        val meldekortkodenavn = if (meldekortkode == "MK") "Meldekort" else meldekortkode

        return ArenaBeregnetMeldekortMedFeilDTO(
            meldekortId = string("MELDEKORT_ID"),
            modDato = localDate("MOD_DATO"),
            meldekortkodenavn = meldekortkodenavn,
            beregningstatusnavn = string("BEREGNINGSTATUSNAVN"),
            datoFra = localDate("DATO_FRA"),
            datoTil = localDate("DATO_TIL"),
        )
    }
}
