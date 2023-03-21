package no.nav.tiltakspenger.arena

import io.ktor.server.config.ApplicationConfig
import mu.KotlinLogging

fun main() {
    System.setProperty("logback.configurationFile", "egenLogback.xml")

    val log = KotlinLogging.logger {}
    val securelog = KotlinLogging.logger("tjenestekall")
    log.info { "starting server" }
    Thread.setDefaultUncaughtExceptionHandler { _, e ->
        log.error { "Uncaught exception logget i securelog" }
        securelog.error(e) { e.message }
    }

    val config = ApplicationConfig("application.conf")
    val applicationBuilder = ApplicationBuilder(config = config)
    applicationBuilder.start()
}
