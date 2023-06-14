package no.nav.tiltakspenger.arena.repository

import java.time.LocalDate

data class ArenaVedtakDTO(
    val periodetypeForYtelse: ArenaVedtakType,
    val uttaksgrad: Int,
    val vedtakBruttoBeloep: Int?,
    val vedtakNettoBeloep: Int?,
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
) {
    fun fomGyldighetsdato(): LocalDate? = fomVedtaksperiode ?: registrertDato
    fun tomGyldighetsdato(): LocalDate? = tomVedtaksperiode
}
