package no.nav.tiltakspenger.arena.service.meldekort

import no.nav.tiltakspenger.arena.repository.MeldekortRepository
import java.time.LocalDate

class MeldekortService(
    private val meldekortRepository: MeldekortRepository = MeldekortRepository(),
) {
    fun hentMeldekortForFnr(
        fnr: String,
        fraOgMedDato: LocalDate,
        tilOgMedDato: LocalDate,
    ): List<MeldekortDetaljer> {
        return meldekortRepository.hentMeldekortForFnr(
            fnr = fnr,
            fraOgMedDato = fraOgMedDato,
            tilOgMedDato = tilOgMedDato,
        ).map { it.tilMeldekortDetaljer() }
    }
}

fun String.tilBooleanArena(): Boolean {
    return this.uppercase() == "J"
}
