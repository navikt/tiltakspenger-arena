package no.nav.tiltakspenger.arena.repository

import java.time.LocalDate

data class ArenaUtbetalingshistorikkDTO(
    val meldekortId: String,
    val dato: LocalDate,
    val transaksjonstype: String,
    val sats: Double,
    val status: String,
    val vedtakId: Int?,
    val beløp: Double,
    val fraDato: LocalDate,
    val tilDato: LocalDate,
)
