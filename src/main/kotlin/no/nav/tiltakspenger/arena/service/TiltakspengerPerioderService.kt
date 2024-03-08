package no.nav.tiltakspenger.arena.service

import mu.KotlinLogging
import no.nav.tiltakspenger.arena.Configuration
import no.nav.tiltakspenger.arena.Profile
import no.nav.tiltakspenger.arena.felles.PeriodeMedVerdier
import no.nav.tiltakspenger.arena.repository.ArenaSakMedMinstEttVedtakDTO
import no.nav.tiltakspenger.arena.repository.SakRepository
import no.nav.tiltakspenger.arena.ytelser.ArenaSoapService
import no.nav.tiltakspenger.arena.ytelser.filterKunTiltakspenger
import no.nav.tiltakspenger.arena.ytelser.mapArenaYtelser
import no.nav.tiltakspenger.arena.ytelser.mapArenaYtelserFraDB
import no.nav.tiltakspenger.libs.arena.ytelse.ArenaYtelseResponsDTO
import java.time.LocalDate

class TiltakspengerPerioderService(
    private val arenaSoapService: ArenaSoapService,
    private val arenaSakRepository: SakRepository,
) {

    companion object {
        private val LOG = KotlinLogging.logger {}
        private val SECURELOG = KotlinLogging.logger("tjenestekall")
    }

    fun hentTiltakspengerPerioder(
        ident: String,
        fom: LocalDate = LocalDate.of(1900, 1, 1),
        tom: LocalDate = LocalDate.of(2999, 12, 31),
    ): PeriodeMedVerdier<VedtakDetaljer>? =
        if (Configuration.applicationProfile() == Profile.DEV) {
            val sakerFraDb = arenaSakRepository.hentSakerForFnr(fnr = ident, fom = fom, tom = tom)
            sammenlignDbMedWs(ident, fom, tom, sakerFraDb)
            mapTiltakspengerFraArenaTilVedtaksperioder(sakerFraDb)
        } else {
            null
        }

    private fun sammenlignDbMedWs(
        ident: String,
        fom: LocalDate? = null,
        tom: LocalDate? = null,
        sakerFraDb: List<ArenaSakMedMinstEttVedtakDTO>,
    ) {
        val sakerFraWs = arenaSoapService.getYtelser(fnr = ident, fom = fom, tom = tom)
        val wsRespons: ArenaYtelseResponsDTO = mapArenaYtelser(sakerFraWs).filterKunTiltakspenger()
        val dbRespons: ArenaYtelseResponsDTO = mapArenaYtelserFraDB(sakerFraDb)
        SECURELOG.info { "Antall saker fra db : ${dbRespons.saker?.size}" }
        SECURELOG.info { "Antall saker fra ws : ${wsRespons.saker?.size}" }
        try {
            if (wsRespons == dbRespons) {
                SECURELOG.info { "Lik response fra webservice og db" }
            } else {
                SECURELOG.info { "Ulik response fra webservice og db for ident $ident" }
                SECURELOG.info { "webservice: $wsRespons" }
                SECURELOG.info { "db: $dbRespons" }
            }
            SECURELOG.info { "Antall vedtak fra webservicen er ${wsRespons.saker?.flatMap { it.vedtak }?.size ?: 0}" }
            SECURELOG.info { "Antall vedtak fra db er ${dbRespons.saker?.flatMap { it.vedtak }?.size ?: 0}" }
        } catch (e: Exception) {
            SECURELOG.info("Kall mot Arena db feilet", e)
        }
    }
}
