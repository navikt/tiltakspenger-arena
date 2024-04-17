package no.nav.tiltakspenger.arena.service.vedtakdetaljer

import no.nav.tiltakspenger.arena.felles.PeriodeMedVerdier
import no.nav.tiltakspenger.arena.tilgang.Bruker
import java.time.LocalDate

class RettighetDetaljerServiceImpl(
    private val vedtakDetaljerService: VedtakDetaljerService,
) : RettighetDetaljerService {

    override fun hentRettighetDetaljerPerioder(
        ident: String,
        fom: LocalDate,
        tom: LocalDate,
        bruker: Bruker,
    ): PeriodeMedVerdier<RettighetDetaljer>? = vedtakDetaljerService.hentVedtakDetaljerPerioder(ident, fom, tom, bruker)
        ?.splitt { vedtakDetaljer -> RettighetDetaljer(vedtakDetaljer.rettighet) }
}
