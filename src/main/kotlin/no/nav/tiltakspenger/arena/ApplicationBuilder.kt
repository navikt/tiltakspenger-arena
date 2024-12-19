package no.nav.tiltakspenger.arena

import io.ktor.server.config.ApplicationConfig
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import no.nav.tiltakspenger.arena.repository.SakRepository
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.RettighetDetaljerServiceImpl
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.VedtakDetaljerServiceImpl
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsClientImpl
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsTokenProviderClient

internal fun start(
    config: ApplicationConfig,
    port: Int = Configuration.httpPort(),
) {
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

    embeddedServer(
        factory = Netty,
        port = port,
        module = {
            tiltakApi(
                arenaOrdsClient = arenaOrdsClient,
                vedtakDetaljerService = vedtakDetaljerService,
                rettighetDetaljerService = rettighetDetaljerService,
                config = config,
            )
        },
    ).start(wait = true)
}
