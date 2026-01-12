package no.nav.tiltakspenger.arena.repository

import kotliquery.Row
import kotliquery.TransactionalSession
import kotliquery.queryOf
import org.intellij.lang.annotations.Language

class VedtakfaktaDAO {
    private fun findVedtakFaktaByVedtakId(
        vedtakId: Long,
        txSession: TransactionalSession,
    ): List<ArenaVedtakfaktaDTO> {
        val paramMap = mapOf(
            "vedtak_id" to vedtakId.toString(),
        )
        return txSession.run(
            queryOf(sqlFindVedtaksfaktaByVedtakId, paramMap)
                .map { row -> row.toVedtakfakta() }
                .asList,
        )
    }

    fun findTiltakspengerVedtakfaktaByVedtakId(
        vedtakId: Long,
        txSession: TransactionalSession,
    ): ArenaTiltakspengerVedtakfaktaDTO {
        return findVedtakFaktaByVedtakId(vedtakId, txSession).toArenaTiltakspengerVedtakfaktaDTO()
    }

    fun findBarnetilleggVedtakfaktaByVedtakId(
        vedtakId: Long,
        txSession: TransactionalSession,
    ): ArenaBarnetilleggVedtakfaktaDTO {
        return findVedtakFaktaByVedtakId(vedtakId, txSession).toArenaBarnetilleggVedtakfaktaDTO()
    }

    fun findBeregningVedtakfaktaByVedtakId(
        vedtakId: Long,
        txSession: TransactionalSession,
    ): ArenaBeregningVedtakfaktaDTO {
        return findVedtakFaktaByVedtakId(vedtakId, txSession).toArenaBeregningVedtakfaktaDTO()
    }

    private fun Row.toVedtakfakta(): ArenaVedtakfaktaDTO {
        return ArenaVedtakfaktaDTO(
            vedtakId = long("VEDTAK_ID"),
            vedtakfaktaKode = string("VEDTAKFAKTAKODE"),
            vedtakfaktaVerdi = stringOrNull("VEDTAKVERDI"),
        )
    }

    @Language("SQL")
    private val sqlFindVedtaksfaktaByVedtakId =
        """
        SELECT VF.VEDTAK_ID, VF.VEDTAKFAKTAKODE, VF.VEDTAKVERDI
        FROM VEDTAKFAKTA VF
        WHERE VF.VEDTAK_ID = :vedtak_id
        """.trimIndent()
}
