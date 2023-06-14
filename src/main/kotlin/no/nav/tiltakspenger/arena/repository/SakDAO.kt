package no.nav.tiltakspenger.arena.repository

import kotliquery.Row
import kotliquery.TransactionalSession
import kotliquery.queryOf
import mu.KotlinLogging
import org.intellij.lang.annotations.Language

class SakDAO {
    private val log = KotlinLogging.logger {}
    private val securelog = KotlinLogging.logger("tjenestekall")

    fun hent(
        id: String,
        txSession: TransactionalSession,
    ): String? {
        return txSession.run(
            queryOf(hentSql, id)
                .map { row -> row.toSak() }
                .asSingle,
        )
    }

    private fun Row.toSak(): String {
        return string("sak_id")
    }

    @Language("SQL")
    private val hentSql = "select * from sak where sak_id = ?"
}
