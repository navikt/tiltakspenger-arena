package no.nav.tiltakspenger.arena.repository

import io.github.oshai.kotlinlogging.KotlinLogging
import kotliquery.Row
import kotliquery.TransactionalSession
import kotliquery.queryOf
import no.nav.tiltakspenger.libs.logging.Sikkerlogg
import java.time.LocalDate

class MeldekortDAO(
    private val personDao: PersonDAO,
    private val meldekortdagDAO: MeldekortdagDAO,
) {
    private val logger = KotlinLogging.logger {}


    fun findByFnr(
        fnr: String,
        fraOgMedDato: LocalDate,
        tilOgMedDato: LocalDate,
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
                            m.MELDEKORT_ID            AS MELDEKORT_ID,
                            m.DATO_INNKOMMET          AS DATO_INNKOMMET,
                            m.STATUS_ARBEIDET         AS STATUS_ARBEIDET,
                            m.STATUS_KURS             AS STATUS_KURS,
                            m.STATUS_FERIE            AS STATUS_FERIE,
                            m.STATUS_SYK              AS STATUS_SYK,
                            m.STATUS_ANNETFRAVAER     AS STATUS_ANNETFRAVAER,
                            m.REG_DATO                AS REG_DATO,
                            m.MOD_DATO                AS MOD_DATO,
                            m.MKSKORTKODE             AS MKSKORTKODE,
                            m.BEREGNINGSTATUSKODE     AS BEREGNINGSTATUSKODE,
                            mp.AAR                    AS AAR,
                            mp.PERIODEKODE            AS PERIODEKODE,
                            mp.UKENR_UKE1             AS UKENR_UKE1,
                            mp.UKENR_UKE2             AS UKENR_UKE2,
                            mp.DATO_FRA               AS DATO_FRA,
                            mp.DATO_TIL               AS DATO_TIL
                        FROM MELDEKORT m
                            INNER JOIN PERSON p on m.PERSON_ID = p.PERSON_ID
                            INNER JOIN MELDEKORTPERIODE mp on m.AAR = mp.AAR AND m.PERIODEKODE = mp.PERIODEKODE
                        WHERE p.FODSELSNR = :fnr
                        AND (
                            mp.DATO_FRA <= TO_DATE(:tilOgMedDato, 'YYYY-MM-DD') 
                            AND mp.DATO_TIL >= TO_DATE(:fraOgMedDato, 'YYYY-MM-DD')
                        )
                    """.trimIndent(),
                paramMap = mapOf(
                    "fnr" to fnr,
                    "fraOgMedDato" to fraOgMedDato,
                    "tilOgMedDato" to tilOgMedDato,
                ),
            ).map { row -> row.toMeldekort(txSession) }
                .asList,
        )
    }

    private fun Row.toMeldekort(txSession: TransactionalSession): ArenaMeldekortDTO {
        val meldekortId = string("MELDEKORT_ID")
        val aar = int("AAR")
        val dager = meldekortdagDAO.findByMeldekortId(meldekortId, txSession)

        return ArenaMeldekortDTO(
            meldekortId = meldekortId,
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
            dager = dager,
            totaltArbeidetTimer = dager.sumOf { it.arbeidetTimer },
            meldekortperiode = ArenaMeldekortperiodeDTO(
                aar = aar,
                periodekode = int("PERIODEKODE"),
                ukenrUke1 = int("UKENR_UKE1"),
                ukenrUke2 = int("UKENR_UKE2"),
                datoFra = localDate("DATO_FRA"),
                datoTil = localDate("DATO_TIL"),
            ),
        )
    }
}
