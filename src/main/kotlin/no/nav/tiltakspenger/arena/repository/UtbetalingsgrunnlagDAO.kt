package no.nav.tiltakspenger.arena.repository

import kotliquery.Row
import kotliquery.TransactionalSession
import kotliquery.queryOf
import java.time.LocalDate

class UtbetalingsgrunnlagDAO {
    fun hentVedtakForUtbetalingshistorikk(
        personId: Long,
        fraOgMedDato: LocalDate,
        tilOgMedDato: LocalDate,
        txSession: TransactionalSession,
    ): List<ArenaUtbetalingsgrunnlagDTO> {
        return txSession.run(
            action = queryOf(
                //language=SQL
                statement =
                """
                        SELECT 
                            u.MELDEKORT_ID              AS MELDEKORT_ID,
                            u.MOD_DATO                  AS MOD_DATO,
                            t.TRANSAKSJONSTYPENAVN      AS TRANSAKSJONSTYPENAVN,
                            u.POSTERINGSATS             AS POSTERINGSATS,
                            u.VEDTAK_ID                 AS VEDTAK_ID,
                            u.BELOP                     AS BELOP,
                            u.DATO_PERIODE_FRA          AS DATO_PERIODE_FRA,
                            u.DATO_PERIODE_TIL          AS DATO_PERIODE_TIL
                        FROM UTBETALINGSGRUNNLAG u
                        INNER JOIN TRANSAKSJONTYPE t on t.TRANSAKSJONSKODE = u.TRANSAKSJONSKODE
                        WHERE u.PERSON_ID = :personId
                        AND (
                            u.DATO_PERIODE_FRA <= :tilOgMedDato 
                            AND u.DATO_PERIODE_TIL >= :fraOgMedDato
                        )
                """.trimIndent(),
                paramMap = mapOf(
                    "personId" to personId,
                    "fraOgMedDato" to fraOgMedDato,
                    "tilOgMedDato" to tilOgMedDato,
                ),
            ).map { row -> row.tilUtbetalingsgrunnlag() }
                .asList,
        )
    }

    private fun Row.tilUtbetalingsgrunnlag(): ArenaUtbetalingsgrunnlagDTO {
        return ArenaUtbetalingsgrunnlagDTO(
            meldekortId = string("MELDEKORT_ID"),
            modDato = localDate("MOD_DATO"),
            transaksjonstypenavn = string("TRANSAKSJONSTYPENAVN"),
            posteringsats = double("POSTERINGSATS"),
            vedtakId = intOrNull("VEDTAK_ID"),
            belop = double("BELOP"),
            datoPeriodeFra = localDate("DATO_PERIODE_FRA"),
            datoPeriodeTil = localDate("DATO_PERIODE_TIL"),
        )
    }
}
