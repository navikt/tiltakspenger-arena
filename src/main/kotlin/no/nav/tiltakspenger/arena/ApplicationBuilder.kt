package no.nav.tiltakspenger.arena

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import no.nav.tiltakspenger.arena.repository.sak.SakRepository
import no.nav.tiltakspenger.arena.service.meldekort.MeldekortService
import no.nav.tiltakspenger.arena.service.utbetalingshistorikk.UtbetalingshistorikkService
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.RettighetDetaljerServiceImpl
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.VedtakDetaljerServiceImpl
import no.nav.tiltakspenger.libs.texas.client.TexasHttpClient
import no.nav.tiltakspenger.libs.tid.zoneIdOslo
import java.time.Clock

internal fun start(
    port: Int = Configuration.httpPort(),
    clock: Clock = Clock.system(zoneIdOslo),
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

    val server = embeddedServer(
        factory = Netty,
        port = port,
        module = {
            tiltakApi(
                vedtakDetaljerService = vedtakDetaljerService,
                rettighetDetaljerService = rettighetDetaljerService,
                meldekortService = meldekortService,
                utbetalingshistorikkService = utbetalingshistorikkService,
                texasClient = texasClient,
            )
        },
    )
    server.application.attributes.put(isReadyKey, true)

    Runtime.getRuntime().addShutdownHook(
        Thread {
            server.application.attributes.put(isReadyKey, false)
            server.stop(gracePeriodMillis = 5_000, timeoutMillis = 10_000)
        },
    )
    server.start(wait = true)
}
