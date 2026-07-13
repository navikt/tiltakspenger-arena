package no.nav.tiltakspenger.arena.repository.sak

import kotliquery.Row
import kotliquery.TransactionalSession
import kotliquery.queryOf
import no.nav.tiltakspenger.arena.repository.ArenaSakStatus
import no.nav.tiltakspenger.arena.repository.ArenaYtelse
import no.nav.tiltakspenger.arena.repository.vedtak.BarnetilleggVedtakDAO
import no.nav.tiltakspenger.arena.repository.vedtak.TiltakspengerVedtakDAO
import no.nav.tiltakspenger.arena.repository.vedtakfakta.VedtakfaktaLoggkontekst
import org.intellij.lang.annotations.Language

class SakDAO(
    private val tiltakspengerVedtakDAO: TiltakspengerVedtakDAO = TiltakspengerVedtakDAO(),
    private val barnetilleggVedtakDAO: BarnetilleggVedtakDAO = BarnetilleggVedtakDAO(),
) {
    companion object {
        private fun String.toStatus(): ArenaSakStatus =
            ArenaSakStatus.valueOf(this)

        private fun String.toYtelse(): ArenaYtelse =
            ArenaYtelse.valueOf(this)
    }

    fun findByPersonId(
        personId: Long,
        fnr: String,
        txSession: TransactionalSession,
    ): List<ArenaSakDTO> {
        val paramMap = mapOf(
            "person_id" to personId.toString(),
        )
        return txSession.run(
            queryOf(sqlFindTiltakspengerSakerByPersonId, paramMap)
                .map { row -> row.toSak(txSession, fnr) }
                .asList,
        )
    }

    private fun Row.toSak(txSession: TransactionalSession, fnr: String): ArenaSakDTO {
        val sakId = long("SAK_ID")
        val aar = int("AAR")
        val lopenrSak = long("LOPENRSAK")
        val kontekst = VedtakfaktaLoggkontekst(
            fnr = fnr,
            sakId = sakId,
            saksnummer = "$aar$lopenrSak",
        )
        val tiltakspengerVedtak = tiltakspengerVedtakDAO.findTiltakspengerBySakId(sakId, kontekst, txSession)
        val barnetilleggVedtak = barnetilleggVedtakDAO.findBarnetilleggBySakId(sakId, kontekst, txSession)

        return ArenaSakDTO(
            sakId = sakId,
            aar = aar,
            lopenrSak = lopenrSak,
            status = string("SAKSTATUSKODE").toStatus(),
            ytelsestype = string("SAKSKODE").toYtelse(),
            opprettetDato = localDate("REG_DATO"),
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
