package no.nav.tiltakspenger.arena.repository

import java.time.LocalDate
import java.time.LocalDateTime

data class ArenaSakDTO(
    val aar: Int,
    val lopenrSak: Long,
    val status: ArenaSakStatus,
    val ytelsestype: ArenaYtelse,
    val ihtVedtak: List<ArenaVedtakDTO>,
) {
    val fomGyldighetsperiode: LocalDateTime
        get() = ihtVedtak.mapNotNull { it.fomGyldighetsdato() }.min()
    val tomGyldighetsperiode: LocalDateTime?
        get() = ihtVedtak.mapNotNull { it.tomGyldighetsdato() }.maxOrNull()
    val datoKravMottatt: LocalDate
        get() = ihtVedtak.first { it.vedtakType == ArenaVedtakType.O }.mottattDato
    val fagsystemSakId: String
        get() = aar.toString() + lopenrSak

    fun harVedtakMedÅpenPeriode(): Boolean =
        this.ihtVedtak.any { it.isVedtaksperiodeÅpen() }

    fun førsteFomVedtaksperiodeIsBeforeOrEqualTo(date: LocalDate): Boolean =
        this.ihtVedtak
            .mapNotNull { it.fomVedtaksperiode }
            .minOrNull()
            ?.let { it == date || it.isBefore(date) } ?: true

    fun sisteVedtakMedLukketPeriodeIsAfterOrEqualTo(date: LocalDate): Boolean =
        this.ihtVedtak
            .mapNotNull { it.tomVedtaksperiode }
            .maxOrNull()
            ?.let { it == date || it.isAfter(date) } ?: true

    fun harVedtak(): Boolean = this.ihtVedtak.isNotEmpty()
}

fun List<ArenaSakDTO>.kunSakerMedVedtakInnenforPeriode(fom: LocalDate, tom: LocalDate) =
    this.filter { sak -> sak.harVedtak() }
        .filter { sak -> sak.førsteFomVedtaksperiodeIsBeforeOrEqualTo(tom) }
        .filter { sak ->
            sak.harVedtakMedÅpenPeriode() || (sak.sisteVedtakMedLukketPeriodeIsAfterOrEqualTo(fom))
        }

/*
ArenaDagpengeSakDTO må evt også ha feltene
    protected int antallDagerIgjen;
    protected int antallUkerIgjen;
    protected Integer antallDagerIgjenUnderPermittering;
    protected Integer antallUkerIgjenUnderPermittering;

Dagpenger og AAP må ha feltene:
    val bortfallsprosentDagerIgjen: Int?,
    val bortfallsprosentUkerIgjen: Int?,
*/
