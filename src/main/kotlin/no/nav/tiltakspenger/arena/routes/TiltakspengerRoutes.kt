package no.nav.tiltakspenger.arena.routes

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import no.nav.tiltakspenger.arena.SE_SIKKERLOGG
import no.nav.tiltakspenger.arena.routes.ArenaTiltakspengerRettighetPeriodeMapper.toArenaTiltakspengerRettighetPeriode
import no.nav.tiltakspenger.arena.routes.ArenaTiltakspengerVedtakPeriodeMapper.toArenaTiltakspengerVedtakPeriode
import no.nav.tiltakspenger.arena.service.meldekort.MeldekortService
import no.nav.tiltakspenger.arena.service.utbetalingshistorikk.UtbetalingshistorikkService
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.Rettighet
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.RettighetDetaljer
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.RettighetDetaljerService
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.VedtakDetaljer
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.VedtakDetaljerService
import no.nav.tiltakspenger.libs.logging.Sikkerlogg
import no.nav.tiltakspenger.libs.periodisering.Periodisering
import no.nav.tiltakspenger.libs.texas.IdentityProvider
import java.time.LocalDate

private val logger = KotlinLogging.logger {}

fun Route.tiltakspengerRoutes(
    vedtakDetaljerService: VedtakDetaljerService,
    rettighetDetaljerService: RettighetDetaljerService,
    meldekortService: MeldekortService,
    utbetalingshistorikkService: UtbetalingshistorikkService,
) {
    authenticate(IdentityProvider.AZUREAD.value) {
        route("/azure/tiltakspenger") {
            post("/vedtaksperioder") {
                try {
                    val req = call.receive<VedtakRequest>()
                    val periode: Periodisering<VedtakDetaljer>? =
                        vedtakDetaljerService.hentVedtakDetaljerPerioder(
                            ident = req.ident,
                            fom = req.fom ?: LocalDate.of(1900, 1, 1),
                            tom = req.tom ?: LocalDate.of(2999, 12, 31),
                        )
                    val filtrertPeriode = periode?.filter {
                        it.verdi.rettighet == Rettighet.TILTAKSPENGER ||
                            it.verdi.rettighet == Rettighet.TILTAKSPENGER_OG_BARNETILLEGG ||
                            it.verdi.rettighet == Rettighet.BARNETILLEGG
                    }
                    val respons = filtrertPeriode.toArenaTiltakspengerVedtakPeriode()
                    logger.info { "Hentet vedtaksperioder (${respons.size} perioder). $SE_SIKKERLOGG" }
                    Sikkerlogg.info { "Hentet vedtaksperioder (${respons.size} perioder). Ident: ${req.ident}" }
                    call.respond(respons)
                } catch (e: Exception) {
                    logger.error(e) { "Kunne ikke hente vedtaksperioder. $SE_SIKKERLOGG" }
                    Sikkerlogg.error(e) { "Kunne ikke hente vedtaksperioder" }
                    call.respondText(text = e.message ?: e.toString(), status = HttpStatusCode.InternalServerError)
                }
            }

            post("/vedtak") {
                try {
                    val req = call.receive<VedtakRequest>()
                    val periode: Periodisering<VedtakDetaljer>? =
                        vedtakDetaljerService.hentVedtakDetaljerPerioder(
                            ident = req.ident,
                            fom = req.fom ?: LocalDate.of(1900, 1, 1),
                            tom = req.tom ?: LocalDate.of(2999, 12, 31),
                        )
                    val respons = periode.toArenaTiltakspengerVedtakPeriode()
                    logger.info { "Hentet vedtak fra arena (${respons.size} perioder). $SE_SIKKERLOGG" }
                    Sikkerlogg.info { "Hentet vedtak fra arena (${respons.size} perioder). Ident: ${req.ident}" }
                    call.respond(respons)
                } catch (e: Exception) {
                    logger.error(e) { "Kunne ikke hente vedtak fra arena. $SE_SIKKERLOGG" }
                    Sikkerlogg.error(e) { "Kunne ikke hente vedtak fra arena" }
                    call.respondText(text = e.message ?: e.toString(), status = HttpStatusCode.InternalServerError)
                }
            }

            post("/rettighetsperioder") {
                try {
                    val req = call.receive<VedtakRequest>()
                    val periode: Periodisering<RettighetDetaljer>? =
                        rettighetDetaljerService.hentRettighetDetaljerPerioder(
                            ident = req.ident,
                            fom = req.fom ?: LocalDate.of(1900, 1, 1),
                            tom = req.tom ?: LocalDate.of(2999, 12, 31),
                        )
                    val respons = periode.toArenaTiltakspengerRettighetPeriode()
                    logger.info { "Hentet rettighetsperioder (${respons.size} perioder). $SE_SIKKERLOGG" }
                    Sikkerlogg.info { "Hentet rettighetsperioder (${respons.size} perioder). Ident: ${req.ident}" }
                    call.respond(respons)
                } catch (e: Exception) {
                    logger.error(e) { "Kunne ikke hente rettighetsperioder. $SE_SIKKERLOGG" }
                    Sikkerlogg.error(e) { "Kunne ikke hente rettighetsperioder" }
                    call.respondText(text = e.message ?: e.toString(), status = HttpStatusCode.InternalServerError)
                }
            }

            post("/meldekort") {
                try {
                    val req = call.receive<VedtakRequest>()
                    val meldekort = meldekortService.hentMeldekortForFnr(
                        fnr = req.ident,
                        fraOgMedDato = req.fom ?: LocalDate.of(1900, 1, 1),
                        tilOgMedDato = req.tom ?: LocalDate.of(2999, 12, 31),
                    )
                    logger.info { "Hentet meldekort (${meldekort.size} meldekort). $SE_SIKKERLOGG" }
                    Sikkerlogg.info { "Hentet meldekort (${meldekort.size} meldekort). Ident: ${req.ident}" }
                    call.respond(meldekort)
                } catch (e: Exception) {
                    logger.error(e) { "Kunne ikke hente meldekort. $SE_SIKKERLOGG" }
                    Sikkerlogg.error(e) { "Kunne ikke hente meldekort" }
                    call.respondText(text = e.message ?: e.toString(), status = HttpStatusCode.InternalServerError)
                }
            }

            post("/utbetalingshistorikk") {
                try {
                    val req = call.receive<VedtakRequest>()
                    val utbetalingshistorikk = utbetalingshistorikkService.hentUtbetalingshistorikkForFnr(
                        fnr = req.ident,
                        fraOgMedDato = req.fom ?: LocalDate.of(1900, 1, 1),
                        tilOgMedDato = req.tom ?: LocalDate.of(2999, 12, 31),
                    )
                    logger.info { "Hentet utbetalingshistorikk (${utbetalingshistorikk.size} innslag). $SE_SIKKERLOGG" }
                    Sikkerlogg.info { "Hentet utbetalingshistorikk (${utbetalingshistorikk.size} innslag). Ident: ${req.ident}" }
                    call.respond(utbetalingshistorikk)
                } catch (e: Exception) {
                    logger.error(e) { "Kunne ikke hente utbetalingshistorikk. $SE_SIKKERLOGG" }
                    Sikkerlogg.error(e) { "Kunne ikke hente utbetalingshistorikk" }
                    call.respondText(text = e.message ?: e.toString(), status = HttpStatusCode.InternalServerError)
                }
            }

            get("/utbetalingshistorikk/detaljer") {
                try {
                    val vedtakId = call.request.queryParameters["vedtakId"]?.toLongOrNull()
                    val meldekortId = call.request.queryParameters["meldekortId"]?.toLongOrNull()

                    val anmerkninger = utbetalingshistorikkService.hentAnmerkningerForMeldekort(meldekortId)
                    val vedtakfakta = utbetalingshistorikkService.hentVedtakfaktaForVedtak(vedtakId)
                    logger.info {
                        "Hentet detaljer om utbetalingshistorikk for vedtakId $vedtakId, meldekortId $meldekortId " +
                            "(${anmerkninger.size} anmerkninger, vedtakfakta: ${vedtakfakta != null})"
                    }

                    call.respond(
                        UtbetalingshistorikkVedtaksfaktaOgAnmerkninger(
                            anmerkninger = anmerkninger,
                            vedtakfakta = vedtakfakta,
                        ),
                    )
                } catch (e: Exception) {
                    logger.error(e) { "Kunne ikke hente detaljer om utbetalingshistorikk. $SE_SIKKERLOGG" }
                    Sikkerlogg.error(e) { "Kunne ikke hente detaljer om utbetalingshistorikk" }
                    call.respondText(text = e.message ?: e.toString(), status = HttpStatusCode.InternalServerError)
                }
            }
        }
    }
}

data class VedtakRequest(
    val ident: String,
    val fom: LocalDate?,
    val tom: LocalDate?,
) {
    /**
     * [ident] er PII og skal ikke bli med om noen logger hele objektet.
     * Samme maskering som [no.nav.tiltakspenger.libs.common.Fnr].
     */
    override fun toString() = "VedtakRequest(ident=***********, fom=$fom, tom=$tom)"
}

data class AnmerkningOgVedtakRequest(
    val vedtakId: Long,
    val meldekortId: Long,
)
