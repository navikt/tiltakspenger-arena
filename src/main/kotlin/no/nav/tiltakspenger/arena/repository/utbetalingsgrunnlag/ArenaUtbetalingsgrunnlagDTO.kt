package no.nav.tiltakspenger.arena.repository.utbetalingsgrunnlag

import java.time.LocalDate

data class ArenaUtbetalingsgrunnlagDTO(
    val meldekortId: Long?,
    val modDato: LocalDate,
    val transaksjonstypenavn: String,
    val posteringsats: Double,
    val vedtakId: Long?,
    val belop: Double,
    val datoPeriodeFra: LocalDate,
    val datoPeriodeTil: LocalDate,
)
