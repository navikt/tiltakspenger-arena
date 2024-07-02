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
import no.nav.tiltakspenger.arena.routes.tiltakspengerRoutes
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.RettighetDetaljerService
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.VedtakDetaljerService
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsClient

fun Application.tiltakApi(
    arenaOrdsClient: ArenaOrdsClient,
    vedtakDetaljerService: VedtakDetaljerService,
    rettighetDetaljerService: RettighetDetaljerService,
    config: ApplicationConfig,
) {
    val issuerTokenX = "tokendings"
    val issuerAzure = "azure"
    install(Authentication) {
        val requiredClaimsMap = arrayOf("acr=Level4")
        tokenValidationSupport(
            name = issuerTokenX,
            config = config,
            requiredClaims = RequiredClaims(
                issuer = issuerTokenX,
                claimMap = requiredClaimsMap,
                combineWithOr = false,
            ),
        )
        tokenValidationSupport(
            name = issuerAzure,
            config = config,
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
        authenticate(issuerTokenX) {
            tiltakRoutes(arenaOrdsClient = arenaOrdsClient)
        }
        authenticate(issuerAzure) {
            tiltakAzureRoutes(arenaOrdsClient = arenaOrdsClient)
            tiltakspengerRoutes(vedtakDetaljerService, rettighetDetaljerService)
        }
    }
}
