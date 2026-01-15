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

    fun hentVedtakForUtbetalingshistorikk(
        fnr: String,
        fraOgMedDato: LocalDate,
        tilOgMedDato: LocalDate,
        txSession: TransactionalSession,
    ): List<ArenaUtbetalingshistorikkDTO> {
        return txSession.run(
            action = queryOf(
                statement =
                    //language=SQL
                    """
                        SELECT 
                            u.MELDEKORT_ID              AS MELDEKORT_ID,
                            u.MOD_DATO                  AS MOD_DATO,
                            t.TRANSAKSJONSTYPENAVN      AS TRANSAKSJONSTYPENAVN,
                            u.POSTERINGSATS             AS POSTERINGSATS,
                            u.VEDTAK_ID                 AS VEDTAK_ID,
                            u.BELOEP                    AS BELOEP,
                            u.DATO_PERIODE_FRA          AS DATO_PERIODE_FRA,
                            u.DATO_PERIODE_TIL          AS DATO_PERIODE_TIL
                        FROM UTBETALINGSGRUNNLAG u
                        INNER JOIN TRANSAKSJONSTYPE t on t.TRANSAKSJONSKODE = u.TRANSAKSJONSKODE
                        INNER JOIN PERSON pe on pe.PERSON_ID = u.PERSON_ID
                        WHERE pe.FODSELSNR = :fnr
                        AND (
                            u.DATO_PERIODE_FRA <= :tilOgMedDato 
                            AND u.DATO_PERIODE_TIL >= :fraOgMedDato
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
            dato = localDate("MOD_DATO"),
            transaksjonstype = string("TRANSAKSJONSTYPENAVN"),
            sats = double("POSTERINGSATS"),
            status = "Ikke overført utbetaling",
            vedtakId = intOrNull("VEDTAK_ID"),
            beløp = double("BELOEP"),
            fraDato = localDate("DATO_PERIODE_FRA"),
            tilDato = localDate("DATO_PERIODE_TIL"),
        )
    }
}
