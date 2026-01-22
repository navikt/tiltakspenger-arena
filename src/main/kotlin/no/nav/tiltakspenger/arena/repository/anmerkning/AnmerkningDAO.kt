package no.nav.tiltakspenger.arena.repository.anmerkning

import kotliquery.Row
import kotliquery.TransactionalSession
import kotliquery.queryOf
import java.time.LocalDate

class AnmerkningDAO {
    /**
     * Finner vedtak ut fra anmerkning, hvor vedtaket ikke har en tilknyttet postering, utbetalingsgrunnlag eller
     * beregningslogg. Dette er da vedtak som er forsøkt beregenet, men som feilet og fikk laget en anmerkning på seg.
     * Henter kun vedtak knyttet til anmerkningen med den laveste iden for at vedtaket kun skal hentes en gang.
     */
    fun hentVedtakForUtbetalingshistorikk(
        personId: Long,
        fraOgMedDato: LocalDate,
        tilOgMedDato: LocalDate,
        txSession: TransactionalSession,
    ): List<ArenaAnmerkningMedPeriodeDTO> {
        return txSession.run(
            action = queryOf(
                //language=SQL
                statement =
                """
                        SELECT 
                            a.OBJEKT_ID                 AS MELDEKORT_ID,
                            a.REG_DATO                  AS REG_DATO,
                            r.RETTIGHETNAVN             AS RETTIGHETNAVN,
                            bs.BEREGNINGSTATUSNAVN      AS BEREGNINGSTATUSNAVN,
                            a.VEDTAK_ID                 AS VEDTAK_ID,
                            mk.DATO_FRA                 AS DATO_FRA,
                            mk.DATO_TIL                 AS DATO_TIL
                        FROM ANMERKNING a
                        INNER JOIN VEDTAK v ON a.VEDTAK_ID = v.VEDTAK_ID
                        INNER JOIN RETTIGHETTYPE r ON r.RETTIGHETKODE = v.RETTIGHETKODE
                        INNER JOIN MELDEKORT m ON m.MELDEKORT_ID = a.OBJEKT_ID
                        INNER JOIN MELDEKORTPERIODE mk ON mk.AAR = m.AAR AND mk.PERIODEKODE = m.PERIODEKODE
                        INNER JOIN BEREGNINGSTATUS bs ON bs.BEREGNINGSTATUSKODE = m.BEREGNINGSTATUSKODE
                        WHERE m.PERSON_ID = :personId 
                        AND a.TABELLNAVNALIAS = 'MKORT'
                        AND (
                                mk.DATO_FRA <= :tilOgMedDato 
                            AND mk.DATO_TIL >= :fraOgMedDato
                        )                        
                        AND NOT EXISTS (
                        -- Optimizer hint for å bruke index på anmerkning_id
                        -- https://docs.oracle.com/cd/B10500_01/server.920/a96533/hintsref.htm#5156
                            SELECT /*+ INDEX(a2 ANMERK_I) */ 1
                            FROM  ANMERKNING a2
                            WHERE a2.ANMERKNING_ID  < a.ANMERKNING_ID
                            AND   a2.VEDTAK_ID      = a.VEDTAK_ID
                            AND   a2.OBJEKT_ID      = a.OBJEKT_ID
                        )
                        AND NOT EXISTS (
                            SELECT 1
                        	FROM  UTBETALINGSGRUNNLAG u
                        	WHERE u.MELDEKORT_ID  = a.OBJEKT_ID
                        	AND   u.VEDTAK_ID     = a.VEDTAK_ID
                        )
                        AND NOT EXISTS (
                            SELECT 1 
                            FROM  POSTERING p
                        	WHERE p.MELDEKORT_ID  = a.OBJEKT_ID
                        	AND   p.VEDTAK_ID     = a.VEDTAK_ID
                        )
                        AND NOT EXISTS (
                            SELECT 1
                            FROM  BEREGNINGSLOGG b
                            WHERE b.OBJEKT_ID = m.MELDEKORT_ID
                        )
                """.trimIndent(),
                paramMap = mapOf(
                    "personId" to personId,
                    "fraOgMedDato" to fraOgMedDato,
                    "tilOgMedDato" to tilOgMedDato,
                ),
            ).map { row -> row.tilAnmerkningMedPeriode() }
                .asList,
        )
    }

    private fun Row.tilAnmerkningMedPeriode(): ArenaAnmerkningMedPeriodeDTO {
        return ArenaAnmerkningMedPeriodeDTO(
            meldekortId = longOrNull("MELDEKORT_ID"),
            regDato = localDate("REG_DATO"),
            rettighetnavn = string("RETTIGHETNAVN"),
            beregningstatusnavn = string("BEREGNINGSTATUSNAVN"),
            vedtakId = longOrNull("VEDTAK_ID"),
            datoFra = localDate("DATO_FRA"),
            datoTil = localDate("DATO_TIL"),
        )
    }
}
