package no.nav.tiltakspenger.arena.repository.meldekort

import kotliquery.TransactionalSession
import kotliquery.sessionOf
import no.nav.tiltakspenger.arena.db.Datasource
import no.nav.tiltakspenger.arena.db.Datasource.withTx
import no.nav.tiltakspenger.arena.repository.person.PersonDAO
import java.time.LocalDate

class MeldekortRepository(
    private val personDAO: PersonDAO = PersonDAO(),
    private val meldekortDAO: MeldekortDAO = MeldekortDAO(),
) {
    fun hentMeldekortForFnr(
        fnr: String,
        fraOgMedDato: LocalDate,
        tilOgMedDato: LocalDate,
    ): List<ArenaMeldekortDTO> {
        sessionOf(Datasource.hikariDataSource).use {
            it.transaction { txSession ->
                val person = personDAO.findByFnr(fnr, txSession)
                    ?: return emptyList()

                return meldekortDAO.findByPersonId(
                    personId = person.personId,
                    txSession = txSession,
                    fraOgMedDato = fraOgMedDato,
                    tilOgMedDato = tilOgMedDato,
                )
            }
        }
    }

    fun hentVedtakForUtbetalingshistorikk(
        personId: Long,
        fraOgMedDato: LocalDate,
        tilOgMedDato: LocalDate,
        txSession: TransactionalSession,
    ): List<ArenaBeregnetMeldekortMedFeilDTO> {
        withTx(txSession) { tx ->
            return meldekortDAO.hentVedtakForUtbetalingshistorikk(
                personId = personId,
                txSession = tx,
                fraOgMedDato = fraOgMedDato,
                tilOgMedDato = tilOgMedDato,
            )
        }
    }
}
