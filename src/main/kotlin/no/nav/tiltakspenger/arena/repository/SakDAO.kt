package no.nav.tiltakspenger.arena.repository

import kotliquery.Row
import kotliquery.TransactionalSession
import kotliquery.queryOf
import mu.KotlinLogging
import org.intellij.lang.annotations.Language

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

    fun findByPersonId(
        personId: Long,
        txSession: TransactionalSession,
    ): List<ArenaSakDTO> {
        val paramMap = mapOf(
            "person_id" to personId.toString(),
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
            vedtak = vedtak,
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
        ORDER BY sak.sak_id DESC
        """.trimIndent()
}
