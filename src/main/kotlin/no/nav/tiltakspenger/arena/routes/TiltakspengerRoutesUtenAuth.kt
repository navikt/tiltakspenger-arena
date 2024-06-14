package no.nav.tiltakspenger.arena.routes

import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import mu.KotlinLogging
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.Rettighet
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.VedtakDetaljer
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.VedtakDetaljerServiceImpl
import no.nav.tiltakspenger.libs.periodisering.Periode
import no.nav.tiltakspenger.libs.periodisering.Periodisering
import java.time.LocalDate

private val SECURELOG = KotlinLogging.logger("tjenestekall")

fun Route.tiltakspengerRoutesUtenAuth(service: VedtakDetaljerServiceImpl) {
    post("/tiltakspengerUten") {
        try {
            val req = call.receive<VedtakRequest>()
            val periode: Periodisering<VedtakDetaljer>? =
                service.hentVedtakDetaljerPerioder(
                    ident = req.ident,
                    fom = req.fom ?: LocalDate.of(1900, 1, 1),
                    tom = req.tom ?: LocalDate.of(2999, 12, 31),
                )
            call.respond(periode.toPeriodeDTO())
        } catch (e: Exception) {
            SECURELOG.warn("Feil i kall mot Arena for å hente tiltakspenger ${e.message}", e)
            call.respond(emptyList<Periode>())
        }
    }
}

private fun Periodisering<VedtakDetaljer>?.toPeriodeDTO(): List<ArenaTiltakspengerPeriode> =
    this?.perioder()
        ?.filter { it.verdi.rettighet == Rettighet.TILTAKSPENGER || it.verdi.rettighet == Rettighet.TILTAKSPENGER_OG_BARNETILLEGG }
        ?.map {
            ArenaTiltakspengerPeriode(
                fraOgMed = it.periode.fra,
                tilOgMed = it.periode.til.toNullIfMax(),
                antallDager = it.verdi.antallDager,
                dagsatsTiltakspenger = it.verdi.dagsatsTiltakspenger,
                dagsatsBarnetillegg = it.verdi.dagsatsBarnetillegg,
                antallBarn = it.verdi.antallBarn,
                relaterteTiltak = it.verdi.relaterteTiltak,
                rettighet = it.verdi.rettighet,
            )
        } ?: emptyList()

private fun LocalDate.toNullIfMax(): LocalDate? = if (this == LocalDate.MAX) {
    null
} else {
    this
}

private data class VedtakRequest(
    val ident: String,
    val fom: LocalDate?,
    val tom: LocalDate?,
)

private data class ArenaTiltakspengerPeriode(
    val fraOgMed: LocalDate,
    val tilOgMed: LocalDate?,
    val antallDager: Double,
    val dagsatsTiltakspenger: Int,
    val dagsatsBarnetillegg: Int,
    val antallBarn: Int,
    val relaterteTiltak: String,
    val rettighet: Rettighet,
)
