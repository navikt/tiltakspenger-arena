package no.nav.tiltakspenger.arena.tilgang

data class Systembruker(
    override val brukernavn: String,
    override val roller: List<Rolle>,
) : Bruker
