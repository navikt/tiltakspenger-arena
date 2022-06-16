package no.nav.tiltakspenger.arena.tiltakogaktivitet

interface ArenaOrdsClient {
    suspend fun hentArenaOppfolgingsstatus(fnr: String): ArenaOppfølgingsstatusDTO?
    suspend fun hentArenaOppfolgingssak(fnr: String): ArenaOppfølgingssakDTO?
    suspend fun hentArenaAktiviteter(fnr: String): ArenaAktiviteterDTO?
}
