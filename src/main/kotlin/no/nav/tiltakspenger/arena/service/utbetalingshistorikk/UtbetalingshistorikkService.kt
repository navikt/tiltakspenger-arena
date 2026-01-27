package no.nav.tiltakspenger.arena.service.utbetalingshistorikk

import io.github.oshai.kotlinlogging.KotlinLogging
import kotliquery.sessionOf
import no.nav.tiltakspenger.arena.db.Datasource
import no.nav.tiltakspenger.arena.repository.anmerkning.AnmerkningDAO
import no.nav.tiltakspenger.arena.repository.anmerkning.AnmerkningRepository
import no.nav.tiltakspenger.arena.repository.beregningslogg.BeregningsloggRepository
import no.nav.tiltakspenger.arena.repository.meldekort.MeldekortRepository
import no.nav.tiltakspenger.arena.repository.person.PersonDAO
import no.nav.tiltakspenger.arena.repository.postering.PosteringRepository
import no.nav.tiltakspenger.arena.repository.utbetalingsgrunnlag.UtbetalingsgrunnlagRepository
import no.nav.tiltakspenger.arena.repository.vedtakfakta.VedtakfaktaDAO
import no.nav.tiltakspenger.arena.service.anmerkning.AnmerkningDetaljer
import no.nav.tiltakspenger.arena.service.anmerkning.tilAnmerkningDetaljer
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.VedtakfaktaMeldekortDetaljer
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.tilVedtakfaktaMeldekortDetaljer
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
                            .sortedBy { it.dato }

                logger.info { "Antall innslag av utbetalingshistorikk er ${utbetalingshistorikk.size}" }
                return utbetalingshistorikk
            }
        }
    }

    fun hentAnmerkningerForMeldekort(
        meldekortId: Long?,
    ): List<AnmerkningDetaljer> {
        return meldekortId?.let { id ->
            anmerkningRepository.hentAnmerkningerForMeldekort(id)
                .map { it.tilAnmerkningDetaljer() }
        } ?: emptyList()
    }

    fun hentVedtakfaktaForVedtak(
        vedtakId: Long?,
    ): VedtakfaktaMeldekortDetaljer? {
        return vedtakId?.let { id ->
            vedtakfaktaDAO.findUtbetalingshistorikkVedtakfakta(id)
                .tilVedtakfaktaMeldekortDetaljer()
        }
    }
}
