package no.nav.tiltakspenger.arena.routes

import no.nav.tiltakspenger.arena.repository.ArenaAnmerkningDTO
import no.nav.tiltakspenger.arena.repository.ArenaUtbetalingshistorikkVedtakfaktaDTO

data class UtbetalingshistorikkVedtaksfaktaOgAnmerkninger(
    val vedtakfakta: ArenaUtbetalingshistorikkVedtakfaktaDTO,
    val anmerkninger: List<ArenaAnmerkningDTO>,
)
