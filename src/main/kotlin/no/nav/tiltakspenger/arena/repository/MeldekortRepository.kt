package no.nav.tiltakspenger.arena.repository

import io.github.oshai.kotlinlogging.KotlinLogging
import kotliquery.sessionOf
import no.nav.tiltakspenger.arena.db.Datasource
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
        fraOgMedDato: LocalDate = LocalDate.of(1900, 1, 1),
        tilOgMedDato: LocalDate = LocalDate.of(2999, 12, 31),
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
}
