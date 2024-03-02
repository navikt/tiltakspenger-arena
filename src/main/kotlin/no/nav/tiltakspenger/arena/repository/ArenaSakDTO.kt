package no.nav.tiltakspenger.arena.repository

import java.time.LocalDate
import java.time.LocalDateTime

data class ArenaSakDTO(
    val aar: Int,
    val lopenrSak: Long,
    val status: ArenaSakStatus,
    val ytelsestype: ArenaYtelse,
    val vedtak: List<ArenaVedtakDTO>,
) {
    val fomGyldighetsperiode: LocalDateTime
        get() = vedtak.mapNotNull { it.fomGyldighetsdato() }.min()
    val tomGyldighetsperiode: LocalDateTime?
        get() = vedtak.mapNotNull { it.tomGyldighetsdato() }.maxOrNull()
    val datoKravMottatt: LocalDate
        get() = vedtak.first { it.vedtakType == ArenaVedtakType.O }.mottattDato
    val fagsystemSakId: String
        get() = aar.toString() + lopenrSak

    fun harVedtakMedÅpenPeriode(): Boolean =
        this.vedtak.any { it.isVedtaksperiodeÅpen() }

    fun førsteFomVedtaksperiodeIsBeforeOrEqualTo(date: LocalDate): Boolean =
        this.vedtak
            .mapNotNull { it.fomVedtaksperiode }
            .minOrNull()
            ?.let { it == date || it.isBefore(date) } ?: true

    fun sisteVedtakMedLukketPeriodeIsAfterOrEqualTo(date: LocalDate): Boolean =
        this.vedtak
            .mapNotNull { it.tomVedtaksperiode }
            .maxOrNull()
            ?.let { it == date || it.isAfter(date) } ?: true

    fun harVedtak(): Boolean = this.vedtak.isNotEmpty()
}

// Jeg plasserer denne her og ikke der den brukes da det føles mer naturlig å både lese og teste denne i sammenheng med
// funksjonene som er inni klassen.
fun List<ArenaSakDTO>.kunSakerMedVedtakInnenforPeriode(fom: LocalDate, tom: LocalDate): List<ArenaSakDTO> =
    this.filter { sak -> sak.harVedtak() }
        .filter { sak -> sak.førsteFomVedtaksperiodeIsBeforeOrEqualTo(tom) }
        .filter { sak ->
            sak.harVedtakMedÅpenPeriode() || (sak.sisteVedtakMedLukketPeriodeIsAfterOrEqualTo(fom))
        }
