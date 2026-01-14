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
    ): List<ArenaBeregningsloggDTO> {
        return txSession.run(
            action = queryOf(
                statement =
                    //language=SQL
                    """
                        SELECT 
                            b.objekt_id                 AS meldekort_id,
                            b.reg_dato                  AS dato_postert,
                            t.transaksjonstypenavn      AS transaksjonstypenavn,
                            bs.beregningstatusnavn      AS status,
                            b.vedtak_id                 AS vedtak_id,
                            b.dato_fra                  AS dato_periode_fra,
                            b.dato_til                  AS dato_periode_til
                        FROM beregningslogg b
                        INNER JOIN person pe on pe.person_id = b.person_id
                        INNER JOIN transaksjonstype t on t.transaksjonskode = b.transaksjonskode
                        INNER JOIN beregningstatus bs on bs.beregningstatuskode = b.beregningstatuskode
                        WHERE pe.fodselsnr = :fnr
                        AND (
                            b.dato_periode_fra <= TO_DATE(:tilOgMedDato, 'YYYY-MM-DD') 
                            AND b.dato_periode_til >= TO_DATE(:fraOgMedDato, 'YYYY-MM-DD')
                        )
                        AND b.tabellnavnalias = 'MKORT'
                        AND NOT EXISTS (
                            SELECT 1
                                FROM  utbetalingsgrunnlag u
                                WHERE u.meldekort_id  = b.objekt_id
                                AND   u.vedtak_id     = b.vedtak_id
                        )
                        AND NOT EXISTS (
                            SELECT 1
		                            FROM  postering p
		                            WHERE p.meldekort_id  = b.objekt_id
		                            AND   p.vedtak_id     = b.vedtak_id
                        )
                    """.trimIndent(),
                paramMap = mapOf(
                    "fnr" to fnr,
                    "fraOgMedDato" to fraOgMedDato,
                    "tilOgMedDato" to tilOgMedDato,
                ),
            ).map { row -> row.toBeregningslogg() }
                .asList,
        )
    }

    private fun Row.toBeregningslogg(): ArenaBeregningsloggDTO {
        return ArenaBeregningsloggDTO(
            meldekortId = string("meldekort_id"),
            datoPostert = localDate("dato_postert"),
            transaksjonstypenavn = string("transaksjonstypenavn"),
            status = string("status"),
            vedtakId = intOrNull("vedtak_id"),
            datoPeriodeFra = localDate("dato_periode_fra"),
            datoPeriodeTil = localDate("dato_periode_til"),
        )
    }
}
