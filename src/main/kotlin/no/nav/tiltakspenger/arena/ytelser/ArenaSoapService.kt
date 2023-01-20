package no.nav.tiltakspenger.arena.ytelser

import mu.KotlinLogging
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.HentYtelseskontraktListeSikkerhetsbegrensning
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.YtelseskontraktV3
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.Periode
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.Ytelseskontrakt
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.meldinger.HentYtelseskontraktListeRequest
import java.time.LocalDate

private val LOG = KotlinLogging.logger {}

class ArenaSoapService(
    private val ytelseskontraktV3Service: YtelseskontraktV3,
) {

    fun ping(): Boolean {
        return try {
            ytelseskontraktV3Service.ping()
            true
        } catch (e: Exception) {
            LOG.error("Failed to ping service", e)
            false
        }
    }

    fun getYtelser(fnr: String, fom: LocalDate?, tom: LocalDate?): List<Ytelseskontrakt> {
        val request = HentYtelseskontraktListeRequest().apply {
            personidentifikator = fnr
            periode = Periode().apply {
                this.fom = fom
                this.tom = tom
            }
        }
        return try {
            val response = ytelseskontraktV3Service.hentYtelseskontraktListe(request)
            response.ytelseskontraktListe
        } catch (exception: HentYtelseskontraktListeSikkerhetsbegrensning) {
            LOG.error("HentYtelseskontraktListeSikkerhetsbegrensning logget til securelog")
            LOG.error(exception) { "HentYtelseskontraktListeSikkerhetsbegrensning feil" }
            throw exception
        }
    }
}
