package no.nav.tiltakspenger.arena.repository

import mu.KotlinLogging
import no.nav.tiltakspenger.arena.felles.Periode
import java.time.LocalDate
import java.time.LocalDateTime

private val LOG = KotlinLogging.logger {}
private val SECURELOG = KotlinLogging.logger("tjenestekall")

data class ArenaSakDTO(
    val sakId: Long,
    val aar: Int,
    val lopenrSak: Long,
    val status: ArenaSakStatus,
    val ytelsestype: ArenaYtelse,
    val tiltakspengerVedtak: List<ArenaTiltakspengerVedtakDTO>,
    val barnetilleggVedtak: List<ArenaBarnetilleggVedtakDTO>,
) {

    val fomGyldighetsperiode: LocalDateTime?
        get() = tiltakspengerVedtak.minOfOrNull { it.fomGyldighetstidspunkt() }
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

    fun logVedtakUtenAlleForventedeVerdier() {
        this.tiltakspengerVedtak.forEach {
            if (it.dagsats == null) LOG.warn { "Dagsats er ikke med for tiltakspengervedtak med vedtaksperiode ${it.vedtaksperiode()} for sak med løpenr ${this.lopenrSak}" }
            if (it.antallDager == null) LOG.warn { "Antall dager er ikke med for tiltakspengervedtak med vedtaksperiode ${it.vedtaksperiode()} for sak med løpenr ${this.lopenrSak}" }
            if (it.relatertTiltak == null) LOG.warn { "RelatertTiltak er ikke med for tiltakspengervedtak med vedtaksperiode ${it.vedtaksperiode()} for sak med løpenr ${this.lopenrSak}" }
            if (it.dagsats == null) LOG.warn { "Dagsats er ikke med for tiltakspengervedtak med vedtaksperiode ${it.vedtaksperiode()} for sak med løpenr ${this.lopenrSak}" }
        }
        this.barnetilleggVedtak.forEach {
            if (it.dagsats == null) LOG.warn { "Dagsats er ikke med for barnetilleggvedtak med vedtaksperiode ${it.vedtaksperiode()} for sak med løpenr ${this.lopenrSak}" }
            if (it.antallDager == null) LOG.warn { "Antall dager er ikke med for barnetilleggvedtak med vedtaksperiode ${it.vedtaksperiode()} for sak med løpenr ${this.lopenrSak}" }
            if (it.relatertTiltak == null) LOG.warn { "RelatertTiltak er ikke med for barnetilleggvedtak med vedtaksperiode ${it.vedtaksperiode()} for sak med løpenr ${this.lopenrSak}" }
            if (it.antallBarn == null) LOG.warn { "Antall barn er ikke med for barnetilleggvedtak med vedtaksperiode ${it.vedtaksperiode()} for sak med løpenr ${this.lopenrSak}" }
        }
    }
}

// Jeg plasserer denne her og ikke der den brukes da det føles mer naturlig å både lese og teste denne i sammenheng med
// funksjonene som er inni klassen.
fun List<ArenaSakDTO>.kunSakerMedVedtakInnenforPeriode(fom: LocalDate, tom: LocalDate): List<ArenaSakDTO> =
    this.filter { sak -> sak.harTiltakspengerVedtak() }
        .filter { sak -> sak.sakPeriode()?.overlapperMed(Periode(fom, tom)) ?: false }

fun List<ArenaSakDTO>.logVedtakMedUgyldigeVerdier(): List<ArenaSakDTO> {
    this.forEach { sak ->
        sak.logVedtakUtenAlleForventedeVerdier()
    }
    return this
}
