package no.nav.tiltakspenger.arena

import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaAktiviteterDTO

data class Response(
    val tiltaksaktiviteter: List<ArenaAktiviteterDTO.Tiltaksaktivitet>?,
    val feil: Feilmelding?,
)
