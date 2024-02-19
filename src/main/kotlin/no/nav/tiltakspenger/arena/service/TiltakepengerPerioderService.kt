package no.nav.tiltakspenger.arena.service

import mu.KotlinLogging
import no.nav.tiltakspenger.arena.Configuration
import no.nav.tiltakspenger.arena.Profile
import no.nav.tiltakspenger.arena.felles.Periode
import no.nav.tiltakspenger.arena.felles.inneholderOverlapp
import no.nav.tiltakspenger.arena.felles.leggSammen
import no.nav.tiltakspenger.arena.repository.SakRepository
import no.nav.tiltakspenger.arena.ytelser.ArenaSoapService
import no.nav.tiltakspenger.arena.ytelser.filterKunTiltakspenger
import no.nav.tiltakspenger.arena.ytelser.mapArenaYtelser
import no.nav.tiltakspenger.arena.ytelser.mapArenaYtelserFraDB
import no.nav.tiltakspenger.libs.arena.ytelse.ArenaYtelseResponsDTO
import java.time.LocalDate

class TiltakepengerPerioderService(
    private val arenaSoapService: ArenaSoapService,
    private val arenaSakRepository: SakRepository,
) {

    companion object {
        private val LOG = KotlinLogging.logger {}
        private val SECURELOG = KotlinLogging.logger("tjenestekall")
    }

    fun hentTiltakspengerPerioder(ident: String, fom: LocalDate? = null, tom: LocalDate? = null): List<Periode> {
        val wsRespons: ArenaYtelseResponsDTO =
            mapArenaYtelser(arenaSoapService.getYtelser(fnr = ident, fom = fom, tom = tom))
                .filterKunTiltakspenger()
        LOG.info { "Antall saker fra ws : ${wsRespons.saker?.size}" }
        if (Configuration.applicationProfile() == Profile.DEV) {
            try {
                val dbRespons: ArenaYtelseResponsDTO =
                    mapArenaYtelserFraDB(arenaSakRepository.hentSakerForFnr(fnr = ident))
                LOG.info { "Antall saker fra db : ${dbRespons.saker?.size}" }
                if (wsRespons == dbRespons) {
                    LOG.info { "Lik response fra webservice og db" }
                } else {
                    LOG.info { "Ulik response fra webservice og db" }
                    SECURELOG.info { "Ulik response fra webservice og db for ident $ident" }
                    SECURELOG.info { "webservice: $wsRespons" }
                    SECURELOG.info { "db: $dbRespons" }
                }
                LOG.info { "Antall vedtak fra webservicen er ${wsRespons.saker?.flatMap { it.vedtak }?.size ?: 0}" }
                LOG.info { "Antall vedtak fra db er ${dbRespons.saker?.flatMap { it.vedtak }?.size ?: 0}" }

                return sammenhengendePerioder(dbRespons)
            } catch (e: Exception) {
                LOG.info("Kall mot Arena db feilet", e)
            }
        } else {
            LOG.info("Vi er visst ikke i dev")
        }
        return sammenhengendePerioder(wsRespons)
    }

    private fun sammenhengendePerioder(respons: ArenaYtelseResponsDTO): List<Periode> {
        val vedtakPerioder = respons.saker
            ?.flatMap { it.vedtak }
            ?.map {
                Periode(
                    it.vedtaksperiodeFom ?: LocalDate.MIN,
                    it.vedtaksperiodeTom ?: LocalDate.MAX,
                )
            }
        if (vedtakPerioder != null) {
            if (vedtakPerioder.inneholderOverlapp()) {
                LOG.info { "Vedtaksperiodene fra Arena overlapper hverandre" }
            }
            val sammenhengendePerioder = vedtakPerioder.leggSammen()
            LOG.info { "Antall sammenhengende vedtaksperioder er ${sammenhengendePerioder.size}" }
            return sammenhengendePerioder
        }
        return emptyList()
    }
}
