package no.nav.tiltakspenger.arena.repository

import kotliquery.Row
import kotliquery.TransactionalSession
import kotliquery.queryOf
import mu.KotlinLogging
import org.intellij.lang.annotations.Language

class PersonDAO {

    companion object {
        private val LOG = KotlinLogging.logger {}
        private val SECURELOG = KotlinLogging.logger("tjenestekall")
    }

    fun findByFnr(
        fnr: String,
        txSession: TransactionalSession,
    ): ArenaPersonDTO? {
        return txSession.run(
            queryOf(findByFnrSQL, mapOf("fnr" to fnr))
                .map { row -> row.toPerson() }
                .asSingle,
        )
    }

    private fun Row.toPerson(): ArenaPersonDTO {
        return ArenaPersonDTO(
            personId = long("PERSON_ID"),
            fnr = string("FODSELSNR"),
        )
    }

    @Language("SQL")
    private val findByFnrSQL = "SELECT PERSON_ID, FODSELSNR FROM PERSON WHERE FODSELSNR = :fnr"
}
