package no.nav.tiltakspenger.arena

import io.ktor.server.config.ApplicationConfig
import mu.KotlinLogging
import no.nav.helse.rapids_rivers.RapidApplication
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.tiltakspenger.arena.repository.SakRepository
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.RettighetDetaljerServiceImpl
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.VedtakDetaljerServiceImpl
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsClientImpl
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsTokenProviderClient
import no.nav.tiltakspenger.arena.ytelser.ArenaClientConfiguration
import no.nav.tiltakspenger.arena.ytelser.ArenaSoapService

private val LOG = KotlinLogging.logger {}

internal class ApplicationBuilder(val config: ApplicationConfig) : RapidsConnection.StatusListener {
    private val arenaSoapService = ArenaSoapService(ArenaClientConfiguration().ytelseskontraktV3())
    private val tokenProviderClient = ArenaOrdsTokenProviderClient(Configuration.ArenaOrdsConfig())
    private val arenaSakRepository = SakRepository()
    private val arenaOrdsClient = ArenaOrdsClientImpl(
        arenaOrdsConfig = Configuration.ArenaOrdsConfig(),
        arenaOrdsTokenProvider = tokenProviderClient,
    )

    private val vedtakDetaljerService = VedtakDetaljerServiceImpl(
        arenaSoapService = arenaSoapService,
        arenaSakRepository = arenaSakRepository,
    )
    private val rettighetDetaljerService = RettighetDetaljerServiceImpl(vedtakDetaljerService)

    private val rapidsConnection: RapidsConnection = RapidApplication.Builder(
        RapidApplication.RapidApplicationConfig.fromEnv(Configuration.rapidsAndRivers),
    )
        .withKtorModule {
            tiltakApi(
                arenaOrdsClient = arenaOrdsClient,
                vedtakDetaljerService = vedtakDetaljerService,
                rettighetDetaljerService = rettighetDetaljerService,
                config = config,
            )
        }
        .build()

    init {
        rapidsConnection.register(this)
    }

    fun start() {
        rapidsConnection.start()
    }

    override fun onStartup(rapidsConnection: RapidsConnection) {
        LOG.info { "Starting tiltakspenger-arena" }
    }

    override fun onShutdown(rapidsConnection: RapidsConnection) {
        LOG.info { "Stopping tiltakspenger-arena" }
        super.onShutdown(rapidsConnection)
    }
}
