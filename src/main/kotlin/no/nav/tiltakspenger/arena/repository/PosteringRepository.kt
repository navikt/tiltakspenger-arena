package no.nav.tiltakspenger.arena.repository

import kotliquery.TransactionalSession
import no.nav.tiltakspenger.arena.db.Datasource.withTx
import java.time.LocalDate

class PosteringRepository(
    private val posteringerDAO: PosteringerDAO = PosteringerDAO(),
) {
    fun hentVedtakForUtbetalingshistorikk(
        personId: Long,
        fraOgMedDato: LocalDate,
        tilOgMedDato: LocalDate,
        txSession: TransactionalSession,
    ): List<ArenaPosteringDTO> {
        withTx(txSession) { tx ->
            return posteringerDAO.hentVedtakForUtbetalingshistorikk(
                personId = personId,
                txSession = tx,
                fraOgMedDato = fraOgMedDato,
                tilOgMedDato = tilOgMedDato,
            )
        }
    }
}
