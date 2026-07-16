package no.nav.tiltakspenger.arena.repository.vedtak

import kotliquery.Row
import kotliquery.TransactionalSession
import kotliquery.queryOf
import no.nav.tiltakspenger.arena.repository.ArenaAktivitetFase
import no.nav.tiltakspenger.arena.repository.ArenaRettighet
import no.nav.tiltakspenger.arena.repository.ArenaUtfall
import no.nav.tiltakspenger.arena.repository.ArenaVedtakStatus
import no.nav.tiltakspenger.arena.repository.ArenaVedtakType
import no.nav.tiltakspenger.arena.repository.vedtakfakta.VedtakfaktaDAO
import no.nav.tiltakspenger.arena.repository.vedtakfakta.VedtakfaktaLoggkontekst
import org.intellij.lang.annotations.Language

class TiltakspengerVedtakDAO(
    private val vedtakfaktaDAO: VedtakfaktaDAO = VedtakfaktaDAO(),
) {

    companion object {
        private fun String.toVedtakType(): ArenaVedtakType =
            ArenaVedtakType.valueOf(this)

        private fun String.toVedtakStatus(): ArenaVedtakStatus =
            ArenaVedtakStatus.valueOf(this)

        private fun String.toRettighetType(): ArenaRettighet =
            ArenaRettighet.valueOf(this)

        private fun String.toAktivitetFase(): ArenaAktivitetFase =
            ArenaAktivitetFase.valueOf(this)

        private fun String.toUtfall(): ArenaUtfall =
            ArenaUtfall.valueOf(this)
    }

    private fun findAlleTiltakspengerBySakId(
        sakId: Long,
        kontekst: VedtakfaktaLoggkontekst,
        txSession: TransactionalSession,
    ): List<ArenaTiltakspengerVedtakDTO> {
        val paramMap = mapOf(
            "sak_id" to sakId,
        )
        return txSession.run(
            queryOf(sqlFindTiltakspengerVedtakOgFiltrerBortUønskede, paramMap)
                .map { row -> row.toVedtak(txSession, sakId, kontekst) }
                .asList,
        )
    }

    fun findTiltakspengerBySakId(
        sakId: Long,
        kontekst: VedtakfaktaLoggkontekst,
        txSession: TransactionalSession,
    ): List<ArenaTiltakspengerVedtakDTO> = findAlleTiltakspengerBySakId(sakId, kontekst, txSession)

    private fun Row.toVedtak(txSession: TransactionalSession, sakId: Long, kontekst: VedtakfaktaLoggkontekst): ArenaTiltakspengerVedtakDTO {
        val vedtakId = long("VEDTAK_ID")
        val vedtakFakta = vedtakfaktaDAO.findTiltakspengerVedtakfaktaByVedtakId(vedtakId, kontekst, txSession)
        return ArenaTiltakspengerVedtakDTO(
            vedtakId = vedtakId,
            tilhørendeSakId = sakId,
            beslutningsdato = vedtakFakta.beslutningsdato,
            vedtakType = string("VEDTAKTYPEKODE").toVedtakType(),
            uttaksgrad = 100,
            status = string("VEDTAKSTATUSKODE").toVedtakStatus(),
            rettighettype = string("RETTIGHETKODE").toRettighetType(),
            aktivitetsfase = string("AKTFASEKODE").toAktivitetFase(),
            dagsats = vedtakFakta.dagsats,
            fomVedtaksperiode = localDate("FRA_DATO"),
            tomVedtaksperiode = localDateOrNull("TIL_DATO"),
            mottattDato = localDate("DATO_MOTTATT"),
            registrertDato = localDateOrNull("REG_DATO"),
            utfall = string("UTFALLKODE").toUtfall(),
            antallDager = vedtakFakta.antallDager,
            opprinneligTomVedtaksperiode = vedtakFakta.opprinneligTilDato,
            relatertTiltak = vedtakFakta.relatertTiltak,
        )
    }

    // Vi gjør filtreringen her i stedet for i Kotlin-koden, da de ulike where-clausene er ganske enkle å forstå,
    // og det er kjappere å filtrere i db.
    @Language("SQL")
    private val sqlFindTiltakspengerVedtakOgFiltrerBortUønskede =
        """
        SELECT *
        FROM vedtak v
        WHERE v.sak_id = :sak_id
        AND v.rettighetkode = 'BASI'
        AND v.vedtaktypekode IN ('O', 'E', 'G') --Ny rettighet, endring, gjenopptak
        AND v.utfallkode NOT IN ('AVBRUTT', 'NEI') --Vi vil bare ha positive vedtak
        AND v.vedtakstatuskode IN ('IVERK', 'AVSLU') --Vi vil bare ha vedtak som faktisk er vedtatt
        AND v.fra_dato IS NOT NULL --Ekskluderer spesialutbetalinger
        AND v.fra_dato <= NVL(v.til_dato, v.fra_dato) --Ekskluderer ugyldiggjorte vedtak
        ORDER BY v.lopenrvedtak DESC
        """.trimIndent()
}
