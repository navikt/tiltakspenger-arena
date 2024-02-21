package no.nav.tiltakspenger.arena.repository

import java.time.LocalDate
import java.time.LocalDateTime

data class ArenaVedtakDTO(
    val vedtakType: ArenaVedtakType,
    val uttaksgrad: Int,
    val fomVedtaksperiode: LocalDate?,
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
    fun fomGyldighetsdato(): LocalDateTime? = (fomVedtaksperiode ?: registrertDato)!!.atStartOfDay()
    fun tomGyldighetsdato(): LocalDateTime? = tomVedtaksperiode?.atStartOfDay()

    fun isTiltakspenger(): Boolean = this.rettighettype == ArenaRettighet.BASI
    fun isNotAvbruttOrNei(): Boolean = !(this.utfall == ArenaUtfall.AVBRUTT || this.utfall == ArenaUtfall.NEI)
    fun isNyRettighetOrGjenopptakOrEndring(): Boolean =
        this.vedtakType == ArenaVedtakType.O ||
            this.vedtakType == ArenaVedtakType.G ||
            this.vedtakType == ArenaVedtakType.E

    fun isFraDatoNotNull(): Boolean = this.fomVedtaksperiode != null

    fun isNotEngangsutbetaling(): Boolean =
        this.fomVedtaksperiode != null && // Litt usikker på denne..
            tomIsNullOrEqualToOrAfterFom(this.fomVedtaksperiode, this.tomVedtaksperiode)

    private fun tomIsNullOrEqualToOrAfterFom(fom: LocalDate, tom: LocalDate?) =
        tom == null || !tom.isBefore(fom)

    fun isVedtaksperiodeÅpen() = this.tomVedtaksperiode == null
}
