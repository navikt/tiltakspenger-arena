package no.nav.tiltakspenger.arena.ytelser

import mu.KotlinLogging
import no.nav.common.cxf.CXFClient
import no.nav.common.cxf.StsConfig
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.YtelseskontraktV3
import org.apache.cxf.ext.logging.LoggingOutInterceptor
import javax.xml.namespace.QName

class ArenaSoapConfig(
    private val ytelseskontraktUrl: String,
    private val stsUrl: String,
    private val stsUsername: String,
    private val stsPassword: String,
//    private val tiltakogaktivitetUrl: String,
) {
    companion object {
        private val log = KotlinLogging.logger {}
    }

    private val stsConfig: StsConfig
        private get() = StsConfig
            .builder()
            .url(stsUrl)
            .username(stsUsername)
            .password(stsPassword)
            .build()

    fun ytelseskontraktV3(): YtelseskontraktV3 {
        log.info("Using URL {} for service {}", ytelseskontraktUrl, YtelseskontraktV3::class.java.simpleName)
        return CXFClient(YtelseskontraktV3::class.java)
            .wsdl("classpath:wsdl/tjenestespesifikasjon/no/nav/tjeneste/virksomhet/ytelseskontrakt/v3/Binding.wsdl")
            .serviceName(QName("http://nav.no/tjeneste/virksomhet/ytelseskontrakt/v3/Binding", "Ytelseskontrakt_v3"))
            .endpointName(
                QName(
                    "http://nav.no/tjeneste/virksomhet/ytelseskontrakt/v3/Binding",
                    "Ytelseskontrakt_v3Port"
                )
            )
            .withOutInterceptor(LoggingOutInterceptor())
            .address(ytelseskontraktUrl)
            .configureStsForSystemUser(stsConfig)
            .build()
    }

    /*
    fun tiltakOgAktivitetV1(): TiltakOgAktivitetV1 {
        log.info("Using URL {} for service {}", tiltakogaktivitetUrl, TiltakOgAktivitetV1::class.java.simpleName)
        return CXFClient(TiltakOgAktivitetV1::class.java)
            .wsdl("classpath:wsdl/tjenestespesifikasjon/no/nav/tjeneste/virksomhet/tiltakogaktivitet/v1/Binding.wsdl")
            .serviceName(
                QName(
                    "http://nav.no/tjeneste/virksomhet/tiltakOgAktivitet/v1/binding",
                    "TiltakOgAktivitet_v1"
                )
            )
            .endpointName(
                QName(
                    "http://nav.no/tjeneste/virksomhet/tiltakOgAktivitet/v1/binding",
                    "TiltakOgAktivitet_v1Port"
                )
            )
            .withOutInterceptor(LoggingOutInterceptor())
            .address(tiltakogaktivitetUrl)
            .configureStsForSystemUser(stsConfig)
            .build()
    }

     */
}
