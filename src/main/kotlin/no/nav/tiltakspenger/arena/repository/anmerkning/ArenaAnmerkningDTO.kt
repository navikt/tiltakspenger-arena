package no.nav.tiltakspenger.arena.repository.anmerkning

import java.time.LocalDateTime

/**
 * Bruker samme format som i arena-api
 * https://github.com/navikt/arena-api/blob/f7a8491f0ee8bec9ac21899535a95820f4bac5b5/src/main/java/no/nav/arena/restapi/api/beregningsgrunnlag/v1/model/Anmerkning.java
 */
class ArenaAnmerkningDTO(
    val kilde: String?,
    val regDato: LocalDateTime?,
    val beskrivelse: String?,
)
