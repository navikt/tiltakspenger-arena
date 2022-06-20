package no.nav.tiltakspenger.arena

import mu.KotlinLogging
import no.nav.helse.rapids_rivers.RapidApplication
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsClientImpl
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsTokenProviderClient
import no.nav.tiltakspenger.arena.ytelser.ArenaClientConfiguration
import no.nav.tiltakspenger.arena.ytelser.ArenaSoapService

private val LOG = KotlinLogging.logger {}

fun main() {
    Thread.setDefaultUncaughtExceptionHandler { _, e -> LOG.error(e) { e.message } }
    val arenaSoapService = ArenaSoapService(ArenaClientConfiguration().ytelseskontraktV3())

    val tokenProviderClient = ArenaOrdsTokenProviderClient(Configuration.ArenaOrdsConfig())
    val arenaOrdsService = ArenaOrdsClientImpl(
        arenaOrdsConfig = Configuration.ArenaOrdsConfig(),
        arenaOrdsTokenProvider = tokenProviderClient
    )

    RapidApplication.create(Configuration.rapidsAndRivers).apply {
        ArenaYtelserService(
            rapidsConnection = this,
            arenaSoapService = arenaSoapService,
        )
        ArenaTiltakService(
            rapidsConnection = this,
            arenaOrdsService = arenaOrdsService
        )
        
        register(object : RapidsConnection.StatusListener {
            override fun onStartup(rapidsConnection: RapidsConnection) {
                LOG.info { "Starting tiltakspenger-arena" }
            }

            override fun onShutdown(rapidsConnection: RapidsConnection) {
                LOG.info { "Stopping tiltakspenger-arena" }
                super.onShutdown(rapidsConnection)
            }
        })
    }.start()
}
