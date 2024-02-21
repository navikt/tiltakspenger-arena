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

    fun findAlleBySakId(
        sakId: Long,
        txSession: TransactionalSession,
    ): List<ArenaVedtakDTO> {
        val paramMap = mapOf(
            "sak_id" to sakId,
        )
        return txSession.run(
            queryOf(findBySQL, paramMap)
                .map { row -> row.toVedtak(txSession) }
                .asList,
        )
    }

    fun findBySakId(
        sakId: Long,
        txSession: TransactionalSession,
    ): List<ArenaVedtakDTO> = findAlleBySakId(sakId, txSession)
        .asSequence()
        // En del av filterne her gjøres også i SQL-koden, så det er overflødig..
        // TODO: Må sjekke om fraDato kan være null
        .filter { it.isTiltakspenger() }
        .filter { it.isNotAvbruttOrNei() }
        .filter { it.isNyRettighetOrGjenopptakOrEndring() }
        .filter { it.isFraDatoNotNull() }
        .filter { it.isNotEngangsutbetaling() }
        .toList()

    private fun Row.toVedtak(txSession: TransactionalSession): ArenaVedtakDTO {
        val vedtakId = long("VEDTAK_ID")
        val vedtakFakta = vedtakfaktaDAO.findByVedtakId(vedtakId, txSession)
        val dto = ArenaVedtakDTO(
            beslutningsdato = vedtakFakta.beslutningsdato,
            vedtakType = string("VEDTAKTYPEKODE").toVedtakType(),
            uttaksgrad = 100,
            status = string("VEDTAKSTATUSKODE").toVedtakStatus(),
            rettighettype = string("RETTIGHETKODE").toRettighetType(),
            aktivitetsfase = string("AKTFASEKODE").toAktivitetFase(),
            dagsats = vedtakFakta.dagsats,
            fomVedtaksperiode = localDateOrNull("FRA_DATO"),
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

    // Vi vil bare ha positive vedtak,
    // da det vi skal finne ut av er når brukeren har tiltakspenger
    @Language("SQL")
    private val findBySQL =
        """
        SELECT *
        FROM vedtak v
        WHERE v.sak_id = :sak_id
        -- AND v.rettighetkode IN ('BASI', 'BTIL') -- Venter litt med BTIL (Barnetillegg)
        AND v.rettighetkode = 'BASI'
        AND v.vedtaktypekode IN ('O', 'E', 'G') --Ny rettighet, endring, gjenopptak
        AND v.utfallkode NOT IN ('AVBRUTT', 'NEI') --Vi vil bare ha positive vedtak
        ORDER BY v.lopenrvedtak DESC
        """.trimIndent()
}
