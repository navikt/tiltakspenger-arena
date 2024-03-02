package no.nav.tiltakspenger.arena.repository

import no.nav.tiltakspenger.arena.felles.Periode
import java.time.LocalDate
import java.time.LocalDateTime

data class ArenaSakDTO(
    val aar: Int,
    val lopenrSak: Long,
    val status: ArenaSakStatus,
    val ytelsestype: ArenaYtelse,
    val tiltakspengerVedtak: List<ArenaTiltakspengerVedtakDTO>,
    val barnetilleggVedtak: List<ArenaBarnetilleggVedtakDTO>,
) {
    // Denne vil feile på saker uten vedtak, men de skal filtreres bort!
    val fomGyldighetsperiode: LocalDateTime
        get() = tiltakspengerVedtak.minOf { it.fomGyldighetstidspunkt() }
    val tomGyldighetsperiode: LocalDateTime?
        get() = tiltakspengerVedtak.mapNotNull { it.tomGyldighetstidspunkt() }.maxOrNull()
    val datoKravMottatt: LocalDate
        get() = tiltakspengerVedtak.first { it.vedtakType == ArenaVedtakType.O }.mottattDato
    val fagsystemSakId: String
        get() = aar.toString() + lopenrSak

    fun harTiltakspengerVedtak(): Boolean = this.tiltakspengerVedtak.isNotEmpty()

    fun harTiltakspengerVedtakMedÅpenPeriode(): Boolean =
        this.tiltakspengerVedtak.any { it.isVedtaksperiodeÅpen() }

    fun sakPeriode(): Periode? {
        val fraDato = tiltakspengerVedtak.minOfOrNull { it.fomVedtaksperiode }
        val tilDato = if (tiltakspengerVedtak.map { it.tomVedtaksperiode }.contains(null)) {
            LocalDate.MAX
        } else {
            tiltakspengerVedtak.mapNotNull { it.tomVedtaksperiode }.maxOrNull() ?: LocalDate.MAX
        }
        return fraDato?.let { Periode(it, tilDato) }
    }
}

// Jeg plasserer denne her og ikke der den brukes da det føles mer naturlig å både lese og teste denne i sammenheng med
// funksjonene som er inni klassen.
fun List<ArenaSakDTO>.kunSakerMedVedtakInnenforPeriode(fom: LocalDate, tom: LocalDate): List<ArenaSakDTO> =
    this.filter { sak -> sak.harTiltakspengerVedtak() }
        .filter { sak -> sak.sakPeriode()?.overlapperMed(Periode(fom, tom)) ?: false }
