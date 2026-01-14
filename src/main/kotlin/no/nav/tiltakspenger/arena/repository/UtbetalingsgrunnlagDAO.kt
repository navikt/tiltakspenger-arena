package no.nav.tiltakspenger.arena.repository

import io.github.oshai.kotlinlogging.KotlinLogging
import kotliquery.Row
import kotliquery.TransactionalSession
import kotliquery.queryOf
import java.time.LocalDate

class UtbetalingsgrunnlagDAO(
    private val personDao: PersonDAO,
) {
    private val logger = KotlinLogging.logger {}

    fun findByPersonId(
        fnr: String,
        fraOgMedDato: LocalDate,
        tilOgMedDato: LocalDate,
        txSession: TransactionalSession,
    ): List<ArenaUtbetalingsgrunnlagDTO> {
        return txSession.run(
            action = queryOf(
                statement =
                    //language=SQL
                    """
                        SELECT 
                            u.MELDEKORT_ID              AS MELDEKORT_ID,
                            u.MOD_DATO                  AS DATO_POSTERT,
                            t.TRANSAKSJONSTYPENAVN      AS TRANSAKSJONSTYPENAVN,
                            u.POSTERINGSATS             AS SATS,
                            u.VEDTAK_ID                 AS VEDTAK_ID,
                            u.BELOEP                    AS BELOEP,
                            u.DATO_PERIODE_FRA          AS DATO_PERIODE_FRA,
                            u.DATO_PERIODE_TIL          AS DATO_PERIODE_TIL
                        FROM UTBETALINGSGRUNNLAG u
                        INNER JOIN TRANSAKSJONSTYPE t on t.TRANSAKSJONSKODE = u.TRANSAKSJONSKODE
                        INNER JOIN PERSON pe on pe.PERSON_ID = u.PERSON_ID
                        WHERE pe.FODSELSNR = :fnr
                        AND (
                            u.DATO_PERIODE_FRA <= TO_DATE(:tilOgMedDato, 'YYYY-MM-DD') 
                            AND u.DATO_PERIODE_TIL >= TO_DATE(:fraOgMedDato, 'YYYY-MM-DD')
                        )
                    """.trimIndent(),
                paramMap = mapOf(
                    "fnr" to fnr,
                    "fraOgMedDato" to fraOgMedDato,
                    "tilOgMedDato" to tilOgMedDato,
                ),
            ).map { row -> row.toUtbetalingsgrunnlag() }
                .asList,
        )
    }

    private fun Row.toUtbetalingsgrunnlag(): ArenaUtbetalingsgrunnlagDTO {
        return ArenaUtbetalingsgrunnlagDTO(
            meldekortId = string("MELDEKORT_ID"),
            datoPostert = localDate("DATO_POSTERT"),
            transaksjonstypenavn = string("TRANSAKSJONSTYPENAVN"),
            sats = double("SATS"),
            vedtakId = intOrNull("VEDTAK_ID"),
            beløp = double("BELOEP"),
            datoPeriodeFra = localDate("DATO_PERIODE_FRA"),
            datoPeriodeTil = localDate("DATO_PERIODE_TIL"),
        )
    }
}
