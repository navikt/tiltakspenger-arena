package no.nav.tiltakspenger.arena.repository

import io.github.oshai.kotlinlogging.KotlinLogging
import kotliquery.sessionOf
import no.nav.tiltakspenger.arena.db.Datasource
import no.nav.tiltakspenger.libs.logging.Sikkerlogg
import java.time.LocalDate

class UtbetalingshistorikkRepository(
    private val personDAO: PersonDAO = PersonDAO(),
    private val meldekortDAO: MeldekortDAO = MeldekortDAO(),
    private val posteringerDAO: PosteringerDAO = PosteringerDAO(),
    private val utbetalingsgrunnlagDAO: UtbetalingsgrunnlagDAO = UtbetalingsgrunnlagDAO(),
    private val beregningsloggDAO: BeregningsloggDAO = BeregningsloggDAO(),
    private val anmerkningDAO: AnmerkningDAO = AnmerkningDAO(),
) {
    companion object {
        private val LOG = KotlinLogging.logger {}
    }

    fun hentMeldekortForFnr(
        fnr: String,
        fraOgMedDato: LocalDate = LocalDate.of(1900, 1, 1),
        tilOgMedDato: LocalDate = LocalDate.of(2999, 12, 31),
    ): List<ArenaUtbetalingshistorikkDTO> {
        sessionOf(Datasource.hikariDataSource).use { session ->
            session.transaction { txSession ->
                val person = personDAO.findByFnr(fnr, txSession)
                if (person == null) {
                    LOG.info { "Fant ikke person" }
                    Sikkerlogg.info { "Fant ikke person med ident $fnr" }
                    return emptyList()
                }

                val historikkPosteringer = posteringerDAO.hentVedtakForUtbetalingshistorikk(
                    personId = person.personId,
                    txSession = txSession,
                    fraOgMedDato = fraOgMedDato,
                    tilOgMedDato = tilOgMedDato,
                )
                val historikkUtbetalingsgrunnlag = utbetalingsgrunnlagDAO.hentVedtakForUtbetalingshistorikk(
                    personId = person.personId,
                    txSession = txSession,
                    fraOgMedDato = fraOgMedDato,
                    tilOgMedDato = tilOgMedDato,
                )
                val historikkBeregningslogg = beregningsloggDAO.hentVedtakForUtbetalingshistorikk(
                    personId = person.personId,
                    txSession = txSession,
                    fraOgMedDato = fraOgMedDato,
                    tilOgMedDato = tilOgMedDato,
                )
                val historikkAnmerkninger = anmerkningDAO.hentVedtakForUtbetalingshistorikk(
                    personId = person.personId,
                    txSession = txSession,
                    fraOgMedDato = fraOgMedDato,
                    tilOgMedDato = tilOgMedDato,
                )

                val utbetalingshistorikk =
                    historikkPosteringer + historikkUtbetalingsgrunnlag + historikkBeregningslogg + historikkAnmerkninger

                LOG.info { "Antall innslag av utbetalingshistorikk er ${utbetalingshistorikk.size}" }
                return utbetalingshistorikk.sortedBy { it.dato }
            }
        }
    }
}
