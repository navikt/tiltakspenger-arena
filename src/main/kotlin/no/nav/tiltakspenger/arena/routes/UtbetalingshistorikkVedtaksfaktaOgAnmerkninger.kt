package no.nav.tiltakspenger.arena.routes

import no.nav.tiltakspenger.arena.repository.ArenaUtbetalingshistorikkVedtakfaktaDTO
import no.nav.tiltakspenger.arena.service.anmerkning.AnmerkningDetaljer

data class UtbetalingshistorikkVedtaksfaktaOgAnmerkninger(
    val vedtakfakta: ArenaUtbetalingshistorikkVedtakfaktaDTO,
    val anmerkninger: List<AnmerkningDetaljer>,
)
