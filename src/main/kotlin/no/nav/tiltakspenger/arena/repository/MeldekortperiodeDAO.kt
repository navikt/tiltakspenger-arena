package no.nav.tiltakspenger.arena.repository

import kotliquery.Row
import kotliquery.TransactionalSession
import kotliquery.queryOf

class MeldekortperiodeDAO {
    fun findByAarOgPeriodekode(aar: Int, periodekode: Int, txSession: TransactionalSession): ArenaMeldekortperiodeDTO? {
        return txSession.run(
            action = queryOf(
                statement =
                    //language=SQL
                    """
                        SELECT  *
                        FROM MELDEKORTPERIODE mp
                        WHERE mp.AAR = :aar AND mp.PERIODEKODE = :periodekode
                    """.trimIndent(),
                paramMap = mapOf("aar" to aar, "periodekode" to periodekode),
            ).map { row -> row.toMeldekortperiode() }
                .asSingle,
        )
    }

    private fun Row.toMeldekortperiode(): ArenaMeldekortperiodeDTO {
        return ArenaMeldekortperiodeDTO(
            aar = int("AAR"),
            periodekode = int("PERIODEKODE"),
            ukenrUke1 = int("UKENR_UKE1"),
            ukenrUke2 = int("UKENR_UKE2"),
            datoFra = localDate("DATO_FRA"),
            datoTil = localDate("DATO_TIL"),
        )
    }
}
