package no.nav.tiltakspenger.arena.tiltakogaktivitet

import java.time.LocalDate

data class ArenaOppfølgingsstatusDTO(
    var rettighetsgruppeKode: String?,
    var formidlingsgruppeKode: String?,
    var servicegruppeKode: String?,
    var navOppfoelgingsenhet: String?,
    var inaktiveringsdato: LocalDate?,
    var kanEnkeltReaktiveres: Boolean?,
)
