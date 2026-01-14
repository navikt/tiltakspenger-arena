package no.nav.tiltakspenger.arena.repository

import java.time.LocalDate

class ArenaPosteringDTO(
    meldekortId: String,
    datoPostert: LocalDate,
    transaksjonstypenavn: String,
    sats: Double,
    status: String = "Overført utbetaling",
    vedtakId: Int?,
    beløp: Double,
    datoPeriodeFra: LocalDate,
    datoPeriodeTil: LocalDate,
) {
}
