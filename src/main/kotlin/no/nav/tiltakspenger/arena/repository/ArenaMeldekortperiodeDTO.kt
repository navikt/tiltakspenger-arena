package no.nav.tiltakspenger.arena.repository

import java.time.LocalDate

class ArenaMeldekortperiodeDTO(
    val aar: Int,
    val periodekode: Int,
    val ukenrUke1: Int,
    val ukenrUke2: Int,
    val datoFra: LocalDate, // fra og med
    val datoTil: LocalDate, // til og med
)
