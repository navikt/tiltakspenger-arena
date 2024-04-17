package no.nav.tiltakspenger.arena.tilgang

data class Saksbehandler(
    val navIdent: String,
    override val brukernavn: String,
    val epost: String,
    override val roller: List<Rolle>,
) : Bruker
