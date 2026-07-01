package no.nav.tiltakspenger.arena

import io.github.oshai.kotlinlogging.KotlinLogging
import org.slf4j.bridge.SLF4JBridgeHandler

/**
 * Kjør opp lokalt via LokalMain.kt
 */
fun main() {
    System.setProperty("logback.configurationFile", "logback.xml")
    System.setProperty("oracle.jdbc.fanEnabled", "false")
    SLF4JBridgeHandler.removeHandlersForRootLogger()
    SLF4JBridgeHandler.install()

    val log = KotlinLogging.logger {}
    log.info { "starting server" }
    Thread.setDefaultUncaughtExceptionHandler { _, e ->
        log.error(e) { e.message }
    }

    start(log)
}
