package no.nav.tiltakspenger.arena.repository

import java.time.LocalDate

class ArenaUtbetalingshistorikkDTO(
    meldekortId: String,
    datoPostert: LocalDate,
    transaksjonstypenavn: String,
    sats: Double,
    status: String,
    vedtakId: Int?,
    beløp: Double,
    datoPeriodeFra: LocalDate,
    datoPeriodeTil: LocalDate,
)
