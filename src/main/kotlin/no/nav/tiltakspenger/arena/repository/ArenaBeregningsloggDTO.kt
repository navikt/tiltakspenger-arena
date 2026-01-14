package no.nav.tiltakspenger.arena.repository

import java.time.LocalDate

class ArenaBeregningsloggDTO(
    meldekortId: String,
    datoPostert: LocalDate,
    transaksjonstypenavn: String,
    sats: Double = 0.0,
    status: String,
    vedtakId: Int?,
    beløp: Double = 0.0,
    datoPeriodeFra: LocalDate,
    datoPeriodeTil: LocalDate,
)
