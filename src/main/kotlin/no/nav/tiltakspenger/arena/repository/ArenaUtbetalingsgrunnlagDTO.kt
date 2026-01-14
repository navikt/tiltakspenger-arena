package no.nav.tiltakspenger.arena.repository

import java.time.LocalDate

class ArenaUtbetalingsgrunnlagDTO(
    meldekortId: String,
    datoPostert: LocalDate,
    transaksjonstypenavn: String,
    sats: Double,
    status: String = "Ikke overført utbetaling",
    vedtakId: Int?,
    beløp: Double,
    datoPeriodeFra: LocalDate,
    datoPeriodeTil: LocalDate,
) {
}
