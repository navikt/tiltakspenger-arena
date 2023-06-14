package no.nav.tiltakspenger.arena.repository

import java.time.LocalDate

data class ArenaSakDTO(
    val aar: Int,
    val lopenrSak: Long,
    val status: ArenaSakStatus,
    val ytelsestype: ArenaYtelse,
    val ihtVedtak: List<ArenaVedtakDTO>,
) {
    val fomGyldighetsperiode: LocalDate?
        get() = ihtVedtak.mapNotNull { it.fomGyldighetsdato() }.minOrNull()
    val tomGyldighetsperiode: LocalDate?
        get() = ihtVedtak.mapNotNull { it.tomGyldighetsdato() }.maxOrNull()
    val datoKravMottatt: LocalDate
        get() = ihtVedtak.first { it.periodetypeForYtelse == ArenaVedtakType.O }.mottattDato
    val fagsystemSakId
        get() = aar + lopenrSak
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
