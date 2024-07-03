package no.nav.tiltakspenger.arena.repository

import kotliquery.Row
import kotliquery.TransactionalSession
import kotliquery.queryOf
import mu.KotlinLogging
import org.intellij.lang.annotations.Language

class SakDAO(
    private val tiltakspengerVedtakDAO: TiltakspengerVedtakDAO = TiltakspengerVedtakDAO(),
    private val barnetilleggVedtakDAO: BarnetilleggVedtakDAO = BarnetilleggVedtakDAO(),
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
            queryOf(sqlFindTiltakspengerSakerByPersonId, paramMap)
                .map { row -> row.toSak(txSession) }
                .asList,
        )
    }

    private fun Row.toSak(txSession: TransactionalSession): ArenaSakDTO {
        val sakId = long("SAK_ID")
        val tiltakspengerVedtak = tiltakspengerVedtakDAO.findTiltakspengerBySakId(sakId, txSession)
        val barnetilleggVedtak = barnetilleggVedtakDAO.findBarnetilleggBySakId(sakId, txSession)
        LOG.info { "Antall tiltakspengerVedtak er ${tiltakspengerVedtak.size} for sak med id $sakId" }
        LOG.info { "Antall barnetilleggVedtak er ${barnetilleggVedtak.size} for sak med id $sakId" }
        SECURELOG.info { "Antall tiltakspengerVedtak er ${tiltakspengerVedtak.size} for sak med id $sakId" }
        SECURELOG.info { "Antall barnetilleggVedtak er ${barnetilleggVedtak.size} for sak med id $sakId" }

        return ArenaSakDTO(
            sakId = sakId,
            aar = int("AAR"),
            lopenrSak = long("LOPENRSAK"),
            status = string("SAKSTATUSKODE").toStatus(),
            ytelsestype = string("SAKSKODE").toYtelse(),
            tiltakspengerVedtak = tiltakspengerVedtak,
            barnetilleggVedtak = barnetilleggVedtak,
        )
    }

    @Language("SQL")
    private val sqlFindTiltakspengerSakerByPersonId =
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
