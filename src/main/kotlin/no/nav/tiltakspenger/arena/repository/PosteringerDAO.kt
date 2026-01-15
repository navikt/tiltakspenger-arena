package no.nav.tiltakspenger.arena.repository

import io.github.oshai.kotlinlogging.KotlinLogging
import kotliquery.Row
import kotliquery.TransactionalSession
import kotliquery.queryOf
import java.time.LocalDate

class PosteringerDAO {
    private val logger = KotlinLogging.logger {}

    fun hentVedtakForUtbetalingshistorikk(
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
                            p.MELDEKORT_ID          AS MELDEKORT_ID,
                            p.DATO_POSTERT          AS DATO_POSTERT,
                            t.TRANSAKSJONSTYPENAVN  AS TRANSAKSJONSTYPENAVN,
                            p.POSTERINGSATS         AS POSTERINGSATS,
                            p.VEDTAK_ID             AS VEDTAK_ID,
                            p.BELOP                 AS BELOP,
                            p.DATO_PERIODE_FRA      AS DATO_PERIODE_FRA,
                            p.DATO_PERIODE_TIL      AS DATO_PERIODE_TIL
                        FROM POSTERING p
                        INNER JOIN TRANSAKSJONTYPE t on t.TRANSAKSJONSKODE = p.TRANSAKSJONSKODE
                        INNER JOIN PERSON pe on pe.PERSON_ID = p.PERSON_ID
                        WHERE pe.FODSELSNR = :fnr
                        AND (
                            p.DATO_PERIODE_FRA <= :tilOgMedDato 
                            AND p.DATO_PERIODE_TIL >= :fraOgMedDato
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
            dato = localDate("DATO_POSTERT"),
            transaksjonstype = string("TRANSAKSJONSTYPENAVN"),
            sats = double("POSTERINGSATS"),
            status = "Overført utbetaling",
            vedtakId = intOrNull("VEDTAK_ID"),
            beløp = double("BELOEP"),
            fraDato = localDate("DATO_PERIODE_FRA"),
            tilDato = localDate("DATO_PERIODE_TIL"),
        )
    }
}
