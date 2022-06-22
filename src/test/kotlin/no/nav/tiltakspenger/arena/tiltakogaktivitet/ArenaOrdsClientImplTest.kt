package no.nav.tiltakspenger.arena.tiltakogaktivitet

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.tiltakspenger.arena.Configuration.ArenaOrdsConfig
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsException.OtherException
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsException.PersonNotFoundException
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsException.UnauthorizedException
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class ArenaOrdsClientImplTest {

    private fun mockConfig() = ArenaOrdsConfig(
        arenaOrdsUrl = "",
        arenaOrdsClientId = "",
        arenaOrdsClientSecret = ""
    )

    private fun mockClient(response: String): HttpClient {
        val mockEngine = MockEngine {
            respond(
                status = HttpStatusCode.OK,
                content = response,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Xml.toString())
            )
        }
        return HttpClient(mockEngine) { setupHttpClient() }
    }

    private fun mockClientError(statusCode: HttpStatusCode): HttpClient {
        val mockEngine = MockEngine {
            respondError(
                status = statusCode,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Xml.toString())
            )
        }
        return HttpClient(mockEngine) { setupHttpClient() }
    }

    @Test
    @Disabled
    fun `should be able to serialize non-errors`() {
        val mockTokenProvider = mockk<ArenaOrdsTokenProviderClient>()
        coEvery { mockTokenProvider.token() } returns "token"

        val response = this::class.java.classLoader.getResource("aktiviteterTestResponse.xml").readText()
        val arenaOrdsService = ArenaOrdsClientImpl(mockConfig(), mockTokenProvider, mockClient(response))

        @Suppress("UnusedPrivateMember")
        val aktiviteter = runBlocking {
            arenaOrdsService.hentArenaAktiviteter("01019012345")
        }
        // add asserts when we have a proper response
    }

    @Test
    fun `should be able to handle 500`() {
        val mockTokenProvider = mockk<ArenaOrdsTokenProviderClient>()
        coEvery { mockTokenProvider.token() } returns "token"

        val arenaOrdsService = ArenaOrdsClientImpl(
            mockConfig(),
            mockTokenProvider,
            mockClientError(HttpStatusCode.InternalServerError)
        )

        assertThrows(OtherException::class.java) {
            runBlocking {
                arenaOrdsService.hentArenaAktiviteter("01019012345")
            }
        }
    }

    @Test
    fun `should be able to handle 204`() {
        val mockTokenProvider = mockk<ArenaOrdsTokenProviderClient>()
        coEvery { mockTokenProvider.token() } returns "token"

        val arenaOrdsService = ArenaOrdsClientImpl(
            mockConfig(),
            mockTokenProvider,
            mockClientError(HttpStatusCode.NoContent)
        )

        assertThrows(PersonNotFoundException::class.java) {
            runBlocking {
                arenaOrdsService.hentArenaAktiviteter("01019012345")
            }
        }
    }

    @Test
    fun `should be able to handle 401`() {
        val mockTokenProvider = mockk<ArenaOrdsTokenProviderClient>()
        coEvery { mockTokenProvider.token() } returns "token"

        val arenaOrdsService = ArenaOrdsClientImpl(
            mockConfig(),
            mockTokenProvider,
            mockClientError(HttpStatusCode.Unauthorized)
        )

        assertThrows(UnauthorizedException::class.java) {
            runBlocking {
                arenaOrdsService.hentArenaAktiviteter("01019012345")
            }
        }
    }

    @Test
    fun `should be able to handle 404`() {
        val mockTokenProvider = mockk<ArenaOrdsTokenProviderClient>()
        coEvery { mockTokenProvider.token() } returns "token"

        val arenaOrdsService = ArenaOrdsClientImpl(
            mockConfig(),
            mockTokenProvider,
            mockClientError(HttpStatusCode.NotFound)
        )

        assertThrows(ClientRequestException::class.java) {
            runBlocking {
                arenaOrdsService.hentArenaAktiviteter("01019012345")
            }
        }
    }
}
