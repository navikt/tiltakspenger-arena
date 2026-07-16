package no.nav.tiltakspenger.arena.routes

import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.shouldBe
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.mockk.coEvery
import io.mockk.mockk
import no.nav.tiltakspenger.arena.configureTestApplication
import no.nav.tiltakspenger.arena.db.OracleTestbase
import no.nav.tiltakspenger.arena.repository.sak.SakRepository
import no.nav.tiltakspenger.arena.service.meldekort.MeldekortService
import no.nav.tiltakspenger.arena.service.utbetalingshistorikk.UtbetalingshistorikkService
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.RettighetDetaljerServiceImpl
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.VedtakDetaljerServiceImpl
import no.nav.tiltakspenger.libs.texas.IdentityProvider
import no.nav.tiltakspenger.libs.texas.client.TexasHttpClient
import no.nav.tiltakspenger.libs.texas.client.TexasIntrospectionResponse

/** Mock av [TexasHttpClient] som godkjenner alle AZUREAD-tokens (`active=true`). */
fun texasClientSomGodkjenner(): TexasHttpClient = mockk<TexasHttpClient>().also {
    coEvery { it.introspectToken(any(), IdentityProvider.AZUREAD) } returns TexasIntrospectionResponse(
        active = true,
        error = null,
        groups = null,
        roles = null,
    )
}

/**
 * Kjører en full-vertikal route-test: HTTP → prod [no.nav.tiltakspenger.arena.tiltakApi] → ekte
 * service → ekte repo → Oracle-testcontainer. Krever at [OracleTestbase.start] er kalt (typisk i
 * `@BeforeAll`); den kalles også her for å være trygg. Texas godkjenner alle AZUREAD-tokens.
 */
fun medArenaRouteTest(test: suspend ApplicationTestBuilder.() -> Unit) {
    OracleTestbase.start()
    testApplication {
        val vedtakDetaljerService = VedtakDetaljerServiceImpl(SakRepository())
        configureTestApplication(
            texasClient = texasClientSomGodkjenner(),
            vedtakDetaljerService = vedtakDetaljerService,
            rettighetDetaljerService = RettighetDetaljerServiceImpl(vedtakDetaljerService),
            meldekortService = MeldekortService(),
            utbetalingshistorikkService = UtbetalingshistorikkService(),
        )
        test()
    }
}

/** POST med gyldig AZUREAD-token (godkjennes av [texasClientSomGodkjenner]) og JSON-body. */
suspend fun ApplicationTestBuilder.postAutentisert(
    uri: String,
    body: String,
    token: String = "gyldig-token",
): HttpResponse = client.post(uri) {
    header(HttpHeaders.Authorization, "Bearer $token")
    header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
    setBody(body)
}

/** Bygger en `VedtakRequest`-body som ren tekst (ikke via DTO). */
fun vedtakRequestBody(ident: String, fom: String = "2000-01-01", tom: String = "2099-12-31"): String =
    """{ "ident": "$ident", "fom": "$fom", "tom": "$tom" }"""

/**
 * Asserter at responsen er 200 med nøyaktig [forventetJson]. [shouldEqualJson] sammenligner hele
 * JSON-strukturen (rekkefølge-uavhengig, men streng på nøkler): omdøpte, fjernede eller nye felt i
 * respons-DTO-ene brekker testen. Vi går bevisst utenom DTO-deserialisering, slik at en
 * DTO-refaktorering fanges som en kontraktsendring.
 */
suspend fun HttpResponse.skalHaOkMedJson(forventetJson: String) {
    status shouldBe HttpStatusCode.OK
    bodyAsText() shouldEqualJson forventetJson
}
