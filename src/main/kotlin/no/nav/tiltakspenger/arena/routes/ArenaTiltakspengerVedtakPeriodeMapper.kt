package no.nav.tiltakspenger.arena.routes

import no.nav.tiltakspenger.arena.felles.PeriodeMedVerdier
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.Rettighet
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.VedtakDetaljer
import java.time.LocalDate

object ArenaTiltakspengerVedtakPeriodeMapper {
    fun PeriodeMedVerdier<VedtakDetaljer>?.toArenaTiltakspengerVedtakPeriode(): List<ArenaTiltakspengerVedtakPeriode> =
        this?.perioder()
            ?.filter {
                it.verdi.rettighet == Rettighet.TILTAKSPENGER ||
                    it.verdi.rettighet == Rettighet.TILTAKSPENGER_OG_BARNETILLEGG ||
                    it.verdi.rettighet == Rettighet.BARNETILLEGG
            }
            ?.map {
                ArenaTiltakspengerVedtakPeriode(
                    fraOgMed = it.periode.fra,
                    tilOgMed = it.periode.til.toNullIfMax(),
                    antallDager = it.verdi.antallDager,
                    dagsatsTiltakspenger = it.verdi.dagsatsTiltakspenger,
                    dagsatsBarnetillegg = it.verdi.dagsatsBarnetillegg,
                    antallBarn = it.verdi.antallBarn,
                    relaterteTiltak = it.verdi.relaterteTiltak,
                    rettighet = it.verdi.rettighet,
                    vedtakId = it.verdi.vedtakId,
                    sakId = it.verdi.sakId,
                )
            } ?: emptyList()

    private fun LocalDate.toNullIfMax(): LocalDate? = if (this == LocalDate.MAX) {
        null
    } else {
        this
    }
}
