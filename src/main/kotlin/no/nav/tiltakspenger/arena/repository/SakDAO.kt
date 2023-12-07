package no.nav.tiltakspenger.arena.repository

import kotliquery.Row
import kotliquery.TransactionalSession
import kotliquery.queryOf
import mu.KotlinLogging
import org.intellij.lang.annotations.Language
import java.time.LocalDate

class SakDAO(
    private val vedtakDAO: VedtakDAO = VedtakDAO(),
) {
    companion object {
        private val LOG = KotlinLogging.logger {}
        private val SECURELOG = KotlinLogging.logger("tjenestekall")

        private fun String.toStatus(): ArenaSakStatus =
            ArenaSakStatus.valueOf(this)

        private fun String.toYtelse(): ArenaYtelse =
            ArenaYtelse.valueOf(this)
    }

    fun findByPersonIdAndPeriode(
        personId: Long,
        fom: LocalDate,
        tom: LocalDate,
        txSession: TransactionalSession,
    ): List<ArenaSakDTO> {
        val paramMap = mapOf(
            "person_id" to personId.toString(),
            "fra_dato" to fom,
            "til_dato" to tom,
        )
        return txSession.run(
            queryOf(findBySQL, paramMap)
                .map { row -> row.toSak(txSession) }
                .asList,
        )
    }

    private fun Row.toSak(txSession: TransactionalSession): ArenaSakDTO {
        val sakId = long("SAK_ID")
        val vedtak = vedtakDAO.findBySakId(sakId, txSession)
        LOG.info { "Antall vedtak er ${vedtak.size} for sak med id $sakId" }
        SECURELOG.info { "Antall vedtak er ${vedtak.size} for sak med id $sakId" }

        return ArenaSakDTO(
            aar = int("AAR"),
            lopenrSak = long("LOPENRSAK"),
            status = string("SAKSTATUSKODE").toStatus(),
            ytelsestype = string("SAKSKODE").toYtelse(),
            ihtVedtak = vedtak,
        )
    }

    @Language("SQL")
    private val findBySQL =
        """
        SELECT *
        FROM sak
        WHERE sak.tabellnavnalias = 'PERS'
          AND sak.objekt_id = :person_id
          AND sak.sakstatuskode <> 'HIST'
          AND sak.sakskode = 'INDIV'
          AND EXISTS
            (SELECT 1
             FROM vedtak
             WHERE sak.sak_id = vedtak.sak_id
               AND vedtak.rettighetkode = 'BASI' --Sjekker ikke for barnetillegg her
               AND (((SELECT DISTINCT first_value(fra_dato) OVER (ORDER BY fra_dato ASC)
                     FROM vedtak vedt
                     WHERE vedt.sak_id = sak.sak_id
                     AND vedt.utfallkode != 'AVBRUTT'
                     AND vedt.fra_dato <= NVL(vedt.til_dato, vedt.fra_dato)) <= nvl(:til_dato, SYSDATE)
               AND (SELECT DISTINCT nvl(first_value(til_dato) OVER (ORDER BY fra_dato DESC, til_dato DESC), SYSDATE)
                    FROM vedtak vedt
                    WHERE vedt.sak_id = sak.sak_id
                    AND vedt.fra_dato <= NVL(vedt.til_dato, vedt.fra_dato) -- Skal ikke ha vedtakene som er engangsutbetalinger
                    AND vedt.utfallkode != 'AVBRUTT'
                    AND vedt.vedtaktypekode IN ('O', 'E', 'G')) >= nvl(:fra_dato, SYSDATE)
               AND vedtak.vedtaktypekode IN ('O', 'E', 'G')
               AND vedtak.utfallkode NOT IN ('AVBRUTT', 'NEI')
               AND vedtak.fra_dato <= NVL(vedtak.til_dato, vedtak.fra_dato)
               )))
        ORDER BY sak.sak_id DESC
        """.trimIndent()
}
