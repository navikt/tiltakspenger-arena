package no.nav.tiltakspenger.arena

import mu.KotlinLogging
import no.nav.helse.rapids_rivers.RapidApplication
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsClientImpl
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsTokenProviderClient
import no.nav.tiltakspenger.arena.ytelser.ArenaClientConfiguration
import no.nav.tiltakspenger.arena.ytelser.ArenaSoapService

private val LOG = KotlinLogging.logger {}

internal class ApplicationBuilder : RapidsConnection.StatusListener {
    val arenaSoapService = ArenaSoapService(ArenaClientConfiguration().ytelseskontraktV3())
    val tokenProviderClient = ArenaOrdsTokenProviderClient(Configuration.ArenaOrdsConfig())
    val arenaOrdsClient = ArenaOrdsClientImpl(
        arenaOrdsConfig = Configuration.ArenaOrdsConfig(),
        arenaOrdsTokenProvider = tokenProviderClient,
    )

    val rapidsConnection: RapidsConnection = RapidApplication.Builder(
        RapidApplication.RapidApplicationConfig.fromEnv(Configuration.rapidsAndRivers),
    )
        .withKtorModule {
            tiltakApi(arenaOrdsClient = arenaOrdsClient)
        }
        .build()
        .apply {
            ArenaYtelserService(
                rapidsConnection = this,
                arenaSoapService = arenaSoapService,
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
