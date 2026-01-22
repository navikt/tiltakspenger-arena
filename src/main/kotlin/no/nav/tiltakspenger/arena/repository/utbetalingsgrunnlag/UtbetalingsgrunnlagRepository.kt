package no.nav.tiltakspenger.arena.repository.utbetalingsgrunnlag

import kotliquery.TransactionalSession
import no.nav.tiltakspenger.arena.db.Datasource.withTx
import java.time.LocalDate

class UtbetalingsgrunnlagRepository(
    private val utbetalingsgrunnlagDAO: UtbetalingsgrunnlagDAO = UtbetalingsgrunnlagDAO(),
) {
    fun hentVedtakForUtbetalingshistorikk(
        personId: Long,
        fraOgMedDato: LocalDate,
        tilOgMedDato: LocalDate,
        txSession: TransactionalSession? = null,
    ): List<ArenaUtbetalingsgrunnlagDTO> =
        withTx(txSession) { tx ->
            utbetalingsgrunnlagDAO.hentVedtakForUtbetalingshistorikk(
                personId = personId,
                txSession = tx,
                fraOgMedDato = fraOgMedDato,
                tilOgMedDato = tilOgMedDato,
            )
        }
}
