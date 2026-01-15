package no.nav.tiltakspenger.arena.repository

import java.time.LocalDate

class ArenaUtbetalingshistorikkDTO(
    meldekortId: String,
    dato: LocalDate,
    transaksjonstype: String,
    sats: Double,
    status: String,
    vedtakId: Int?,
    beløp: Double,
    fraDato: LocalDate,
    tilDato: LocalDate,
)
