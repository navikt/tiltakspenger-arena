package no.nav.tiltakspenger.arena.repository.meldekort

import io.github.oshai.kotlinlogging.KotlinLogging
import kotliquery.TransactionalSession
import kotliquery.sessionOf
import no.nav.tiltakspenger.arena.db.Datasource
import no.nav.tiltakspenger.arena.db.Datasource.withTx
import no.nav.tiltakspenger.arena.repository.person.PersonDAO
import no.nav.tiltakspenger.libs.logging.Sikkerlogg
import java.time.LocalDate

class MeldekortRepository(
    private val personDAO: PersonDAO = PersonDAO(),
    private val meldekortDAO: MeldekortDAO = MeldekortDAO(),
) {
    companion object {
        private val LOG = KotlinLogging.logger {}
    }

    fun hentMeldekortForFnr(
        fnr: String,
        fraOgMedDato: LocalDate,
        tilOgMedDato: LocalDate,
    ): List<ArenaMeldekortDTO> {
        sessionOf(Datasource.hikariDataSource).use {
            it.transaction { txSession ->
                val person = personDAO.findByFnr(fnr, txSession)
                if (person == null) {
                    LOG.info { "Fant ikke person" }
                    Sikkerlogg.info { "Fant ikke person med ident $fnr" }
                    return emptyList()
                }

                val meldekort = meldekortDAO.findByPersonId(
                    personId = person.personId,
                    txSession = txSession,
                    fraOgMedDato = fraOgMedDato,
                    tilOgMedDato = tilOgMedDato,
                )
                LOG.info { "Antall meldekort er ${meldekort.size}" }
                return meldekort
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
