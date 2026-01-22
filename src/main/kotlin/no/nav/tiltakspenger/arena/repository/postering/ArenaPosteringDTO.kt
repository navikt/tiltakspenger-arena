package no.nav.tiltakspenger.arena.repository.postering

import java.time.LocalDate

data class ArenaPosteringDTO(
    val meldekortId: Long?,
    val datoPostert: LocalDate,
    val transaksjonstypenavn: String,
    val posteringsats: Double,
    val vedtakId: Long?,
    val belop: Double,
    val datoPeriodeFra: LocalDate,
    val datoPeriodeTil: LocalDate,
)
