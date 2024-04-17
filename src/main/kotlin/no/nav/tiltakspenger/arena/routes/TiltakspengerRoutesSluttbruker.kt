package no.nav.tiltakspenger.arena.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import mu.KotlinLogging
import no.nav.tiltakspenger.arena.felles.PeriodeMedVerdier
import no.nav.tiltakspenger.arena.fødselsnummer
import no.nav.tiltakspenger.arena.routes.ArenaTiltakspengerRettighetPeriodeMapper.toArenaTiltakspengerRettighetPeriode
import no.nav.tiltakspenger.arena.routes.ArenaTiltakspengerVedtakPeriodeMapper.toArenaTiltakspengerVedtakPeriode
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.RettighetDetaljer
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.RettighetDetaljerService
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.VedtakDetaljer
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.VedtakDetaljerService

private val SECURELOG = KotlinLogging.logger("tjenestekall")

fun Route.tiltakspengerRoutesSluttbruker(
    vedtakDetaljerService: VedtakDetaljerService,
    rettighetDetaljerService: RettighetDetaljerService,
) {
    post("/sluttbruker/tiltakspenger/vedtaksperioder") {
        try {
            val ident = call.fødselsnummer() ?: throw IllegalStateException("Mangler fødselsnummer")
            SECURELOG.info { "$ident spør om rettighetsperioder for seg selv" }

            val periode: PeriodeMedVerdier<VedtakDetaljer>? =
                vedtakDetaljerService.hentVedtakDetaljerPerioder(ident = ident, bruker = bruker)
            call.respond(periode.toArenaTiltakspengerVedtakPeriode())
        } catch (e: IllegalStateException) {
            SECURELOG.warn { "Mangler fødselsnummer" }
            call.respondText(text = "Bad Request", status = HttpStatusCode.BadRequest)
        } catch (e: Exception) {
            SECURELOG.warn("Feilet å hente tiltakspenger ${e.message}", e)
            call.respondText(text = e.message ?: e.toString(), status = HttpStatusCode.InternalServerError)
        }
    }

    post("/sluttbruker/tiltakspenger/rettighetsperioder") {
        try {
            val ident = call.fødselsnummer() ?: throw IllegalStateException("Mangler fødselsnummer")
            SECURELOG.info { "$ident spør om rettighetsperioder for seg selv" }

            val periode: PeriodeMedVerdier<RettighetDetaljer>? =
                rettighetDetaljerService.hentRettighetDetaljerPerioder(ident = ident, bruker = bruker)
            call.respond(periode.toArenaTiltakspengerRettighetPeriode())
        } catch (e: IllegalStateException) {
            SECURELOG.warn { "Mangler fødselsnummer" }
            call.respondText(text = "Bad Request", status = HttpStatusCode.BadRequest)
        } catch (e: Exception) {
            SECURELOG.warn("Feilet å hente tiltakspenger ${e.message}", e)
            call.respondText(text = e.message ?: e.toString(), status = HttpStatusCode.InternalServerError)
        }
    }
}
