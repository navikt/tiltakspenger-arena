package no.nav.tiltakspenger.arena.service.vedtakdetaljer

import no.nav.tiltakspenger.arena.repository.vedtakfakta.ArenaUtbetalingshistorikkVedtakfaktaDTO
import java.time.LocalDate

data class VedtakfaktaMeldekortDetaljer(
    val dagsats: Int?,
    val gjelderFra: LocalDate?,
    val gjelderTil: LocalDate?,
    val antallUtbetalinger: Int?,
    val belopPerUtbetalinger: Int?,
    val alternativBetalingsmottaker: String?,
)

fun ArenaUtbetalingshistorikkVedtakfaktaDTO.tilVedtakfaktaMeldekortDetaljer(): VedtakfaktaMeldekortDetaljer =
    VedtakfaktaMeldekortDetaljer(
        dagsats = this.dagsats,
        gjelderFra = this.gjelderFra,
        gjelderTil = this.gjelderTil,
        antallUtbetalinger = this.antallUtbetalinger,
        belopPerUtbetalinger = this.belopPerUtbetalinger,
        alternativBetalingsmottaker = this.alternativBetalingsmottaker,
    )
