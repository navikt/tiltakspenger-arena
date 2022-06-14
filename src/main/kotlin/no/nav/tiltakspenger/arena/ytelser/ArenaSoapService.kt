package no.nav.tiltakspenger.arena.ytelser

import mu.KotlinLogging
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.HentYtelseskontraktListeSikkerhetsbegrensning
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.YtelseskontraktV3
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.Periode
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.Ytelseskontrakt
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.meldinger.HentYtelseskontraktListeRequest
import java.time.LocalDate
import javax.ws.rs.InternalServerErrorException

class ArenaSoapService(
    private val ytelseskontraktV3Service: YtelseskontraktV3,
    // private val tiltakOgAktivitetV1Service: TiltakOgAktivitetV1,
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
        val periode = Periode()
        periode.fom = fom
        periode.tom = tom
        val request = HentYtelseskontraktListeRequest()
        request.periode = periode
        request.personidentifikator = fnr
        return try {
            val response = ytelseskontraktV3Service.hentYtelseskontraktListe(request)
            response.ytelseskontraktListe
        } catch (exception: HentYtelseskontraktListeSikkerhetsbegrensning) {
            log.error("HentYtelseskontraktListeSikkerhetsbegrensning feil:", exception)
            throw InternalServerErrorException()
        }
    }

    /*
    fun getTiltaksaktivitetListe(fnr: String?): List<Tiltaksaktivitet> {
        val request = HentTiltakOgAktiviteterForBrukerRequest()
        request.personident = fnr
        return try {
            val response = tiltakOgAktivitetV1Service!!.hentTiltakOgAktiviteterForBruker(request)
            response.tiltaksaktivitetListe
        } catch (exception: HentTiltakOgAktiviteterForBrukerUgyldigInput) {
            log.error("Ugyldig input:", exception)
            throw BadRequestException()
        } catch (exception: HentTiltakOgAktiviteterForBrukerSikkerhetsbegrensning) {
            log.error("Systembruker har ikke tilgang:", exception)
            throw ForbiddenException()
        } catch (exception: HentTiltakOgAktiviteterForBrukerPersonIkkeFunnet) {
            log.error("Person ikke funnet:", exception)
            throw NotFoundException()
        }
    }
     */
}
