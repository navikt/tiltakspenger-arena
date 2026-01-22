package no.nav.tiltakspenger.arena.repository.person

import kotliquery.Row
import kotliquery.TransactionalSession
import kotliquery.queryOf
import org.intellij.lang.annotations.Language

class PersonDAO {
    fun findByFnr(
        fnr: String,
        txSession: TransactionalSession,
    ): ArenaPersonDTO? {
        return txSession.run(
            queryOf(sqlFindPersonIdByFnr, mapOf("fnr" to fnr))
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
    private val sqlFindPersonIdByFnr = "SELECT PERSON_ID, FODSELSNR FROM PERSON WHERE FODSELSNR = :fnr"
}
