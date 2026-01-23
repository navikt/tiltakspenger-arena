package no.nav.tiltakspenger.arena.repository.meldekort

import kotliquery.Row
import kotliquery.TransactionalSession
import kotliquery.queryOf

class MeldekortdagDAO {
    fun findByMeldekortId(
        meldekortId: String,
        txSession: TransactionalSession,
    ): List<ArenaMeldekortDagDTO> {
        //language=SQL
        return txSession.run(
            action = queryOf(
                statement =
                """
                        SELECT 
                            UKENR,
                            DAGNR,
                            STATUS_ARBEIDSDAG,
                            STATUS_FERIE,
                            STATUS_KURS,
                            STATUS_SYK,
                            STATUS_ANNETFRAVAER,
                            REG_USER,
                            REG_DATO,
                            TIMER_ARBEIDET
                        FROM MELDEKORTDAG
                        WHERE MELDEKORT_ID = :meldekortId
                """.trimIndent(),
                paramMap = mapOf("meldekortId" to meldekortId),
            ).map { row -> row.toMeldekortdag() }
                .asList,
        )
    }

    private fun Row.toMeldekortdag(): ArenaMeldekortDagDTO {
        return ArenaMeldekortDagDTO(
            ukeNr = int("UKENR"),
            dagNr = int("DAGNR"),
            statusArbeidsdag = string("STATUS_ARBEIDSDAG"),
            statusFerie = stringOrNull("STATUS_FERIE"),
            statusKurs = string("STATUS_KURS"),
            statusSyk = string("STATUS_SYK"),
            statusAnnetfravaer = string("STATUS_ANNETFRAVAER"),
            regUser = string("REG_USER"),
            regDato = localDateTime("REG_DATO"),
            arbeidetTimer = int("TIMER_ARBEIDET"),
        )
    }
}
