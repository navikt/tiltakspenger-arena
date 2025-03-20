package no.nav.tiltakspenger.arena.routes

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import no.nav.tiltakspenger.arena.auth.texas.TexasAuthEntraId
import no.nav.tiltakspenger.arena.auth.texas.client.TexasClient
import no.nav.tiltakspenger.arena.routes.ArenaTiltakspengerRettighetPeriodeMapper.toArenaTiltakspengerRettighetPeriode
import no.nav.tiltakspenger.arena.routes.ArenaTiltakspengerVedtakPeriodeMapper.toArenaTiltakspengerVedtakPeriode
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.RettighetDetaljer
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.RettighetDetaljerService
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.VedtakDetaljer
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.VedtakDetaljerService
import no.nav.tiltakspenger.libs.logging.sikkerlogg
import no.nav.tiltakspenger.libs.periodisering.Periodisering
import java.time.LocalDate

private val logger = KotlinLogging.logger {}

fun Route.tiltakspengerRoutes(
    texasClient: TexasClient,
    vedtakDetaljerService: VedtakDetaljerService,
    rettighetDetaljerService: RettighetDetaljerService,
) {
    route("/azure/tiltakspenger") {
        install(TexasAuthEntraId) { client = texasClient }
        post("/vedtaksperioder") {
            try {
                val req = call.receive<VedtakRequest>()
                logger.info { "Saksbehandler henter vedtaksperioder" }
                val periode: Periodisering<VedtakDetaljer>? =
                    vedtakDetaljerService.hentVedtakDetaljerPerioder(
                        ident = req.ident,
                        fom = req.fom ?: LocalDate.of(1900, 1, 1),
                        tom = req.tom ?: LocalDate.of(2999, 12, 31),
                    )
                logger.info { "Saksbehandler har hentet vedtaksperioder" }
                call.respond(periode.toArenaTiltakspengerVedtakPeriode())
            } catch (e: Exception) {
                sikkerlogg.warn(e) { "Feilet å hente tiltakspenger ${e.message}" }
                logger.warn { "Kunne ikke hente vedtaksperioder" }
                call.respondText(text = e.message ?: e.toString(), status = HttpStatusCode.InternalServerError)
            }
        }

        post("/rettighetsperioder") {
            try {
                val req = call.receive<VedtakRequest>()
                logger.info { "Saksbehandler henter rettighetsperioder" }
                val periode: Periodisering<RettighetDetaljer>? =
                    rettighetDetaljerService.hentRettighetDetaljerPerioder(
                        ident = req.ident,
                        fom = req.fom ?: LocalDate.of(1900, 1, 1),
                        tom = req.tom ?: LocalDate.of(2999, 12, 31),
                    )
                logger.info { "Saksbehandler har hentet rettighetsperioder" }
                call.respond(periode.toArenaTiltakspengerRettighetPeriode())
            } catch (e: Exception) {
                sikkerlogg.warn(e) { "Feilet å hente tiltakspenger ${e.message}" }
                logger.warn { "Kunne ikke hente rettighetsperioder" }
                call.respondText(text = e.message ?: e.toString(), status = HttpStatusCode.InternalServerError)
            }
        }
    }
}

data class VedtakRequest(
    val ident: String,
    val fom: LocalDate?,
    val tom: LocalDate?,
)
