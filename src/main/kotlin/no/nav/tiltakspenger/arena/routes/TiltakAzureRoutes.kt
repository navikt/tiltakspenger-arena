package no.nav.tiltakspenger.arena.routes

import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import mu.KotlinLogging
import no.nav.tiltakspenger.arena.auth.texas.TexasAuthEntraId
import no.nav.tiltakspenger.arena.auth.texas.client.TexasClient
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsClient
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsException
import no.nav.tiltakspenger.arena.tiltakogaktivitet.mapArenaTiltak
import no.nav.tiltakspenger.libs.arena.tiltak.ArenaTiltaksaktivitetResponsDTO
import no.nav.tiltakspenger.libs.logging.sikkerlogg

data class RequestBody(
    val ident: String,
)

private val logger = KotlinLogging.logger {}

fun Route.tiltakAzureRoutes(
    texasClient: TexasClient,
    arenaOrdsClient: ArenaOrdsClient,
) {
    route("/tiltakAzure") {
        install(TexasAuthEntraId) { client = texasClient }
        post {
            try {
                runBlocking(MDCContext()) {
                    val ident =
                        call.receive<RequestBody>().ident
                    logger.info { "Saksbehandler henter tiltak for innbygger" }
                    val arenaTiltak =
                        mapArenaTiltak(arenaOrdsClient.hentArenaAktiviteter(ident).response.tiltaksaktivitetListe)
                    call.respond(arenaTiltak)
                }
            } catch (e: ArenaOrdsException.PersonNotFoundException) {
                logger.warn { "Person ikke funnet i Arena Tiltak" }
                sikkerlogg.warn { "Person ikke funnet i Arena Tiltak ${e.message}" }
                call.respond(
                    ArenaTiltaksaktivitetResponsDTO(
                        tiltaksaktiviteter = emptyList(),
                        feil = null,
                    ),
                )
            }
        }
    }
}
