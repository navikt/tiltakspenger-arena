package no.nav.tiltakspenger.arena.repository

import java.time.LocalDate

class ArenaUtbetalingsgrunnlagDTO(
    meldekortId: String,
    datoPostert: LocalDate,
    transaksjonstypenavn: String,
    sats: Double,
    status: String,
    vedtakId: Int?,
    beløp: Double,
    datoPeriodeFra: LocalDate,
    datoPeriodeTil: LocalDate,
) {
}
