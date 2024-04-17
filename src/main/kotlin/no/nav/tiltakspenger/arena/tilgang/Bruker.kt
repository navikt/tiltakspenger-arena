package no.nav.tiltakspenger.arena.tilgang

interface Bruker {
    val brukernavn: String
    val roller: List<Rolle>

    fun harKode6() = roller.contains(Rolle.STRENGT_FORTROLIG_ADRESSE)
    fun harKode7() = roller.contains(Rolle.FORTROLIG_ADRESSE)
    fun harEgenAnsatt() = roller.contains(Rolle.SKJERMING)
}
