package no.nav.tiltakspenger.arena

import io.ktor.server.config.ApplicationConfig
import mu.KotlinLogging
import no.nav.helse.rapids_rivers.RapidApplication
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.tiltakspenger.arena.repository.SakRepository
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsClientImpl
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsTokenProviderClient
import no.nav.tiltakspenger.arena.ytelser.ArenaClientConfiguration
import no.nav.tiltakspenger.arena.ytelser.ArenaSoapService

private val LOG = KotlinLogging.logger {}

internal class ApplicationBuilder(val config: ApplicationConfig) : RapidsConnection.StatusListener {
    val arenaSoapService = ArenaSoapService(ArenaClientConfiguration().ytelseskontraktV3())
    val tokenProviderClient = ArenaOrdsTokenProviderClient(Configuration.ArenaOrdsConfig())
    val arenaSakRepository = SakRepository()
    val arenaOrdsClient = ArenaOrdsClientImpl(
        arenaOrdsConfig = Configuration.ArenaOrdsConfig(),
        arenaOrdsTokenProvider = tokenProviderClient,
    )

    val rapidsConnection: RapidsConnection = RapidApplication.Builder(
        RapidApplication.RapidApplicationConfig.fromEnv(Configuration.rapidsAndRivers),
    )
        .withKtorModule {
            tiltakApi(
                arenaSoapService = arenaSoapService,
                arenaSakRepository = arenaSakRepository,
                arenaOrdsClient = arenaOrdsClient,
                config = config,
            )
        }
        .build()
        .apply {
            ArenaYtelserService(
                rapidsConnection = this,
                arenaSoapService = arenaSoapService,
                arenaSakRepository = arenaSakRepository,
            )
            ArenaTiltakService(
                rapidsConnection = this,
                arenaOrdsClient = arenaOrdsClient,
            )
        }

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
