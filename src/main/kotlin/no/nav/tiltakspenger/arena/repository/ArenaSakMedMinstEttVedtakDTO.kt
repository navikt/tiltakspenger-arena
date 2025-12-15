package no.nav.tiltakspenger.arena.repository

import no.nav.tiltakspenger.libs.periodisering.Periode
import java.time.LocalDate
import java.time.LocalDateTime

class ArenaSakMedMinstEttVedtakDTO(
    val sakId: Long,
    val aar: Int,
    val lopenrSak: Long,
    val status: ArenaSakStatus,
    val ytelsestype: ArenaYtelse,
    val opprettetDato: LocalDate,
    val tiltakspengerVedtak: List<ArenaTiltakspengerVedtakDTO>,
    val barnetilleggVedtak: List<ArenaBarnetilleggVedtakDTO>,
) {

    companion object {
        operator fun invoke(arenaSakDTO: ArenaSakDTO): ArenaSakMedMinstEttVedtakDTO {
            if (arenaSakDTO.tiltakspengerVedtak.isEmpty()) {
                throw IllegalStateException("Kan ikke opprette ArenaSakMedMinstEttVedtakDTO når saken ikke har vedtak")
            }
            return ArenaSakMedMinstEttVedtakDTO(
                sakId = arenaSakDTO.sakId,
                aar = arenaSakDTO.aar,
                lopenrSak = arenaSakDTO.lopenrSak,
                status = arenaSakDTO.status,
                ytelsestype = arenaSakDTO.ytelsestype,
                opprettetDato = arenaSakDTO.opprettetDato,
                tiltakspengerVedtak = arenaSakDTO.tiltakspengerVedtak,
                barnetilleggVedtak = arenaSakDTO.barnetilleggVedtak,
            )
        }
    }

    val fomGyldighetsperiode: LocalDateTime
        get() = tiltakspengerVedtak.minOf { it.fomGyldighetstidspunkt() }
    val tomGyldighetsperiode: LocalDateTime?
        get() = tiltakspengerVedtak.mapNotNull { it.tomGyldighetstidspunkt() }.maxOrNull()

    val datoKravMottatt: LocalDate
        get() = tiltakspengerVedtak.first { it.vedtakType == ArenaVedtakType.O }.mottattDato
    val fagsystemSakId: String
        get() = aar.toString() + lopenrSak

    fun harTiltakspengerVedtakMedÅpenPeriode(): Boolean =
        this.tiltakspengerVedtak.any { it.isVedtaksperiodeÅpen() }

    fun sakPeriode(): Periode {
        val fraDato: LocalDate = tiltakspengerVedtak.minOf { it.fomVedtaksperiode }
        val tilDato: LocalDate = if (tiltakspengerVedtak.map { it.tomVedtaksperiode }.contains(null)) {
            LocalDate.MAX
        } else {
            tiltakspengerVedtak.mapNotNull { it.tomVedtaksperiode }.maxOrNull() ?: LocalDate.MAX
        }
        return Periode(fraDato, tilDato)
    }
}
