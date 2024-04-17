package no.nav.tiltakspenger.arena.tilgang

import io.ktor.server.application.ApplicationCall

interface InnloggetSaksbehandlerProvider {
    fun krevInnloggetSaksbehandler(call: ApplicationCall): Saksbehandler
}
