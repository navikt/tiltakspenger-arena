package no.nav.tiltakspenger.arena.repository

import kotliquery.Row
import kotliquery.TransactionalSession
import kotliquery.queryOf
import java.time.LocalDate

class BeregningsloggDAO {
    fun hentVedtakForUtbetalingshistorikk(
        personId: Long,
        fraOgMedDato: LocalDate,
        tilOgMedDato: LocalDate,
        txSession: TransactionalSession,
    ): List<ArenaUtbetalingshistorikkDTO> {
        //language=SQL
        return txSession.run(
            action = queryOf(
                statement =
                """
                        SELECT 
                            b.OBJEKT_ID                 AS MELDEKORT_ID,
                            b.REG_DATO                  AS REG_DATO,
                            r.RETTIGHETNAVN             AS RETTIGHETNAVN,
                            bs.BEREGNINGSTATUSNAVN      AS BEREGNINGSTATUSNAVN,
                            b.VEDTAK_ID                 AS VEDTAK_ID,
                            b.DATO_FRA                  AS DATO_FRA,
                            b.DATO_TIL                  AS DATO_TIL
                        FROM BEREGNINGSLOGG b
                        INNER JOIN VEDTAK v ON b.vedtak_id = v.vedtak_id
                        INNER JOIN RETTIGHETTYPE r ON v.RETTIGHETKODE = r.RETTIGHETKODE
                        INNER JOIN MELDEKORT m ON m.MELDEKORT_ID = b.OBJEKT_ID
                        INNER JOIN BEREGNINGSTATUS bs ON bs.BEREGNINGSTATUSKODE = m.BEREGNINGSTATUSKODE
                        WHERE b.PERSON_ID = :personId
                        AND (
                            b.DATO_FRA <= :tilOgMedDato 
                            AND b.DATO_TIL >= :fraOgMedDato
                        )
                        AND b.TABELLNAVNALIAS = 'MKORT'
                        AND NOT EXISTS (
                            SELECT 1
                                FROM  UTBETALINGSGRUNNLAG u
                                WHERE u.MELDEKORT_ID  = b.OBJEKT_ID
                                AND   u.VEDTAK_ID     = b.VEDTAK_ID
                        )
                        AND NOT EXISTS (
                            SELECT 1
                                    FROM  POSTERING p
                                    WHERE p.MELDEKORT_ID  = b.OBJEKT_ID
                                    AND   p.VEDTAK_ID     = b.VEDTAK_ID
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

    private fun Row.tilUtbetalingshistorikk(): ArenaUtbetalingshistorikkDTO {
        return ArenaUtbetalingshistorikkDTO(
            meldekortId = string("MELDEKORT_ID"),
            dato = localDate("REG_DATO"),
            transaksjonstype = string("RETTIGHETNAVN"),
            sats = 0.0,
            status = string("BEREGNINGSTATUSNAVN"),
            vedtakId = intOrNull("VEDTAK_ID"),
            beløp = 0.0,
            fraDato = localDate("DATO_FRA"),
            tilDato = localDate("DATO_TIL"),
        )
    }
}
