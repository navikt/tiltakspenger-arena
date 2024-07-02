package no.nav.tiltakspenger.arena

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.testing.ApplicationTestBuilder
import io.mockk.mockk
import no.nav.security.token.support.v2.RequiredClaims
import no.nav.security.token.support.v2.tokenValidationSupport
import no.nav.tiltakspenger.arena.routes.tiltakAzureRoutes
import no.nav.tiltakspenger.arena.routes.tiltakRoutes
import no.nav.tiltakspenger.arena.routes.tiltakspengerRoutes
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.RettighetDetaljerService
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.VedtakDetaljerService
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsClient

fun ApplicationTestBuilder.configureTestApplication(
    arenaOrdsClient: ArenaOrdsClient = mockk(),
    vedtakDetaljerService: VedtakDetaljerService = mockk(),
    rettighetDetaljerService: RettighetDetaljerService = mockk(),
) {
    application {
        val config = ApplicationConfig("applicationTest.conf")
        install(Authentication) {
            val requiredClaimsMap = arrayOf("acr=Level4")
            tokenValidationSupport(
                name = "tokendings",
                config = config,
                requiredClaims = RequiredClaims(
                    issuer = "tokendings",
                    claimMap = requiredClaimsMap,
                    combineWithOr = false,
                ),
            )
            tokenValidationSupport(
                name = "azure",
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
            authenticate("tokendings") {
                tiltakRoutes(arenaOrdsClient = arenaOrdsClient)
            }
            authenticate("azure") {
                tiltakAzureRoutes(arenaOrdsClient = arenaOrdsClient)
                tiltakspengerRoutes(vedtakDetaljerService, rettighetDetaljerService)
            }
        }
    }
}
