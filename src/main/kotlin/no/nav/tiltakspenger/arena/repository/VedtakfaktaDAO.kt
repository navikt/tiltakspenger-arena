package no.nav.tiltakspenger.arena.repository

import kotliquery.Row
import kotliquery.TransactionalSession
import kotliquery.queryOf
import mu.KotlinLogging
import org.intellij.lang.annotations.Language

class VedtakfaktaDAO {
    companion object {
        private val log = KotlinLogging.logger {}
        private val securelog = KotlinLogging.logger("tjenestekall")
    }

    fun findTiltakspengerVedtakfaktaByVedtakId(
        vedtakId: Long,
        txSession: TransactionalSession,
    ): ArenaTiltakspengerVedtakfaktaDTO {
        val paramMap = mapOf(
            "vedtak_id" to vedtakId.toString(),
        )
        return txSession.run(
            queryOf(findBySQL, paramMap)
                .map { row -> row.toVedtakfakta() }
                .asList,
        ).toArenaTiltakspengerVedtakfaktaDTO()
    }

    fun findBarnetilleggVedtakfaktaByVedtakId(
        vedtakId: Long,
        txSession: TransactionalSession,
    ): ArenaBarnetilleggVedtakfaktaDTO {
        val paramMap = mapOf(
            "vedtak_id" to vedtakId.toString(),
        )
        return txSession.run(
            queryOf(findBySQL, paramMap)
                .map { row -> row.toVedtakfakta() }
                .asList,
        ).toArenaBarnetilleggVedtakfaktaDTO()
    }

    private fun Row.toVedtakfakta(): ArenaVedtakfaktaDTO {
        return ArenaVedtakfaktaDTO(
            vedtakId = long("VEDTAK_ID"),
            vedtakfaktaKode = string("VEDTAKFAKTAKODE"),
            vedtakfaktaVerdi = stringOrNull("VEDTAKVERDI"),
        )
    }

    @Language("SQL")
    private val findBySQL =
        """
        SELECT VF.VEDTAK_ID, VF.VEDTAKFAKTAKODE, VF.VEDTAKVERDI
        FROM VEDTAKFAKTA VF
        WHERE VF.VEDTAK_ID = :vedtak_id
        """.trimIndent()
}
