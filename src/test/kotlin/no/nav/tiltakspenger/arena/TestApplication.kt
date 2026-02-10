package no.nav.tiltakspenger.arena

import io.ktor.http.ContentType
import io.ktor.serialization.jackson3.JacksonConverter
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.testing.ApplicationTestBuilder
import io.mockk.mockk
import no.nav.tiltakspenger.arena.routes.tiltakspengerRoutes
import no.nav.tiltakspenger.arena.service.meldekort.MeldekortService
import no.nav.tiltakspenger.arena.service.utbetalingshistorikk.UtbetalingshistorikkService
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.RettighetDetaljerService
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.VedtakDetaljerService
import no.nav.tiltakspenger.libs.json.objectMapper
import no.nav.tiltakspenger.libs.texas.client.TexasHttpClient

fun ApplicationTestBuilder.configureTestApplication(
    texasClient: TexasHttpClient = mockk(),
    vedtakDetaljerService: VedtakDetaljerService = mockk(),
    rettighetDetaljerService: RettighetDetaljerService = mockk(),
    meldekortService: MeldekortService = mockk(),
    utbetalingshistorikkService: UtbetalingshistorikkService = mockk(),
) {
    application {
        install(ContentNegotiation) {
            register(ContentType.Application.Json, JacksonConverter(objectMapper))
        }
        setupAuthentication(texasClient)
        routing {
            tiltakspengerRoutes(
                vedtakDetaljerService = vedtakDetaljerService,
                rettighetDetaljerService = rettighetDetaljerService,
                meldekortService = meldekortService,
                utbetalingshistorikkService = utbetalingshistorikkService,
            )
        }
    }
}
