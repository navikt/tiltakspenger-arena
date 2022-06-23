package no.nav.tiltakspenger.arena.tiltakogaktivitet

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.*
import io.ktor.http.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.tiltakspenger.arena.Configuration.ArenaOrdsConfig
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsException.OtherException
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsException.PersonNotFoundException
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsException.UnauthorizedException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.charset.Charset
import java.time.LocalDate

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
                headers = headersOf(HttpHeaders.ContentType, ContentType.Text.Xml.toString())
            )
        }
        return HttpClient(mockEngine) { setupHttpClient() }
    }

    private fun mockClientError(statusCode: HttpStatusCode): HttpClient {
        val mockEngine = MockEngine {
            respondError(
                status = statusCode,
                headers = headersOf(
                    HttpHeaders.ContentType, ContentType.Text.Xml.toString(),
                )
            )
        }
        return HttpClient(mockEngine) { setupHttpClient() }
    }

    @Test
    fun `should be able to serialize non-errors`() {
        val mockTokenProvider = mockk<ArenaOrdsTokenProviderClient>()
        coEvery { mockTokenProvider.token() } returns "token"

        val response = this::class.java.classLoader.getResource("aktiviteterTestResponse.xml")
            .readText(Charset.forName("ISO-8859-1"))
        val arenaOrdsService = ArenaOrdsClientImpl(mockConfig(), mockTokenProvider, mockClient(response))

        val aktiviteter = runBlocking {
            arenaOrdsService.hentArenaAktiviteter("01019012345")
        }
        assertTrue(aktiviteter.response.tiltaksaktivitetListe.size == 1)
        val tiltak = aktiviteter.response.tiltaksaktivitetListe.first()
        assertEquals("Arbeidstrening", tiltak.tiltaksnavn)
        assertEquals("TA6698352", tiltak.aktivitetId)
        assertEquals("Arbeidstrening", tiltak.tiltakLokaltNavn)
        assertEquals("GRÅTASS BARNEHAGE AS", tiltak.arrangoer)
        assertEquals(
            ArenaAktiviteterDTO.Tiltaksaktivitet.DeltakelsesPeriode(
                LocalDate.of(2022, 5, 6),
                LocalDate.of(2022, 7, 8)
            ),
            tiltak.deltakelsePeriode
        )
        assertEquals("424242", tiltak.bedriftsnummer)
        assertEquals(100F, tiltak.deltakelseProsent)
        assertEquals("GJENN", tiltak.deltakerStatus.status)
        assertEquals("Gjennomføres", tiltak.deltakerStatus.termnavn)
        assertEquals(LocalDate.of(2022, 6, 1), tiltak.statusSistEndret)
        assertEquals("Trenger tiltaksplass", tiltak.begrunnelseInnsoeking)
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
