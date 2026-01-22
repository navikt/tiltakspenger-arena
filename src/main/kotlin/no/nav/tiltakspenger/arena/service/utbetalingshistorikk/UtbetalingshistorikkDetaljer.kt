package no.nav.tiltakspenger.arena.service.utbetalingshistorikk

import no.nav.tiltakspenger.arena.repository.ArenaAnmerkningMedPeriodeDTO
import no.nav.tiltakspenger.arena.repository.ArenaBeregnetMeldekortMedFeilDTO
import no.nav.tiltakspenger.arena.repository.ArenaBeregningsloggDTO
import no.nav.tiltakspenger.arena.repository.ArenaPosteringDTO
import no.nav.tiltakspenger.arena.repository.ArenaUtbetalingsgrunnlagDTO
import java.time.LocalDate

data class UtbetalingshistorikkDetaljer(
    val meldekortId: Long?,
    val dato: LocalDate,
    val transaksjonstype: String,
    val sats: Double,
    val status: String,
    val vedtakId: Long?,
    val belop: Double,
    val fraOgMedDato: LocalDate,
    val tilOgMedDato: LocalDate,
)

fun ArenaPosteringDTO.tilUtbetalingshistorikk() = UtbetalingshistorikkDetaljer(
    meldekortId = this.meldekortId,
    dato = this.datoPostert,
    transaksjonstype = this.transaksjonstypenavn,
    sats = this.posteringsats,
    status = "Overført utbetaling",
    vedtakId = this.vedtakId,
    belop = this.belop,
    fraOgMedDato = this.datoPeriodeFra,
    tilOgMedDato = this.datoPeriodeTil,
)

fun ArenaUtbetalingsgrunnlagDTO.tilUtbetalingshistorikk() = UtbetalingshistorikkDetaljer(
    meldekortId = this.meldekortId,
    dato = this.modDato,
    transaksjonstype = this.transaksjonstypenavn,
    sats = this.posteringsats,
    status = "Ikke overført utbetaling",
    vedtakId = this.vedtakId,
    belop = this.belop,
    fraOgMedDato = this.datoPeriodeFra,
    tilOgMedDato = this.datoPeriodeTil,
)

fun ArenaBeregningsloggDTO.tilUtbetalingshistorikk() = UtbetalingshistorikkDetaljer(
    meldekortId = this.meldekortId,
    dato = this.regDato,
    transaksjonstype = this.rettighetsnavn,
    sats = 0.0,
    status = this.beregningstatusnavn,
    vedtakId = this.vedtakId,
    belop = 0.0,
    fraOgMedDato = this.datoFra,
    tilOgMedDato = this.datoTil,
)

fun ArenaAnmerkningMedPeriodeDTO.tilUtbetalingshistorikk() = UtbetalingshistorikkDetaljer(
    meldekortId = this.meldekortId,
    dato = this.regDato,
    transaksjonstype = this.rettighetnavn,
    sats = 0.0,
    status = this.beregningstatusnavn,
    vedtakId = this.vedtakId,
    belop = 0.0,
    fraOgMedDato = this.datoFra,
    tilOgMedDato = this.datoTil,
)

fun ArenaBeregnetMeldekortMedFeilDTO.tilUtbetalingshistorikk(): UtbetalingshistorikkDetaljer {
    return UtbetalingshistorikkDetaljer(
        meldekortId = this.meldekortId,
        dato = this.modDato,
        transaksjonstype = meldekortkodenavn,
        sats = 0.0,
        status = this.beregningstatusnavn,
        vedtakId = null,
        belop = 0.0,
        fraOgMedDato = this.datoFra,
        tilOgMedDato = this.datoTil,
    )
}
