package no.nav.tiltakspenger.arena.routes

import no.nav.tiltakspenger.arena.service.anmerkning.AnmerkningDetaljer
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.VedtakfaktaMeldekortDetaljer

data class UtbetalingshistorikkVedtaksfaktaOgAnmerkninger(
    val vedtakfakta: VedtakfaktaMeldekortDetaljer?,
    val anmerkninger: List<AnmerkningDetaljer>,
)
