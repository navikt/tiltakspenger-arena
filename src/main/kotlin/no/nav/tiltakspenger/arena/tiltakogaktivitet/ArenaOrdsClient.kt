package no.nav.tiltakspenger.arena.tiltakogaktivitet

interface ArenaOrdsClient {
    suspend fun hentArenaAktiviteter(fnr: String): ArenaAktiviteterDTO
}
