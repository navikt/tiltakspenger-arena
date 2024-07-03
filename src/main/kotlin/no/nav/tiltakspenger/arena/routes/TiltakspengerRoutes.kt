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
import java.time.LocalDate

private val SECURELOG = KotlinLogging.logger("tjenestekall")

fun Route.tiltakspengerRoutes(
    vedtakDetaljerService: VedtakDetaljerService,
    rettighetDetaljerService: RettighetDetaljerService,
) {
    post("/azure/tiltakspenger/vedtaksperioder") {
        try {
            val req = call.receive<VedtakRequest>()
            val periode: Periodisering<VedtakDetaljer>? =
                vedtakDetaljerService.hentVedtakDetaljerPerioder(
                    ident = req.ident,
                    fom = req.fom ?: LocalDate.of(1900, 1, 1),
                    tom = req.tom ?: LocalDate.of(2999, 12, 31),
                )
            call.respond(periode.toArenaTiltakspengerVedtakPeriode())
        } catch (e: Exception) {
            SECURELOG.warn("Feilet å hente tiltakspenger ${e.message}", e)
            call.respondText(text = e.message ?: e.toString(), status = HttpStatusCode.InternalServerError)
        }
    }

    post("/azure/tiltakspenger/rettighetsperioder") {
        try {
            val req = call.receive<VedtakRequest>()
            val periode: Periodisering<RettighetDetaljer>? =
                rettighetDetaljerService.hentRettighetDetaljerPerioder(
                    ident = req.ident,
                    fom = req.fom ?: LocalDate.of(1900, 1, 1),
                    tom = req.tom ?: LocalDate.of(2999, 12, 31),
                )
            call.respond(periode.toArenaTiltakspengerRettighetPeriode())
        } catch (e: Exception) {
            SECURELOG.warn("Feilet å hente tiltakspenger ${e.message}", e)
            call.respondText(text = e.message ?: e.toString(), status = HttpStatusCode.InternalServerError)
        }
    }
}

data class VedtakRequest(
    val ident: String,
    val fom: LocalDate?,
    val tom: LocalDate?,
)
