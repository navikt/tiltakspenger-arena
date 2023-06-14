package no.nav.tiltakspenger.arena.repository

import kotliquery.sessionOf
import no.nav.tiltakspenger.arena.db.Datasource

class SakRepository(
    private val sakDAO: SakDAO = SakDAO(),
) {

    fun hent(id: String): String? {
        sessionOf(Datasource.hikariDataSource).use {
            it.transaction { txSession ->
                return sakDAO.hent(id, txSession)
            }
        }
    }
}
