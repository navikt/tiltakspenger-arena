package no.nav.tiltakspenger.arena.repository.anmerkning

import kotliquery.TransactionalSession
import no.nav.tiltakspenger.arena.db.Datasource.withTx
import java.time.LocalDate

class AnmerkningRepository(
    private val anmerkningDAO: AnmerkningDAO = AnmerkningDAO(),
) {
    fun hentVedtakForUtbetalingshistorikk(
        personId: Long,
        fraOgMedDato: LocalDate,
        tilOgMedDato: LocalDate,
        txSession: TransactionalSession,
    ): List<ArenaAnmerkningMedPeriodeDTO> {
        withTx(txSession) { tx ->
            return anmerkningDAO.hentVedtakForUtbetalingshistorikk(
                personId = personId,
                txSession = tx,
                fraOgMedDato = fraOgMedDato,
                tilOgMedDato = tilOgMedDato,
            )
        }
    }

    fun hentAnmerkningerForMeldekort(
        meldekortId: Long,
        txSession: TransactionalSession? = null,
    ): List<ArenaAnmerkningDTO> {
        withTx(txSession) { tx ->
            return anmerkningDAO.hentAnmerkningerForMeldekort(
                meldekortId = meldekortId,
                txSession = tx,
            )
        }
    }
}
