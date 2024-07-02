package no.nav.tiltakspenger.arena.routes

import java.time.LocalDate

data class ArenaTiltakspengerRettighetPeriode(
    val fraOgMed: LocalDate,
    val tilOgMed: LocalDate?,
)
