package no.nav.tiltakspenger.arena

import mu.KotlinLogging
import no.nav.helse.rapids_rivers.RapidApplication
import no.nav.helse.rapids_rivers.RapidsConnection

private val LOG = KotlinLogging.logger {}

fun main() {
    Thread.setDefaultUncaughtExceptionHandler { _, e -> LOG.error(e) { e.message } }
    RapidApplication.create(Configuration.rapidsAndRivers).apply {
        register(object : RapidsConnection.StatusListener {
            override fun onStartup(rapidsConnection: RapidsConnection) {
                LOG.info { "Starting tiltakspenger-arena" }
                while (true) {
                    // infinte loop to prevent from shutting down. It's a hack!
                }
            }

            override fun onShutdown(rapidsConnection: RapidsConnection) {
                LOG.info { "Stopping tiltakspenger-arena" }
                super.onShutdown(rapidsConnection)
            }
        })
    }.start()
}
