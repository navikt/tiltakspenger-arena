package no.nav.tiltakspenger.arena

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tiltakspenger.arena.db.flywayMigrate
import org.slf4j.bridge.SLF4JBridgeHandler

fun main() {
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

    // TODO jah: Må legge til oracle-db i docker-compose.yml. Gjetter på man kan fjerne zookeeper+kafka, men må synkronisere mock-oauth2-server med docker-compose.yml i tiltakspenger root.
    flywayMigrate()
    start()
}
