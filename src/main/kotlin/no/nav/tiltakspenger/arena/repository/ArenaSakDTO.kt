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
        get() = ihtVedtak.first { it.periodetypeForYtelse == ArenaVedtakType.O }.mottattDato
    val fagsystemSakId: String
        get() = aar.toString() + lopenrSak
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
