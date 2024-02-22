package no.nav.tiltakspenger.arena.repository

import java.time.LocalDate
import java.time.LocalDateTime

data class ArenaVedtakDTO(
    val vedtakType: ArenaVedtakType,
    val uttaksgrad: Int,
    val fomVedtaksperiode: LocalDate,
    val tomVedtaksperiode: LocalDate?,
    val status: ArenaVedtakStatus,
    val rettighettype: ArenaRettighet,
    val aktivitetsfase: ArenaAktivitetFase,
    val dagsats: Int?,
    val beslutningsdato: LocalDate?,
    val mottattDato: LocalDate,
    val registrertDato: LocalDate?,
    val utfall: ArenaUtfall,
    val antallDager: Double?,
    val opprinneligTomVedtaksperiode: LocalDate?,
    val relatertTiltak: String?,
    val antallBarn: Int?,
) {
    // Har en hypotese om at fomVedtaksperiode aldri er null på iverksatte vedtak, så har gjort den ikke-nullable.
    // Men venter med å endre på koden

    fun fomGyldighetsdato(): LocalDateTime? = (fomVedtaksperiode ?: registrertDato)!!.atStartOfDay()
    fun tomGyldighetsdato(): LocalDateTime? = tomVedtaksperiode?.atStartOfDay()

    fun isTiltakspenger(): Boolean = this.rettighettype == ArenaRettighet.BASI
    fun isIverksatt(): Boolean = this.status == ArenaVedtakStatus.IVERK
    fun isNotAvbruttOrNei(): Boolean = !(this.utfall == ArenaUtfall.AVBRUTT || this.utfall == ArenaUtfall.NEI)
    fun isNyRettighetOrGjenopptakOrEndring(): Boolean =
        this.vedtakType == ArenaVedtakType.O ||
            this.vedtakType == ArenaVedtakType.G ||
            this.vedtakType == ArenaVedtakType.E

    fun isFraDatoNotNull(): Boolean = this.fomVedtaksperiode != null

    fun isNotEngangsutbetaling(): Boolean =
        isVedtaksperiodeÅpen() || !isEngangsutbetaling()

    private fun isEngangsutbetaling(): Boolean =
        this.tomVedtaksperiode != null && this.tomVedtaksperiode.isBefore(this.fomVedtaksperiode)

    fun isVedtaksperiodeÅpen(): Boolean = this.tomVedtaksperiode == null
}
