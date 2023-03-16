package no.nav.tiltakspenger.arena

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Routing
import no.nav.security.token.support.v2.RequiredClaims
import no.nav.security.token.support.v2.tokenValidationSupport
import no.nav.tiltakspenger.arena.routes.healthRoutes
import no.nav.tiltakspenger.arena.routes.tiltakRoutes
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsClient

fun Application.tiltakApi(arenaOrdsClient: ArenaOrdsClient) {
    val config = environment.config
    val issuerName = "tokendings"
    install(Authentication) {
        val requiredClaimsMap = arrayOf("acr=Level4")
        tokenValidationSupport(
            name = issuerName,
            config = config,
            requiredClaims = RequiredClaims(
                issuer = issuerName,
                claimMap = requiredClaimsMap,
                combineWithOr = false,
            ),
        )
    }
    install(Routing) {
        authenticate(issuerName) {
            tiltakRoutes(arenaOrdsClient = arenaOrdsClient)
        }
        healthRoutes()
    }
}
