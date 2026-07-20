package no.nav.tiltakspenger.arena.routes

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
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
                medFeilhåndtering("hente vedtaksperioder") {
                    val req = call.receive<VedtakRequest>()
                    val periode: Periodisering<VedtakDetaljer>? =
                        vedtakDetaljerService.hentVedtakDetaljerPerioder(
                            ident = req.ident,
                            fom = req.fomEllerMin,
                            tom = req.tomEllerMaks,
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
                }
            }

            post("/vedtak") {
                medFeilhåndtering("hente vedtak fra arena") {
                    val req = call.receive<VedtakRequest>()
                    val periode: Periodisering<VedtakDetaljer>? =
                        vedtakDetaljerService.hentVedtakDetaljerPerioder(
                            ident = req.ident,
                            fom = req.fomEllerMin,
                            tom = req.tomEllerMaks,
                        )
                    val respons = periode.toArenaTiltakspengerVedtakPeriode()
                    logger.info { "Hentet vedtak fra arena (${respons.size} perioder). $SE_SIKKERLOGG" }
                    Sikkerlogg.info { "Hentet vedtak fra arena (${respons.size} perioder). Ident: ${req.ident}" }
                    call.respond(respons)
                }
            }

            post("/rettighetsperioder") {
                medFeilhåndtering("hente rettighetsperioder") {
                    val req = call.receive<VedtakRequest>()
                    val periode: Periodisering<RettighetDetaljer>? =
                        rettighetDetaljerService.hentRettighetDetaljerPerioder(
                            ident = req.ident,
                            fom = req.fomEllerMin,
                            tom = req.tomEllerMaks,
                        )
                    val respons = periode.toArenaTiltakspengerRettighetPeriode()
                    logger.info { "Hentet rettighetsperioder (${respons.size} perioder). $SE_SIKKERLOGG" }
                    Sikkerlogg.info { "Hentet rettighetsperioder (${respons.size} perioder). Ident: ${req.ident}" }
                    call.respond(respons)
                }
            }

            post("/meldekort") {
                medFeilhåndtering("hente meldekort") {
                    val req = call.receive<VedtakRequest>()
                    val meldekort = meldekortService.hentMeldekortForFnr(
                        fnr = req.ident,
                        fraOgMedDato = req.fomEllerMin,
                        tilOgMedDato = req.tomEllerMaks,
                    )
                    logger.info { "Hentet meldekort (${meldekort.size} meldekort). $SE_SIKKERLOGG" }
                    Sikkerlogg.info { "Hentet meldekort (${meldekort.size} meldekort). Ident: ${req.ident}" }
                    call.respond(meldekort)
                }
            }

            post("/utbetalingshistorikk") {
                medFeilhåndtering("hente utbetalingshistorikk") {
                    val req = call.receive<VedtakRequest>()
                    val utbetalingshistorikk = utbetalingshistorikkService.hentUtbetalingshistorikkForFnr(
                        fnr = req.ident,
                        fraOgMedDato = req.fomEllerMin,
                        tilOgMedDato = req.tomEllerMaks,
                    )
                    logger.info { "Hentet utbetalingshistorikk (${utbetalingshistorikk.size} innslag). $SE_SIKKERLOGG" }
                    Sikkerlogg.info { "Hentet utbetalingshistorikk (${utbetalingshistorikk.size} innslag). Ident: ${req.ident}" }
                    call.respond(utbetalingshistorikk)
                }
            }

            get("/utbetalingshistorikk/detaljer") {
                medFeilhåndtering("hente detaljer om utbetalingshistorikk") {
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
                }
            }
        }
    }
}

/**
 * Felles feilhåndtering for handlerne: uventede feil (typisk mot Arena-databasen) logges ett sted og gir 500 med feilmeldingen som tekst.
 * [beskrivelse] skiller endepunktene i loggen ("hente meldekort" osv.).
 */
private suspend fun RoutingContext.medFeilhåndtering(
    beskrivelse: String,
    block: suspend RoutingContext.() -> Unit,
) {
    // TODO jah: Bytt til Either.catch {...}
    try {
        block()
    } catch (e: Exception) {
        logger.error(e) { "Kunne ikke $beskrivelse. $SE_SIKKERLOGG" }
        Sikkerlogg.error(e) { "Kunne ikke $beskrivelse" }
        call.respondText(text = e.message ?: e.toString(), status = HttpStatusCode.InternalServerError)
    }
}

data class VedtakRequest(
    val ident: String,
    val fom: LocalDate?,
    val tom: LocalDate?,
) {
    /** Utelatt fom/tom tolkes som ubegrenset periode. */
    val fomEllerMin: LocalDate get() = fom ?: LocalDate.of(1900, 1, 1)
    val tomEllerMaks: LocalDate get() = tom ?: LocalDate.of(2999, 12, 31)

    /**
     * [ident] er PII og skal ikke bli med om noen logger hele objektet.
     * Samme maskering som [no.nav.tiltakspenger.libs.common.Fnr].
     */
    override fun toString() = "VedtakRequest(ident=***********, fom=$fom, tom=$tom)"
}
