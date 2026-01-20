package no.nav.tiltakspenger.arena.repository

import java.time.LocalDate

data class ArenaUtbetalingsgrunnlagDTO(
    val meldekortId: String,
    val modDato: LocalDate,
    val transaksjonstypenavn: String,
    val posteringsats: Double,
    val vedtakId: Int?,
    val belop: Double,
    val datoPeriodeFra: LocalDate,
    val datoPeriodeTil: LocalDate,
)
