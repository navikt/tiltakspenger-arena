package no.nav.tiltakspenger.arena.routes

import no.nav.tiltakspenger.arena.service.vedtakdetaljer.Rettighet
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.RettighetDetaljer
import no.nav.tiltakspenger.libs.periodisering.Periodisering
import java.time.LocalDate

object ArenaTiltakspengerRettighetPeriodeMapper {
    fun Periodisering<RettighetDetaljer>?.toArenaTiltakspengerRettighetPeriode(): List<ArenaTiltakspengerRettighetPeriode> {
        return this?.filter {
            it.verdi.rettighet == Rettighet.TILTAKSPENGER ||
                it.verdi.rettighet == Rettighet.TILTAKSPENGER_OG_BARNETILLEGG
        }
            ?.map {
                ArenaTiltakspengerRettighetPeriode(
                    fraOgMed = it.periode.fraOgMed,
                    tilOgMed = it.periode.tilOgMed.toNullIfMax(),
                )
            }?.verdier ?: emptyList()
    }

    private fun LocalDate.toNullIfMax(): LocalDate? {
        return if (this == LocalDate.MAX) {
            null
        } else {
            this
        }
    }
}
