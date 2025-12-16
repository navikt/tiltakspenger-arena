package no.nav.tiltakspenger.arena.repository

import io.github.oshai.kotlinlogging.KotlinLogging
import kotliquery.Row
import kotliquery.TransactionalSession
import kotliquery.queryOf
import no.nav.tiltakspenger.libs.logging.Sikkerlogg

class MeldekortDAO(
    private val personDao: PersonDAO,
    private val meldekortdagDAO: MeldekortdagDAO,
    private val meldekortperiodeDAO: MeldekortperiodeDAO,
) {
    private val logger = KotlinLogging.logger {}


    fun findByFnr(
        fnr: String,
        txSession: TransactionalSession,
    ): List<ArenaMeldekortDTO> {
        val person = personDao.findByFnr(fnr, txSession)
        if (person == null) {
            logger.info { "Fant ikke person" }
            Sikkerlogg.info { "Fant ikke person med ident $fnr" }
            return emptyList()
        }

        return txSession.run(
            action = queryOf(
                statement =
                    //language=SQL
                    """
                        SELECT 
                            PERSON_ID,
                            MELDEKORT_ID,
                            DATO_INNKOMMET,
                            STATUS_ARBEIDET,
                            STATUS_KURS,
                            STATUS_FERIE,
                            STATUS_SYK,
                            STATUS_ANNETFRAVAER,
                            REG_DATO,
                            MOD_DATO,
                            MKSKORTKODE,
                            BEREGNINGSTATUSKODE,
                            AAR,
                            PERIODEKODE
                        FROM MELDEKORT
                        WHERE PERSON_ID = :personId
                    """.trimIndent(),
                paramMap = mapOf("personId" to person.personId),
            ).map { row -> row.toMeldekort(txSession) }
                .asList,
        )
    }

    private fun Row.toMeldekort(txSession: TransactionalSession): ArenaMeldekortDTO {
        val meldekortId = string("MELDEKORT_ID")
        val aar = int("AAR")
        val periodekode = int("PERIODEKODE")
        val meldekortperiode = meldekortperiodeDAO.findByAarOgPeriodekode(aar, periodekode, txSession)
            ?: throw IllegalStateException("Fant ikke meldekortperiode for aar $aar og periodekode $periodekode")
        val dager = meldekortdagDAO.findByMeldekortId(meldekortId, txSession)

        return ArenaMeldekortDTO(
            meldekortId = meldekortId,
            personId = long("PERSON_ID"),
            datoInnkommet = localDateOrNull("DATO_INNKOMMET"),
            statusArbeidet = string("STATUS_ARBEIDET"),
            statusKurs = string("STATUS_KURS"),
            statusFerie = string("STATUS_FERIE"),
            statusSyk = string("STATUS_SYK"),
            statusAnnetFravaer = string("STATUS_ANNETFRAVAER"),
            regDato = localDateTime("REG_DATO"),
            modDato = localDateTime("MOD_DATO"),
            mksKortKode = string("MKSKORTKODE").let { ArenaMeldekortDTO.MKSKortKode.valueOf(it) },
            beregningstatusKode = string("BEREGNINGSTATUSKODE").let { ArenaMeldekortDTO.BeregningStatusKode.valueOf(it) },
            aar = aar,
            periodekode = periodekode,
            dager = dager,
            meldekortperiode = meldekortperiode,
        )
    }
}
