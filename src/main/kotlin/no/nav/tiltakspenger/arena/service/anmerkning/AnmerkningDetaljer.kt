package no.nav.tiltakspenger.arena.service.anmerkning

import no.nav.tiltakspenger.arena.repository.anmerkning.ArenaAnmerkningDTO
import java.time.LocalDateTime

class AnmerkningDetaljer(
    val kilde: String?,
    val registrert: LocalDateTime?,
    val beskrivelse: String?,
)

fun ArenaAnmerkningDTO.tilAnmerkningDetaljer(): AnmerkningDetaljer =
    AnmerkningDetaljer(
        kilde = this.kilde,
        registrert = this.regDato,
        beskrivelse = this.beskrivelse,
    )
