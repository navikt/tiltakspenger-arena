package no.nav.tiltakspenger.arena.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import mu.KotlinLogging
import no.nav.tiltakspenger.arena.routes.ArenaTiltakspengerRettighetPeriodeMapper.toArenaTiltakspengerRettighetPeriode
import no.nav.tiltakspenger.arena.routes.ArenaTiltakspengerVedtakPeriodeMapper.toArenaTiltakspengerVedtakPeriode
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.RettighetDetaljer
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.RettighetDetaljerService
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.VedtakDetaljer
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.VedtakDetaljerService
import no.nav.tiltakspenger.libs.periodisering.Periodisering

private val SECURELOG = KotlinLogging.logger("tjenestekall")

fun Route.tiltakspengerRoutesAzureAuth(
    vedtakDetaljerService: VedtakDetaljerService,
    rettighetDetaljerService: RettighetDetaljerService,
) {
    post("/azure/tiltakspenger/vedtaksperioder") {
        try {
            /*
            val bruker = if (call.getClaim("azure", "idtyp") != null) {
                // Systembruker
                innloggetSystembrukerProvider.krevInnloggetSystembruker(call)
            } else {
                // OBO
                innloggetSaksbehandlerProvider.krevInnloggetSaksbehandler(call)
            }
             */
            val ident = call.receive<RequestBody>().ident
            // SECURELOG.info { "$bruker spør om vedtaksperioder for $ident" }

            val periode: Periodisering<VedtakDetaljer>? =
                vedtakDetaljerService.hentVedtakDetaljerPerioder(ident = ident)
            call.respond(periode.toArenaTiltakspengerVedtakPeriode())
        } catch (e: Exception) {
            SECURELOG.warn("Feilet å hente tiltakspenger ${e.message}", e)
            call.respondText(text = e.message ?: e.toString(), status = HttpStatusCode.InternalServerError)
        }
    }

    post("/azure/tiltakspenger/rettighetsperioder") {
        try {
            /*
            val bruker = if (call.getClaim("azure", "idtyp") != null) {
                // Systembruker
                innloggetSystembrukerProvider.krevInnloggetSystembruker(call)
            } else {
                // OBO
                innloggetSaksbehandlerProvider.krevInnloggetSaksbehandler(call)
            }
             */
            val ident =
                call.receive<RequestBody>().ident
            // SECURELOG.info { "$bruker spør om rettighetsperioder for $ident" }

            val periode: Periodisering<RettighetDetaljer>? =
                rettighetDetaljerService.hentRettighetDetaljerPerioder(ident = ident)
            call.respond(periode.toArenaTiltakspengerRettighetPeriode())
        } catch (e: Exception) {
            SECURELOG.warn("Feilet å hente tiltakspenger ${e.message}", e)
            call.respondText(text = e.message ?: e.toString(), status = HttpStatusCode.InternalServerError)
        }
    }
}
