package no.nav.tiltakspenger.arena.service.vedtakdetaljer

import no.nav.tiltakspenger.arena.felles.PeriodeMedVerdier
import no.nav.tiltakspenger.arena.tilgang.Bruker
import java.time.LocalDate

interface RettighetDetaljerService {
    fun hentRettighetDetaljerPerioder(
        ident: String,
        fom: LocalDate = LocalDate.of(1900, 1, 1),
        tom: LocalDate = LocalDate.of(2999, 12, 31),
        bruker: Bruker,
    ): PeriodeMedVerdier<RettighetDetaljer>?
}
