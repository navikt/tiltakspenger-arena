package no.nav.tiltakspenger.arena

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.testing.ApplicationTestBuilder
import io.mockk.mockk
import no.nav.tiltakspenger.arena.auth.texas.client.TexasClient
import no.nav.tiltakspenger.arena.routes.tiltakAzureRoutes
import no.nav.tiltakspenger.arena.routes.tiltakRoutes
import no.nav.tiltakspenger.arena.routes.tiltakspengerRoutes
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.RettighetDetaljerService
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.VedtakDetaljerService
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsClient

fun ApplicationTestBuilder.configureTestApplication(
    texasClient: TexasClient = mockk(),
    arenaOrdsClient: ArenaOrdsClient = mockk(),
    vedtakDetaljerService: VedtakDetaljerService = mockk(),
    rettighetDetaljerService: RettighetDetaljerService = mockk(),
) {
    application {
        install(ContentNegotiation) {
            jackson {
                configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                registerModule(JavaTimeModule())
                registerModule(KotlinModule.Builder().build())
            }
        }
        routing {
            tiltakRoutes(
                texasClient = texasClient,
                arenaOrdsClient = arenaOrdsClient,
            )
            tiltakAzureRoutes(
                texasClient = texasClient,
                arenaOrdsClient = arenaOrdsClient,
            )
            tiltakspengerRoutes(
                texasClient = texasClient,
                vedtakDetaljerService = vedtakDetaljerService,
                rettighetDetaljerService = rettighetDetaljerService,
            )
        }
    }
}
