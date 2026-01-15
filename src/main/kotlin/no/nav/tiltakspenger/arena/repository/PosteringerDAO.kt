package no.nav.tiltakspenger.arena.repository

import io.github.oshai.kotlinlogging.KotlinLogging
import kotliquery.Row
import kotliquery.TransactionalSession
import kotliquery.queryOf
import no.nav.tiltakspenger.libs.logging.Sikkerlogg
import java.time.LocalDate

class PosteringerDAO(
    private val personDao: PersonDAO,
) {
    private val logger = KotlinLogging.logger {}

    fun findByPersonId(
        fnr: String,
        fraOgMedDato: LocalDate,
        tilOgMedDato: LocalDate,
        txSession: TransactionalSession,
    ): List<ArenaUtbetalingshistorikkDTO> {
        val person = personDao.findByFnr(fnr, txSession)
        if (person == null) {
            logger.info { "Fant ikke person" }
            Sikkerlogg.info { "Fant ikke person med ident $fnr" }
            return emptyList()
        }

        return txSession.run(
            action = queryOf(
                statement =
                    //language=SQL
                    """
                        SELECT 
                            p.MELDEKORT_ID          AS MELDEKORT_ID,
                            p.DATO_POSTERT          AS DATO_POSTERT,
                            t.TRANSAKSJONSTYPENAVN  AS TRANSAKSJONSTYPENAVN,
                            p.POSTERINGSATS         AS SATS,
                            p.VEDTAK_ID             AS VEDTAK_ID,
                            p.BELOEP                AS BELOEP,
                            p.DATO_PERIODE_FRA      AS DATO_PERIODE_FRA,
                            p.DATO_PERIODE_TIL      AS DATO_PERIODE_TIL
                        FROM POSTERING p
                        INNER JOIN TRANSAKSJONSTYPE t on t.TRANSAKSJONSKODE = p.TRANSAKSJONSKODE
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
            datoPostert = localDate("DATO_POSTERT"),
            transaksjonstypenavn = string("TRANSAKSJONSTYPENAVN"),
            sats = double("SATS"),
            status = "Overført utbetaling",
            vedtakId = intOrNull("VEDTAK_ID"),
            beløp = double("BELOEP"),
            datoPeriodeFra = localDate("DATO_PERIODE_FRA"),
            datoPeriodeTil = localDate("DATO_PERIODE_TIL"),
        )
    }
}
