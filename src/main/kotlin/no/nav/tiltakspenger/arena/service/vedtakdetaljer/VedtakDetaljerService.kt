package no.nav.tiltakspenger.arena.service.vedtakdetaljer

import no.nav.tiltakspenger.libs.periodisering.Periodisering
import java.time.LocalDate

interface VedtakDetaljerService {
    fun hentVedtakDetaljerPerioder(
        ident: String,
        fom: LocalDate,
        tom: LocalDate,
    ): Periodisering<VedtakDetaljer>?
}
