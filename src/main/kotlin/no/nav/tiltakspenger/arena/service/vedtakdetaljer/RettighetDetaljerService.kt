package no.nav.tiltakspenger.arena.service.vedtakdetaljer

import no.nav.tiltakspenger.libs.periodisering.Periodisering
import java.time.LocalDate

interface RettighetDetaljerService {
    fun hentRettighetDetaljerPerioder(
        ident: String,
        fom: LocalDate = LocalDate.of(1900, 1, 1),
        tom: LocalDate = LocalDate.of(2999, 12, 31),
    ): Periodisering<RettighetDetaljer>?
}
