package no.nav.tiltakspenger.arena.repository

import io.github.oshai.kotlinlogging.KotlinLogging
import kotliquery.Row
import kotliquery.TransactionalSession
import kotliquery.queryOf
import java.time.LocalDate

class BeregningsloggDAO(
    private val personDao: PersonDAO,
) {
    private val logger = KotlinLogging.logger {}

    fun hentForUtbetalingshistorikk(
        fnr: String,
        fraOgMedDato: LocalDate,
        tilOgMedDato: LocalDate,
        txSession: TransactionalSession,
    ): List<ArenaUtbetalingshistorikkDTO> {
        return txSession.run(
            action = queryOf(
                //language=SQL
                statement =
                    """
                        SELECT 
                            b.OBJEKT_ID                 AS MELDEKORT_ID,
                            b.REG_DATO                  AS REG_DATO,
                            t.TRANSAKSJONSTYPENAVN      AS TRANSAKSJONSTYPENAVN,
                            bs.BEREGNINGSTATUSNAVN      AS BEREGNINGSTATUSNAVN,
                            b.VEDTAK_ID                 AS VEDTAK_ID,
                            b.DATO_FRA                  AS DATO_FRA,
                            b.DATO_TIL                  AS DATO_TIL
                        FROM BEREGNINGSLOGG b
                        INNER JOIN PERSON pe on pe.PERSON_ID = b.PERSON_ID
                        INNER JOIN TRANSAKSJONSTYPE t on t.TRANSAKSJONSKODE = b.TRANSAKSJONSKODE
                        INNER JOIN BEREGNINGSTATUS bs on bs.BEREGNINGSTATUSKODE = b.BEREGNINGSTATUSKODE
                        WHERE pe.FODSELSNR = :fnr
                        AND (
                            b.DATO_PERIODE_FRA <= :tilOgMedDato 
                            AND b.DATO_PERIODE_TIL >= :fraOgMedDato
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
                    "fnr" to fnr,
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
            transaksjonstype = string("TRANSAKSJONSTYPENAVN"),
            sats = 0.0,
            status = string("BEREGNINGSTATUSNAVN"),
            vedtakId = intOrNull("VEDTAK_ID"),
            beløp = 0.0,
            fraDato = localDate("DATO_FRA"),
            tilDato = localDate("DATO_TIL"),
        )
    }
}
