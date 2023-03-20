package no.nav.tiltakspenger.arena

import io.ktor.server.application.Application
import mu.KotlinLogging

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    System.setProperty("logback.configurationFile", "egenLogback.xml")

    val log = KotlinLogging.logger {}
    val securelog = KotlinLogging.logger("tjenestekall")
    log.info { "starting server" }
    Thread.setDefaultUncaughtExceptionHandler { _, e ->
        log.error { "Uncaught exception logget i securelog" }
        securelog.error(e) { e.message }
    }

    val config = environment.config
    val applicationBuilder = ApplicationBuilder(config = config)
    applicationBuilder.start()
}
