package no.nav.tiltakspenger.arena.tiltakogaktivitet

import no.nav.common.types.identer.Fnr

interface ArenaOrdsClient {
    fun hentArenaOppfolgingsstatus(fnr: Fnr): ArenaOppfølgingsstatusDTO?
    fun hentArenaOppfolginssak(fnr: Fnr): ArenaOppfølgingssakDTO?
    fun hentArenaAktiviteter(fnr: Fnr): ArenaAktiviteterDTO?
}
