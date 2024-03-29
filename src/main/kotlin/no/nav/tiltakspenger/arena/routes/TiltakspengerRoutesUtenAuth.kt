package no.nav.tiltakspenger.arena.routes

import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import mu.KotlinLogging
import no.nav.tiltakspenger.arena.felles.Periode
import no.nav.tiltakspenger.arena.felles.PeriodeMedVerdier
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.Rettighet
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.VedtakDetaljer
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.VedtakDetaljerServiceImpl
import java.time.LocalDate

private val SECURELOG = KotlinLogging.logger("tjenestekall")

fun Route.tiltakspengerRoutesUtenAuth(service: VedtakDetaljerServiceImpl) {
    post("/tiltakspengerUten") {
        try {
            val ident =
                call.receive<RequestBody>().ident
            val periode: PeriodeMedVerdier<VedtakDetaljer>? =
                service.hentVedtakDetaljerPerioder(ident = ident)
            call.respond(periode.toPeriodeDTO())
        } catch (e: Exception) {
            SECURELOG.warn("Feil i kall mot Arena for å hente tiltakspenger ${e.message}", e)
            call.respond(emptyList<Periode>())
        }
    }
}

private fun PeriodeMedVerdier<VedtakDetaljer>?.toPeriodeDTO(): List<ArenaTiltakspengerPeriode> =
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
