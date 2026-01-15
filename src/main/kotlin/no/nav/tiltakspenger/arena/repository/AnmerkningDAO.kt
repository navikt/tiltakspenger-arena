package no.nav.tiltakspenger.arena.repository

import io.github.oshai.kotlinlogging.KotlinLogging
import kotliquery.Row
import kotliquery.TransactionalSession
import kotliquery.queryOf
import no.nav.tiltakspenger.arena.routes.ArenaAnmerkningDTO
import java.time.LocalDate

class AnmerkningDAO(
    private val personDao: PersonDAO,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * Finner vedtak ut fra anmerkning, hvor vedtaket ikke har en tilknyttet postering, utbetalingsgrunnlag eller
     * beregningslogg. Dette er da vedtak som er forsøkt beregenet, men som feilet og fikk laget en anmerkning på seg.
     * Henter kun vedtak knyttet til anmerkningen med den laveste iden for at vedtaket kun skal hentes en gang.
     */
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
                            a.OBJECT_ID                 AS MELDEKORT_ID,
                            a.REG_DATO                  AS REG_DATO,
                            r.RETTIGHETSNAVN            AS TRANSAKSJONSTYPENAVN,
                            bs.BEREGNINGSTATUSNAVN      AS BEREGNINGSTATUSNAVN,
                            a.VEDTAK_ID                 AS VEDTAK_ID,
                            mk.DATO_FRA                 AS DATO_FRA,
                            mk.DATO_TIL                 AS DATO_TIL
                        FROM ANMERKNING a
                        INNER JOIN VEDTAK v on a.VEDTAK_ID = v.VEDTAK_ID
                        INNER JOIN RETTIGHETSTYPE r on r.RETTIGHETKODE = v.RETTIGHETKODE
                        INNER JOIN MELDEKORT m on m.MELDEKORT_ID = a.OBJEKT_ID
                        INNER JOIN MELDEKORTPERIODE mk on mk.AAR = m.AAR AND mk.PERIODEKODE = m.PERIODEKODE
                        INNER JOIN BEREGNINGSTATUS bs on bs.BEREGNINGSTATUSKODE = a.BEREGNINGSTATUSKODE
                        INNER JOIN PERSON pe on pe.PERSON_ID = m.PERSON_ID
                        WHERE pe.FODSELSNR = :fnr 
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
                    "fnr" to fnr,
                    "fraOgMedDato" to fraOgMedDato,
                    "tilOgMedDato" to tilOgMedDato,
                ),
            ).map { row -> row.tilUtbetalingshistorikk() }
                .asList,
        )
    }

    fun findByVedtakAndMeldekort(
        vedtakId: Int?,
        meldekortId: Int,
        txSession: TransactionalSession,
    ): List<ArenaAnmerkningDTO> {
        return txSession.run(
            action = queryOf(
                statement =
                    //language=SQL
                    """
                        SELECT 
                            a.VEDTAK_ID     AS VEDTAK_ID,
                            a.REG_DATO      AS REG_DATO,
                            at.BESKRIVELSE  AS BESKRIVELSE
                        FROM ANMERKNING a
                        INNER JOIN ANMERKNINGTYPE at ON a.ANMERKNINGTYPE_ID = at.ANMERKNINGTYPE_ID                  
                        WHERE a.TABELLNAVNALIAS = 'MKORT' 
                        AND a.OBJEKT_ID = :meldekortId
                        AND (a.VEDTAK_ID = :vedtakId OR a.VEDTAK_ID IS NULL)
                    """.trimIndent(),
                paramMap = mapOf(
                    "vedtakId" to vedtakId,
                    "meldekortId" to meldekortId,
                ),
            ).map { row -> row.toAnmerkning() }
                .asList,
        )
    }

    private fun Row.toAnmerkning(): ArenaAnmerkningDTO {
        // Bruker samme logikk som i PLSQL som benyttes i arena-api for å bestemme kilde. Queryen henter bare anmerkninger relatert til meldekort og deres vedtak.
        val kilde = if (stringOrNull("VEDTAK_ID") == null) "Meldekort" else "Vedtak"

        return ArenaAnmerkningDTO(
            kilde = kilde,
            regDato = localDateOrNull("REG_DATO"),
            beskrivelse = stringOrNull("BESKRIVELSE"),
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
