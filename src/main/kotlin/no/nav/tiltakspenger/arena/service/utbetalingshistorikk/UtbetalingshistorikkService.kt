package no.nav.tiltakspenger.arena.service.utbetalingshistorikk

import io.github.oshai.kotlinlogging.KotlinLogging
import kotliquery.sessionOf
import no.nav.tiltakspenger.arena.db.Datasource
import no.nav.tiltakspenger.arena.repository.AnmerkningDAO
import no.nav.tiltakspenger.arena.repository.AnmerkningRepository
import no.nav.tiltakspenger.arena.repository.ArenaAnmerkningDTO
import no.nav.tiltakspenger.arena.repository.ArenaUtbetalingshistorikkVedtakfaktaDTO
import no.nav.tiltakspenger.arena.repository.BeregningsloggRepository
import no.nav.tiltakspenger.arena.repository.MeldekortRepository
import no.nav.tiltakspenger.arena.repository.PersonDAO
import no.nav.tiltakspenger.arena.repository.PosteringRepository
import no.nav.tiltakspenger.arena.repository.UtbetalingsgrunnlagRepository
import no.nav.tiltakspenger.arena.repository.VedtakfaktaDAO
import no.nav.tiltakspenger.libs.logging.Sikkerlogg
import java.time.LocalDate

data class UtbetalingshistorikkService(
    val personDAO: PersonDAO = PersonDAO(),
    val posteringRepository: PosteringRepository = PosteringRepository(),
    val utbetalingsgrunnlagRepository: UtbetalingsgrunnlagRepository = UtbetalingsgrunnlagRepository(),
    val beregningsloggRepository: BeregningsloggRepository = BeregningsloggRepository(),
    val anmerkningRepository: AnmerkningRepository = AnmerkningRepository(),
    val meldekortRepository: MeldekortRepository = MeldekortRepository(),
    val anmerkningDAO: AnmerkningDAO = AnmerkningDAO(),
    val vedtakfaktaDAO: VedtakfaktaDAO = VedtakfaktaDAO(),
) {
    private val logger = KotlinLogging.logger {}

    fun hentUtbetalingshistorikkForFnr(
        fnr: String,
        fraOgMedDato: LocalDate,
        tilOgMedDato: LocalDate,
    ): List<UtbetalingshistorikkDetaljer> {
        sessionOf(Datasource.hikariDataSource).use { session ->
            session.transaction { txSession ->
                val person = personDAO.findByFnr(fnr, txSession)
                if (person == null) {
                    logger.info { "Fant ikke person" }
                    Sikkerlogg.info { "Fant ikke person med ident $fnr" }
                    return emptyList()
                }

                val posteringer = posteringRepository.hentVedtakForUtbetalingshistorikk(
                    personId = person.personId,
                    txSession = txSession,
                    fraOgMedDato = fraOgMedDato,
                    tilOgMedDato = tilOgMedDato,
                )

                val utbetalingsgrunnlag = utbetalingsgrunnlagRepository.hentVedtakForUtbetalingshistorikk(
                    personId = person.personId,
                    txSession = txSession,
                    fraOgMedDato = fraOgMedDato,
                    tilOgMedDato = tilOgMedDato,
                )

                val beregningslogg = beregningsloggRepository.hentVedtakForUtbetalingshistorikk(
                    personId = person.personId,
                    txSession = txSession,
                    fraOgMedDato = fraOgMedDato,
                    tilOgMedDato = tilOgMedDato,
                )

                val anmerkninger = anmerkningRepository.hentVedtakForUtbetalingshistorikk(
                    personId = person.personId,
                    txSession = txSession,
                    fraOgMedDato = fraOgMedDato,
                    tilOgMedDato = tilOgMedDato,
                )

                val meldekortMedFeil = meldekortRepository.hentVedtakForUtbetalingshistorikk(
                    personId = person.personId,
                    txSession = txSession,
                    fraOgMedDato = fraOgMedDato,
                    tilOgMedDato = tilOgMedDato,
                )

                val utbetalingshistorikk =
                    posteringer.map { it.tilUtbetalingshistorikk() } +
                        utbetalingsgrunnlag.map { it.tilUtbetalingshistorikk() } +
                        beregningslogg.map { it.tilUtbetalingshistorikk() } +
                        anmerkninger.map { it.tilUtbetalingshistorikk() } +
                        meldekortMedFeil.map { it.tilUtbetalingshistorikk() }

                logger.info { "Antall innslag av utbetalingshistorikk er ${utbetalingshistorikk.size}" }
                return utbetalingshistorikk
            }
        }
    }

    fun hentAnmerkningerOgVedtakfakta(
        vedtakId: Long,
        meldekortId: Long,
    ): Pair<List<ArenaAnmerkningDTO>, ArenaUtbetalingshistorikkVedtakfaktaDTO> {
        sessionOf(Datasource.hikariDataSource).use { session ->
            session.transaction { txSession ->
                val anmerkninger = anmerkningRepository.hentAnmerkningerForVedtakOgMeldekort(
                    vedtakId = vedtakId,
                    meldekortId = meldekortId,
                    txSession = txSession,
                )
                val vedtakfakta = vedtakfaktaDAO.findBeregningVedtakfaktaByVedtakId(
                    vedtakId = vedtakId,
                    txSession = txSession,
                )
                return anmerkninger to vedtakfakta
            }
        }
    }
}
