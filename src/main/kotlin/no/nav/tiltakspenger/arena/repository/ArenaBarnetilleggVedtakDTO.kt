package no.nav.tiltakspenger.arena.repository

import no.nav.tiltakspenger.libs.periodisering.Periode
import java.time.LocalDate
import java.time.LocalDateTime

data class ArenaBarnetilleggVedtakDTO(
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
    fun fomGyldighetstidspunkt(): LocalDateTime = fomVedtaksperiode.atStartOfDay()
    fun tomGyldighetstidspunkt(): LocalDateTime? = tomVedtaksperiode?.atStartOfDay()

    fun isTiltakspenger(): Boolean = this.rettighettype == ArenaRettighet.BASI
    fun isIverksatt(): Boolean = this.status == ArenaVedtakStatus.IVERK
    fun isNotAvbruttOrNei(): Boolean = !(this.utfall == ArenaUtfall.AVBRUTT || this.utfall == ArenaUtfall.NEI)
    fun isNyRettighetOrGjenopptakOrEndring(): Boolean =
        this.vedtakType == ArenaVedtakType.O ||
            this.vedtakType == ArenaVedtakType.G ||
            this.vedtakType == ArenaVedtakType.E

    fun vedtaksperiode(): Periode = Periode(fomVedtaksperiode, tomVedtaksperiode ?: LocalDate.MAX)

    fun isVedtaksperiodeÅpen(): Boolean = this.tomVedtaksperiode == null
}
