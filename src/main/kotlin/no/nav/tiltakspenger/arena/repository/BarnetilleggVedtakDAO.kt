package no.nav.tiltakspenger.arena.repository

import kotliquery.Row
import kotliquery.TransactionalSession
import kotliquery.queryOf
import mu.KotlinLogging
import org.intellij.lang.annotations.Language

class BarnetilleggVedtakDAO(
    private val vedtakfaktaDAO: VedtakfaktaDAO = VedtakfaktaDAO(),
) {

    companion object {
        private val LOG = KotlinLogging.logger {}
        private val SECURELOG = KotlinLogging.logger("tjenestekall")

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

    private fun findAlleBarnetilleggBySakId(
        sakId: Long,
        txSession: TransactionalSession,
    ): List<ArenaBarnetilleggVedtakDTO> {
        val paramMap = mapOf(
            "sak_id" to sakId,
        )
        return txSession.run(
            queryOf(sqlFindBarnetilleggVedtakOgFiltrerBortUønskede, paramMap)
                .map { row -> row.toVedtak(txSession, sakId) }
                .asList,
        )
    }

    fun findBarnetilleggBySakId(
        sakId: Long,
        txSession: TransactionalSession,
    ): List<ArenaBarnetilleggVedtakDTO> = findAlleBarnetilleggBySakId(sakId, txSession)

    private fun Row.toVedtak(txSession: TransactionalSession, sakId: Long): ArenaBarnetilleggVedtakDTO {
        val vedtakId = long("VEDTAK_ID")
        val vedtakFakta = vedtakfaktaDAO.findBarnetilleggVedtakfaktaByVedtakId(vedtakId, txSession)
        val dto = ArenaBarnetilleggVedtakDTO(
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
            antallBarn = vedtakFakta.antallBarn,
        )

        if (!(dto.status == ArenaVedtakStatus.GODKJ || dto.status == ArenaVedtakStatus.IVERK)) {
            LOG.info { "VedtakStatusType er ${dto.status}" }
        }
        return dto
    }

    // Vi gjør filtreringen her i stedet for i Kotlin-koden, da de ulike where-clausene er ganske enkle å forstå,
    // og det er kjappere å filtrere i db.
    @Language("SQL")
    private val sqlFindBarnetilleggVedtakOgFiltrerBortUønskede =
        """
        SELECT *
        FROM vedtak v
        WHERE v.sak_id = :sak_id
        AND v.rettighetkode = 'BTIL'
        AND v.vedtaktypekode IN ('O', 'E', 'G') --Ny rettighet, endring, gjenopptak
        AND v.utfallkode NOT IN ('AVBRUTT', 'NEI') --Vi vil bare ha positive vedtak
        AND v.vedtakstatuskode IN ('IVERK', 'AVSLU') --Vi vil bare ha vedtak som faktisk er vedtatt
        AND v.fra_dato IS NOT NULL --Ekskluderer spesialutbetalinger
        AND v.fra_dato <= NVL(v.til_dato, v.fra_dato) --Ekskluderer ugyldiggjorte vedtak
        ORDER BY v.lopenrvedtak DESC
        """.trimIndent()
}
