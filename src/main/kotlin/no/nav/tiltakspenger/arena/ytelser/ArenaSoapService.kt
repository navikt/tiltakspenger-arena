package no.nav.tiltakspenger.arena.ytelser

import mu.KotlinLogging
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.HentYtelseskontraktListeSikkerhetsbegrensning
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.YtelseskontraktV3
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.Periode
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.Ytelseskontrakt
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.meldinger.HentYtelseskontraktListeRequest
import java.time.LocalDate

class ArenaSoapService(
    private val ytelseskontraktV3Service: YtelseskontraktV3,
) {
    companion object {
        private val log = KotlinLogging.logger {}
    }

    @Suppress("TooGenericExceptionCaught")
    fun ping(): Boolean {
        return try {
            ytelseskontraktV3Service.ping()
            true
        } catch (e: Exception) {
            log.error("Failed to ping service", e)
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
            log.error("HentYtelseskontraktListeSikkerhetsbegrensning feil:", exception)
            throw exception
        }
    }
}
