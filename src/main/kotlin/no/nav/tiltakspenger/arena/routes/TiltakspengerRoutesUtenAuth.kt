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

fun Route.tiltakspengerRoutesUtenAuth(
    vedtakDetaljerService: VedtakDetaljerService,
    rettighetDetaljerService: RettighetDetaljerService,
) {
    post("/kundev/tiltakspenger/vedtaksperioder") {
        try {
            val ident = call.receive<RequestBody>().ident
            val periode: Periodisering<VedtakDetaljer>? =
                vedtakDetaljerService.hentVedtakDetaljerPerioder(ident = ident)
            call.respond(periode.toArenaTiltakspengerVedtakPeriode())
        } catch (e: Exception) {
            SECURELOG.warn("Feilet å hente tiltakspenger ${e.message}", e)
            call.respondText(text = e.message ?: e.toString(), status = HttpStatusCode.InternalServerError)
        }
    }

    post("/kundev/tiltakspenger/rettighetsperioder") {
        try {
            val ident =
                call.receive<RequestBody>().ident
            val periode: Periodisering<RettighetDetaljer>? =
                rettighetDetaljerService.hentRettighetDetaljerPerioder(ident = ident)
            call.respond(periode.toArenaTiltakspengerRettighetPeriode())
        } catch (e: Exception) {
            SECURELOG.warn("Feilet å hente tiltakspenger ${e.message}", e)
            call.respondText(text = e.message ?: e.toString(), status = HttpStatusCode.InternalServerError)
        }
    }
}
