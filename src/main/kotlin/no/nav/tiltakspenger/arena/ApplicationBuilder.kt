package no.nav.tiltakspenger.arena

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tiltakspenger.arena.repository.sak.SakRepository
import no.nav.tiltakspenger.arena.service.meldekort.MeldekortService
import no.nav.tiltakspenger.arena.service.utbetalingshistorikk.UtbetalingshistorikkService
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.RettighetDetaljerServiceImpl
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.VedtakDetaljerServiceImpl
import no.nav.tiltakspenger.libs.ktor.common.oppstart.startApp
import no.nav.tiltakspenger.libs.texas.client.TexasHttpClient
import java.time.Clock

internal fun start(
    log: KLogger = KotlinLogging.logger {},
    port: Int = Configuration.httpPort(),
    host: String = "0.0.0.0",
    clock: Clock,
    isNais: Boolean = Configuration.isNais(),
) {
    val texasClient = TexasHttpClient(
        introspectionUrl = Configuration.naisTokenIntrospectionEndpoint,
        tokenUrl = Configuration.naisTokenEndpoint,
        tokenExchangeUrl = Configuration.tokenExchangeEndpoint,
        clock = clock,
    )
    val arenaSakRepository = SakRepository()

    val vedtakDetaljerService = VedtakDetaljerServiceImpl(
        arenaSakRepository = arenaSakRepository,
    )
    val rettighetDetaljerService = RettighetDetaljerServiceImpl(vedtakDetaljerService)
    val meldekortService = MeldekortService()
    val utbetalingshistorikkService = UtbetalingshistorikkService()

    startApp(
        log = log,
        port = port,
        host = host,
        isNais = isNais,
    ) { readiness ->
        tiltakApi(
            vedtakDetaljerService = vedtakDetaljerService,
            rettighetDetaljerService = rettighetDetaljerService,
            meldekortService = meldekortService,
            utbetalingshistorikkService = utbetalingshistorikkService,
            texasClient = texasClient,
            readiness = readiness,
        )
    }
}
