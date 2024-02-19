package no.nav.tiltakspenger.arena.routes

import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import mu.KotlinLogging
import no.nav.tiltakspenger.arena.felles.Periode
import no.nav.tiltakspenger.arena.service.TiltakepengerPerioderService
import java.time.LocalDate

private val SECURELOG = KotlinLogging.logger("tjenestekall")

fun Route.tiltakspengerRoutesUtenAuth(service: TiltakepengerPerioderService) {
    post("/tiltakspengerUten") {
        try {
            val ident =
                call.receive<RequestBody>().ident
            val sammenhengendePerioder: List<Periode> =
                service.hentTiltakspengerPerioder(ident = ident, fom = LocalDate.MIN, tom = LocalDate.MAX)
            call.respond(sammenhengendePerioder)
        } catch (e: Exception) {
            SECURELOG.warn("Feil i kall mot Arena for å hente tiltakspenger ${e.message}", e)
            call.respond(emptyList<Periode>())
        }
    }
}