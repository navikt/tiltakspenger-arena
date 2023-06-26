package no.nav.tiltakspenger.arena.repository

import kotliquery.Row
import kotliquery.TransactionalSession
import kotliquery.queryOf
import mu.KotlinLogging
import org.intellij.lang.annotations.Language

class VedtakDAO(
    private val vedtakfaktaDAO: VedtakfaktaDAO = VedtakfaktaDAO(),
) {

    companion object {
        private val log = KotlinLogging.logger {}
        private val securelog = KotlinLogging.logger("tjenestekall")

        private fun String.toVedtakType(): ArenaVedtakType =
            ArenaVedtakType.valueOf(this)

        private fun String.toVedtakStatus(): ArenaVedtakStatus =
            ArenaVedtakStatus.valueOf(this)

        private fun String.toRettighetType(): ArenaRettighet =
            ArenaRettighet.valueOf(this)

        private fun String.toYtelseType(): ArenaYtelse =
            ArenaYtelse.valueOf(this)

        private fun String.toAktivitetFase(): ArenaAktivitetFase =
            ArenaAktivitetFase.valueOf(this)

        private fun String.toUtfall(): ArenaUtfall =
            ArenaUtfall.valueOf(this)
    }

    fun findBySakId(
        sakId: Long,
        txSession: TransactionalSession,
    ): List<ArenaVedtakDTO> {
        val paramMap = mapOf(
            "sak_id" to sakId.toString(),
        )
        return txSession.run(
            queryOf(findBySQL, paramMap)
                .map { row -> row.toVedtak(txSession) }
                .asList,
        )
    }

    private fun Row.toVedtak(txSession: TransactionalSession): ArenaVedtakDTO {
        val vedtakId = long("VEDTAK_ID")
        val vedtakFakta = vedtakfaktaDAO.findByVedtakId(vedtakId, txSession)
        return ArenaVedtakDTO(
            beslutningsdato = vedtakFakta.beslutningsdato(),
            periodetypeForYtelse = string("VEDTAKTYPEKODE").toVedtakType(),
            uttaksgrad = 100,
            status = string("VEDTAKSTATUSKODE").toVedtakStatus(),
            rettighettype = string("RETTIGHETKODE").toRettighetType(),
            aktivitetsfase = string("AKTFASEKODE").toAktivitetFase(),
            dagsats = vedtakFakta.dagsats(),
            fomVedtaksperiode = localDateOrNull("FRA_DATO"),
            tomVedtaksperiode = localDateOrNull("TIL_DATO"),
            mottattDato = localDate("DATO_MOTTATT"),
            registrertDato = localDateOrNull("REG_DATO"),
            utfall = string("UTFALLKODE").toUtfall(),
        )
    }

    @Language("SQL")
    private val findBySQL =
        """
        SELECT *
        FROM vedtak v
        WHERE v.sak_id = :sak_id
        AND v.rettighetkode = 'BASI'
        AND v.utfallkode != 'AVBRUTT'
        ORDER BY v.lopenrvedtak DESC
        """.trimIndent()
}
