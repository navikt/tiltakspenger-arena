package no.nav.tiltakspenger.arena.service.vedtakdetaljer

import no.nav.tiltakspenger.libs.periodisering.Periodisering
import java.time.LocalDate

interface RettighetDetaljerService {
    fun hentRettighetDetaljerPerioder(
        ident: String,
        fom: LocalDate,
        tom: LocalDate,
    ): Periodisering<RettighetDetaljer>?
}
