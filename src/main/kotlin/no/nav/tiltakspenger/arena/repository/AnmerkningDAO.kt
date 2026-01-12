package no.nav.tiltakspenger.arena.repository

import io.github.oshai.kotlinlogging.KotlinLogging
import kotliquery.Row
import kotliquery.TransactionalSession
import kotliquery.queryOf
import no.nav.tiltakspenger.arena.routes.ArenaAnmerkningDTO
import java.time.LocalDate

class AnmerkningDAO(
    private val personDao: PersonDAO,
) {
    private val logger = KotlinLogging.logger {}


    fun findByVedtakAndMeldekort(
        vedtakId: Int?,
        meldekortId: Int,
        txSession: TransactionalSession,
    ): List<ArenaAnmerkningDTO> {
        return txSession.run(
            action = queryOf(
                statement =
                    //language=SQL
                    """
                        SELECT 
                            a.VEDTAK_ID     AS VEDTAK_ID,
                            a.REG_DATO      AS REG_DATO,
                            at.BESKRIVELSE  AS BESKRIVELSE
                        FROM ANMERKNING a
                        INNER JOIN ANMERKNINGTYPE at ON a.ANMERKNINGTYPE_ID = at.ANMERKNINGTYPE_ID                  
                        WHERE a.TABELLNAVNALIAS = 'MKORT' 
                        AND a.OBJEKT_ID = :meldekortId
                        AND (a.VEDTAK_ID = :vedtakId OR a.VEDTAK_ID IS NULL)
                    """.trimIndent(),
                paramMap = mapOf(
                    "vedtakId" to vedtakId,
                    "meldekortId" to meldekortId,
                ),
            ).map { row -> row.toAnmerkning(txSession) }
                .asList,
        )
    }

    private fun Row.toAnmerkning(txSession: TransactionalSession): ArenaAnmerkningDTO {
        // Bruker samme logikk som i PLSQL som benyttes i arena-api for å bestemme kilde. Queryen henter bare anmerkninger relatert til meldekort og deres vedtak.
        val kilde = if (stringOrNull("VEDTAK_ID") == null) "Meldekort" else "Vedtak"

        return ArenaAnmerkningDTO(
            kilde = kilde,
            regDato = localDateOrNull("REG_DATO"),
            beskrivelse = stringOrNull("BESKRIVELSE"),
        )
    }
}
