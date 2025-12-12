package no.nav.tiltakspenger.arena.routes

import no.nav.tiltakspenger.arena.service.vedtakdetaljer.Rettighet
import java.time.LocalDate

data class ArenaTiltakspengerVedtakPeriode(
    val fraOgMed: LocalDate,
    val tilOgMed: LocalDate?,
    val antallDager: Double,
    val dagsatsTiltakspenger: Int,
    val dagsatsBarnetillegg: Int,
    val antallBarn: Int,
    // TODO post-mvp jah: Endre til tiltakGjennomføringsId
    val relaterteTiltak: String,
    val rettighet: Rettighet,
    val vedtakId: Long,
    val sakId: Long,
    val beslutningsdato: LocalDate?,
)
