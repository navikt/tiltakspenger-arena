package no.nav.tiltakspenger.arena.ytelser

import javax.xml.namespace.QName
import mu.KotlinLogging
import no.nav.common.cxf.CXFClient
import no.nav.common.cxf.StsConfig
import no.nav.tiltakspenger.arena.Configuration
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.YtelseskontraktV3
import org.apache.cxf.ext.logging.LoggingOutInterceptor

class ArenaClientConfiguration(
    private val arenaSoapConfig: Configuration.ArenaSoapConfig = Configuration.ArenaSoapConfig(),
) {
    companion object {
        private val LOG = KotlinLogging.logger {}
        private val securelog = KotlinLogging.logger("tjenestekall")
    }

    private val stsConfig: StsConfig
        get() = StsConfig
            .builder()
            .url(arenaSoapConfig.stsUrl)
            .username(arenaSoapConfig.stsUsername)
            .password(arenaSoapConfig.stsPassword)
            .build()

    fun ytelseskontraktV3(): YtelseskontraktV3 {
        securelog.info { arenaSoapConfig }
        LOG.info { "stsurl: ${arenaSoapConfig.stsUrl}" }
        LOG.info { "url: ${arenaSoapConfig.ytelseskontraktUrl}" }
        LOG.info { "username: ${arenaSoapConfig.stsUsername}" }
        LOG.info(
            "Using URL {} for service {}",
            arenaSoapConfig.ytelseskontraktUrl,
            YtelseskontraktV3::class.java.simpleName
        )
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
            .address(arenaSoapConfig.ytelseskontraktUrl)
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
