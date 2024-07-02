package no.nav.tiltakspenger.arena.service.vedtakdetaljer

import no.nav.tiltakspenger.libs.periodisering.Periodisering
import java.time.LocalDate

class RettighetDetaljerServiceImpl(
    private val vedtakDetaljerService: VedtakDetaljerService,
) : RettighetDetaljerService {

    override fun hentRettighetDetaljerPerioder(
        ident: String,
        fom: LocalDate,
        tom: LocalDate,
    ): Periodisering<RettighetDetaljer>? = vedtakDetaljerService.hentVedtakDetaljerPerioder(ident, fom, tom)
        ?.map { vedtakDetaljer -> RettighetDetaljer(vedtakDetaljer.rettighet) }
}
