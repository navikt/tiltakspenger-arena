package no.nav.tiltakspenger.arena

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import no.nav.security.token.support.v2.RequiredClaims
import no.nav.security.token.support.v2.tokenValidationSupport
import no.nav.tiltakspenger.arena.routes.tiltakAzureRoutes
import no.nav.tiltakspenger.arena.routes.tiltakRoutes
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsClient

fun Application.tiltakApi(arenaOrdsClient: ArenaOrdsClient, config: ApplicationConfig) {
    val issuerName = "tokendings"
    val issuerAzure = "azure"
    install(Authentication) {
        val requiredClaimsMap = arrayOf("acr=Level4")
//        val requiredClaimsMapAzure = arrayOf("aud=Level4")
        tokenValidationSupport(
            name = issuerName,
            config = config,
            requiredClaims = RequiredClaims(
                issuer = issuerName,
                claimMap = requiredClaimsMap,
                combineWithOr = false,
            ),
        )
        tokenValidationSupport(
            name = issuerAzure,
            config = config,
            requiredClaims = RequiredClaims(
                issuer = issuerAzure,
                claimMap = arrayOf(),
                combineWithOr = false,
            ),
        )
    }
    install(ContentNegotiation) {
        jackson {
            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            registerModule(JavaTimeModule())
            registerModule(KotlinModule.Builder().build())
        }
    }
    routing {
        authenticate(issuerName) {
            tiltakRoutes(arenaOrdsClient = arenaOrdsClient)
        }
        authenticate(issuerAzure) {
            tiltakAzureRoutes(arenaOrdsClient = arenaOrdsClient)
        }
        // tiltakUtenRoutes(arenaOrdsClient = arenaOrdsClient)
    }
}
