package no.nav.tiltakspenger.arena.ytelser

import mu.KotlinLogging
import no.nav.common.cxf.CXFClient
import no.nav.common.cxf.StsConfig
import no.nav.tiltakspenger.arena.Configuration
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.YtelseskontraktV3
import javax.xml.namespace.QName

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
            YtelseskontraktV3::class.java.simpleName,
        )
        return CXFClient(YtelseskontraktV3::class.java)
            .wsdl("classpath:wsdl/tjenestespesifikasjon/no/nav/tjeneste/virksomhet/ytelseskontrakt/v3/Binding.wsdl")
            .serviceName(QName("http://nav.no/tjeneste/virksomhet/ytelseskontrakt/v3/Binding", "Ytelseskontrakt_v3"))
            .endpointName(
                QName(
                    "http://nav.no/tjeneste/virksomhet/ytelseskontrakt/v3/Binding",
                    "Ytelseskontrakt_v3Port",
                ),
            )
            //.withOutInterceptor(LoggingOutInterceptor())
            .address(arenaSoapConfig.ytelseskontraktUrl)
            .configureStsForSystemUser(stsConfig)
            .build()
    }
}
