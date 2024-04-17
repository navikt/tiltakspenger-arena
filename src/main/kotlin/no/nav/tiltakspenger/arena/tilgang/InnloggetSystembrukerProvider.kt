package no.nav.tiltakspenger.arena.tilgang

import io.ktor.server.application.ApplicationCall

interface InnloggetSystembrukerProvider {
    fun krevInnloggetSystembruker(call: ApplicationCall): Systembruker
}
