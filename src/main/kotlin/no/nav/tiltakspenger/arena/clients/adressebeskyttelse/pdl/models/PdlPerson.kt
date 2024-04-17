package no.nav.tiltakspenger.arena.clients.adressebeskyttelse.pdl.models

data class PdlPerson(
    val navn: List<Navn>,
    val foedsel: List<Fødsel>,
    val adressebeskyttelse: List<Adressebeskyttelse>,
    val forelderBarnRelasjon: List<ForelderBarnRelasjon>,
)
