package no.nav.tiltakspenger.arena.routes

import java.time.LocalDate

/**
 * Bruker samme format som i arena-api
 * https://github.com/navikt/arena-api/blob/f7a8491f0ee8bec9ac21899535a95820f4bac5b5/src/main/java/no/nav/arena/restapi/api/beregningsgrunnlag/v1/model/Anmerkning.java
 */
class ArenaAnmerkningDTO(
    val kilde: String?,
    val regDato: LocalDate?,
    val beskrivelse: String?,
)
