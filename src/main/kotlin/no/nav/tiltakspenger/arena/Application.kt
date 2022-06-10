package no.nav.tiltakspenger.arena

import mu.KotlinLogging
import no.nav.helse.rapids_rivers.RapidApplication
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.tiltakspenger.arena.ytelser.ArenaClientConfiguration
import no.nav.tiltakspenger.arena.ytelser.ArenaSoapService

private val LOG = KotlinLogging.logger {}

fun main() {
    val arenaSoapService = ArenaSoapService(ArenaClientConfiguration().ytelseskontraktV3())

    RapidApplication.create(Configuration.rapidsAndRivers).apply {
        TestService(this)
        ArenaYtelserService(this, arenaSoapService)

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
