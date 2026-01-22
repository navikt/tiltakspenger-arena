package no.nav.tiltakspenger.arena.repository.beregningslogg

import kotliquery.TransactionalSession
import no.nav.tiltakspenger.arena.db.Datasource.withTx
import java.time.LocalDate

class BeregningsloggRepository(
    private val beregningsloggDAO: BeregningsloggDAO = BeregningsloggDAO(),
) {
    fun hentVedtakForUtbetalingshistorikk(
        personId: Long,
        fraOgMedDato: LocalDate,
        tilOgMedDato: LocalDate,
        txSession: TransactionalSession,
    ): List<ArenaBeregningsloggDTO> {
        withTx(txSession) { tx ->
            return beregningsloggDAO.hentVedtakForUtbetalingshistorikk(
                personId = personId,
                txSession = tx,
                fraOgMedDato = fraOgMedDato,
                tilOgMedDato = tilOgMedDato,
            )
        }
    }
}
