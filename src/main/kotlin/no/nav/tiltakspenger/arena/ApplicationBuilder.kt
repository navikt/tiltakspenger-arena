package no.nav.tiltakspenger.arena

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import no.nav.tiltakspenger.arena.auth.texas.client.TexasClient
import no.nav.tiltakspenger.arena.repository.SakRepository
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.RettighetDetaljerServiceImpl
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.VedtakDetaljerServiceImpl
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsClientImpl
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsTokenProviderClient

internal fun start(
    port: Int = Configuration.httpPort(),
) {
    val texasClient = TexasClient(
        introspectionUrl = Configuration.naisTokenIntrospectionEndpoint,
    )
    val tokenProviderClient = ArenaOrdsTokenProviderClient(Configuration.ArenaOrdsConfig())
    val arenaSakRepository = SakRepository()
    val arenaOrdsClient = ArenaOrdsClientImpl(
        arenaOrdsConfig = Configuration.ArenaOrdsConfig(),
        arenaOrdsTokenProvider = tokenProviderClient,
    )

    val vedtakDetaljerService = VedtakDetaljerServiceImpl(
        arenaSakRepository = arenaSakRepository,
    )
    val rettighetDetaljerService = RettighetDetaljerServiceImpl(vedtakDetaljerService)

    val server = embeddedServer(
        factory = Netty,
        port = port,
        module = {
            tiltakApi(
                arenaOrdsClient = arenaOrdsClient,
                vedtakDetaljerService = vedtakDetaljerService,
                rettighetDetaljerService = rettighetDetaljerService,
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
