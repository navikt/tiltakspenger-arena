package no.nav.tiltakspenger.arena.routes

import no.nav.tiltakspenger.arena.service.vedtakdetaljer.VedtakDetaljer
import no.nav.tiltakspenger.libs.periodisering.Periodisering
import java.time.LocalDate

object ArenaTiltakspengerVedtakPeriodeMapper {
    fun Periodisering<VedtakDetaljer>?.toArenaTiltakspengerVedtakPeriode(): List<ArenaTiltakspengerVedtakPeriode> =
        this?.map {
            ArenaTiltakspengerVedtakPeriode(
                fraOgMed = it.periode.fraOgMed,
                tilOgMed = it.periode.tilOgMed.toNullIfMax(),
                antallDager = it.verdi.antallDager,
                dagsatsTiltakspenger = it.verdi.dagsatsTiltakspenger,
                dagsatsBarnetillegg = it.verdi.dagsatsBarnetillegg,
                antallBarn = it.verdi.antallBarn,
                relaterteTiltak = it.verdi.tiltakGjennomføringsId,
                rettighet = it.verdi.rettighet,
                vedtakId = it.verdi.vedtakId,
                sakId = it.verdi.sakId,
                beslutningsdato = it.verdi.beslutningsdato,
            )
        }?.verdier ?: emptyList()

    private fun LocalDate.toNullIfMax(): LocalDate? = if (this == LocalDate.MAX) {
        null
    } else {
        this
    }
}
