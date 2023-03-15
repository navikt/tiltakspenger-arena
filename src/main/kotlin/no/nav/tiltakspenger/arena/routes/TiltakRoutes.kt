package no.nav.tiltakspenger.arena.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import mu.KotlinLogging
import no.nav.tiltakspenger.arena.fødselsnummer
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsClient
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsException
import no.nav.tiltakspenger.arena.tiltakogaktivitet.mapArenaTiltak
import no.nav.tiltakspenger.libs.arena.tiltak.ArenaTiltaksaktivitetResponsDTO

private val SECURELOG = KotlinLogging.logger("tjenestekall")
fun Route.tiltakRoutes(arenaOrdsClient: ArenaOrdsClient) {
    get("/tiltak") {
        try {
            runBlocking(MDCContext()) {
                val ident = call.fødselsnummer() ?: throw IllegalStateException("Mangler fødselsnummer")
                val arenaTiltak = mapArenaTiltak(arenaOrdsClient.hentArenaAktiviteter(ident).response.tiltaksaktivitetListe)
                call.respond(arenaTiltak)
            }
        } catch (e: ArenaOrdsException.PersonNotFoundException) {
            SECURELOG.warn { "Person ikke funnet i Arena Tiltak ${e.message}" }
            call.respond(ArenaTiltaksaktivitetResponsDTO(
                tiltaksaktiviteter = emptyList(),
                feil = null,
            ))
        } catch (e: IllegalStateException) {
            SECURELOG.warn { "Mangler fødselsnummer" }
            call.respondText(text = "Bad Request", status = HttpStatusCode.BadRequest)
        }
    }
}
