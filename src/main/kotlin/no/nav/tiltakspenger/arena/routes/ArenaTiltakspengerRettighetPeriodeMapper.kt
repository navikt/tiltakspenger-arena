package no.nav.tiltakspenger.arena.routes

import no.nav.tiltakspenger.arena.felles.PeriodeMedVerdier
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.Rettighet
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.RettighetDetaljer
import java.time.LocalDate

object ArenaTiltakspengerRettighetPeriodeMapper {
    fun PeriodeMedVerdier<RettighetDetaljer>?.toArenaTiltakspengerRettighetPeriode(): List<ArenaTiltakspengerRettighetPeriode> =
        this?.perioder()
            ?.filter {
                it.verdi.rettighet == Rettighet.TILTAKSPENGER ||
                    it.verdi.rettighet == Rettighet.TILTAKSPENGER_OG_BARNETILLEGG
            }
            ?.map {
                ArenaTiltakspengerRettighetPeriode(
                    fraOgMed = it.periode.fra,
                    tilOgMed = it.periode.til.toNullIfMax(),
                )
            } ?: emptyList()

    private fun LocalDate.toNullIfMax(): LocalDate? = if (this == LocalDate.MAX) {
        null
    } else {
        this
    }
}
