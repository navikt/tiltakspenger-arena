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
                statement =
                    //language=SQL
                    """
                        SELECT 
                            a.object_id                 AS meldekort_id,
                            a.reg_dato                  AS dato_postert,
                            r.rettighetsnavn            AS transaksjonstypenavn,
                            bs.beregningstatusnavn      AS status,
                            a.vedtak_id                 AS vedtak_id,
                            mk.dato_fra                 AS dato_periode_fra,
                            mk.dato_til                 AS dato_periode_til
                        FROM anmerkning a
                        INNER JOIN vedtak v on a.vedtak_id = v.vedtak_id
                        INNER JOIN rettighetstype r on r.rettighetkode = v.rettighetkode
                        INNER JOIN meldekort m on m.meldekort_id = a.objekt_id
                        INNER JOIN meldekortperiode mk on mk.aar = m.aar AND mk.periodekode = m.periodekode
                        INNER JOIN beregningstatus bs on bs.beregningstatuskode = a.beregningstatuskode
                        INNER JOIN person pe on pe.person_id = m.person_id
                        WHERE pe.fodselsnr = :fnr 
                        AND a.tabellnavnalias = 'MKORT'
                        AND (
                                mk.dato_fra <= :tilOgMedDato 
                            AND mk.dato_til >= :fraOgMedDato
                        )                        
                        AND NOT EXISTS (
                        -- Optimizer hint for å bruke index på anmerkning_id
                        -- https://docs.oracle.com/cd/B10500_01/server.920/a96533/hintsref.htm#5156
                            SELECT /*+ INDEX(a2 ANMERK_I) */ 1
                            FROM  anmerkning a2
                            WHERE a2.anmerkning_id  < a.anmerkning_id
                            AND   a2.vedtak_id      = a.vedtak_id
                            AND   a2.objekt_id      = a.objekt_id
                        )
                        AND NOT EXISTS (
                            SELECT 1
                        	FROM  utbetalingsgrunnlag u
                        	WHERE u.meldekort_id  = a.objekt_id
                        	AND   u.vedtak_id     = a.vedtak_id
                        )
                        AND NOT EXISTS (
                            SELECT 1 
                            FROM  postering p
                        	WHERE p.meldekort_id  = a.objekt_id
                        	AND   p.vedtak_id     = a.vedtak_id
                        )
                        AND NOT EXISTS (
                            SELECT 1
                            FROM  beregningslogg b
                            WHERE b.objekt_id = m.meldekort_id
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
            meldekortId = string("meldekort_id"),
            datoPostert = localDate("dato_postert"),
            transaksjonstypenavn = string("transaksjonstypenavn"),
            sats = 0.0,
            status = string("status"),
            vedtakId = intOrNull("vedtak_id"),
            beløp = 0.0,
            datoPeriodeFra = localDate("dato_periode_fra"),
            datoPeriodeTil = localDate("dato_periode_til"),
        )
    }
}
