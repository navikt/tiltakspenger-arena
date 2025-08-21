package no.nav.tiltakspenger.arena.routes

import com.nimbusds.jwt.SignedJWT
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.mockk
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.tiltakspenger.arena.configureTestApplication
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.VedtakDetaljerService
import no.nav.tiltakspenger.libs.texas.IdentityProvider
import no.nav.tiltakspenger.libs.texas.client.TexasHttpClient
import no.nav.tiltakspenger.libs.texas.client.TexasIntrospectionResponse
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class TiltakspengerRoutesAuthTest {
    private val texasClient = mockk<TexasHttpClient>()

    companion object {
        private val mockOAuth2Server = MockOAuth2Server().also {
            it.start(8080)
        }

        @AfterAll
        @JvmStatic
        fun teardown() = mockOAuth2Server.shutdown()
    }

    private fun token(
        issuerId: String = "azure",
        audience: String = "tiltakspenger-arena",
        expiry: Long = 3600L,
    ): SignedJWT = mockOAuth2Server
        .issueToken(
            issuerId = issuerId,
            audience = audience,
            expiry = expiry,
        )

    private val gyldigAzureToken: SignedJWT = token()

    private val utgåttAzureToken: SignedJWT = token(expiry = -60L)

    private val tokenMedFeilIssuer: SignedJWT = token(issuerId = "feilIssuer")

    private val tokenMedFeilAudience: SignedJWT = token(audience = "feilAudience")

    private val vedtakRequestBody = """
        {
            "ident": "12345678910",
            "fom": "2021-01-01",
            "tom": "2021-01-31"
        }
    """.trimIndent()

    @BeforeEach
    fun setup() {
        clearMocks(texasClient)
    }

    @Test
    fun `post med ugyldig token skal gi 401`() {
        coEvery { texasClient.introspectToken(any(), IdentityProvider.AZUREAD) } returns TexasIntrospectionResponse(
            active = false,
            error = "Ikke gyldig token",
            groups = null,
            roles = null,
        )
        testApplication {
            configureTestApplication(texasClient = texasClient)
            val response = client.post("/azure/tiltakspenger/vedtaksperioder") {
                header("Authorization", "Bearer tulletoken")
                header("Content-Type", "application/json")
                setBody(vedtakRequestBody)
            }
            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }
    }

    @Test
    fun `post med gyldig token skal gi 200`() {
        coEvery { texasClient.introspectToken(any(), IdentityProvider.AZUREAD) } returns TexasIntrospectionResponse(
            active = true,
            error = null,
            groups = null,
            roles = null,
        )
        val vedtakDetaljerServiceMock = mockk<VedtakDetaljerService>(relaxed = true)

        testApplication {
            configureTestApplication(
                texasClient = texasClient,
                vedtakDetaljerService = vedtakDetaljerServiceMock,
            )
            val response = client.post("/azure/tiltakspenger/vedtaksperioder") {
                header("Authorization", "Bearer ${gyldigAzureToken.serialize()}")
                header("Content-Type", "application/json")
                setBody(vedtakRequestBody)
            }
            assertEquals(HttpStatusCode.OK, response.status)
        }
    }

    @Test
    fun `post med utgått token skal gi 401`() {
        coEvery { texasClient.introspectToken(any(), IdentityProvider.AZUREAD) } returns TexasIntrospectionResponse(
            active = false,
            error = "Utløpt token",
            groups = null,
            roles = null,
        )
        testApplication {
            configureTestApplication(texasClient = texasClient)
            val response = client.post("/azure/tiltakspenger/vedtaksperioder") {
                header("Authorization", "Bearer ${utgåttAzureToken.serialize()}")
                header("Content-Type", "application/json")
                setBody(vedtakRequestBody)
            }
            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }
    }

    @Test
    fun `post med feil issuer token skal gi 401`() {
        coEvery { texasClient.introspectToken(any(), IdentityProvider.AZUREAD) } returns TexasIntrospectionResponse(
            active = false,
            error = "Feil issuer",
            groups = null,
            roles = null,
        )
        testApplication {
            configureTestApplication(texasClient = texasClient)
            val response = client.post("/azure/tiltakspenger/vedtaksperioder") {
                header("Authorization", "Bearer ${tokenMedFeilIssuer.serialize()}")
                header("Content-Type", "application/json")
                setBody(vedtakRequestBody)
            }
            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }
    }

    @Test
    fun `post med feil audience token skal gi 401`() {
        coEvery { texasClient.introspectToken(any(), IdentityProvider.AZUREAD) } returns TexasIntrospectionResponse(
            active = false,
            error = "Feil audience",
            groups = null,
            roles = null,
        )
        testApplication {
            configureTestApplication(texasClient = texasClient)
            val response = client.post("/azure/tiltakspenger/vedtaksperioder") {
                header("Authorization", "Bearer ${tokenMedFeilAudience.serialize()}")
                header("Content-Type", "application/json")
                setBody(vedtakRequestBody)
            }
            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }
    }
}
