package no.nav.tiltakspenger.arena

import io.ktor.server.config.ApplicationConfig
import mu.KotlinLogging
import org.slf4j.bridge.SLF4JBridgeHandler

/**
 * TODO jah: Dersom man ønsker kjøre opp denne lokalt må man lage en LokalMain.kt som ligger i test scope. Se eksempel i tiltakspenger-saksbehandling-api
 */
fun main() {
    System.setProperty("logback.configurationFile", "egenLogback.xml")
    System.setProperty("oracle.jdbc.fanEnabled", "false")
    SLF4JBridgeHandler.removeHandlersForRootLogger()
    SLF4JBridgeHandler.install()

    val log = KotlinLogging.logger {}
    val securelog = KotlinLogging.logger("tjenestekall")
    log.info { "starting server" }
    Thread.setDefaultUncaughtExceptionHandler { _, e ->
        log.error { "Uncaught exception logget i securelog" }
        securelog.error(e) { e.message }
    }

    val config = ApplicationConfig("application.conf")
    start(config)
}
