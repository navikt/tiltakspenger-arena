package no.nav.tiltakspenger.arena

import TestService
import mu.KotlinLogging
import no.nav.helse.rapids_rivers.RapidApplication
import no.nav.helse.rapids_rivers.RapidsConnection

private val LOG = KotlinLogging.logger {}

fun main() {
    RapidApplication.create(Configuration.rapidsAndRivers).apply {
        TestService(this)
        // ArenaYtelserService(this, Configuration.arenaSoapService)

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
