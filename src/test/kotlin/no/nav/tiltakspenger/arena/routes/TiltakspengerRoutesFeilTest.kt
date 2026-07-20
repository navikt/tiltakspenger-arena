package no.nav.tiltakspenger.arena.routes

import io.kotest.matchers.shouldBe
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import io.mockk.every
import io.mockk.mockk
import no.nav.tiltakspenger.arena.configureTestApplication
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.VedtakDetaljerService
import org.junit.jupiter.api.Test

/**
 * Dekker 500-stien i `medFeilhåndtering` i TiltakspengerRoutes.
 * Feilhåndteringen er felles for alle handlerne, så den dekkes ett sted (som auth-avvisning i [TiltakspengerRoutesAuthTest]) gjennom ett endepunkt.
 * Trenger ikke Oracle: den ene feilen oppstår før servicen (ugyldig body), den andre i en mocket service.
 */
internal class TiltakspengerRoutesFeilTest {

    private val uri = "/azure/tiltakspenger/vedtaksperioder"

    @Test
    fun `ugyldig request-body gir 500 med feilmelding`() {
        testApplication {
            configureTestApplication(texasClient = texasClientSomGodkjenner())
            // Mangler påkrevd ident; receive kaster og felleshjelperen svarer 500 med exception-meldingen.
            val response = postAutentisert(uri, """{ "fom": "2023-01-01" }""")
            response.status shouldBe HttpStatusCode.InternalServerError
        }
    }

    @Test
    fun `exception uten message gir 500 med exception-navnet`() {
        val vedtakDetaljerService = mockk<VedtakDetaljerService>()
        every { vedtakDetaljerService.hentVedtakDetaljerPerioder(any(), any(), any()) } throws Exception()

        testApplication {
            configureTestApplication(
                texasClient = texasClientSomGodkjenner(),
                vedtakDetaljerService = vedtakDetaljerService,
            )
            val response = postAutentisert(uri, vedtakRequestBody("10000000000"))
            response.status shouldBe HttpStatusCode.InternalServerError
            // e.message er null, så responsen faller tilbake til e.toString().
            response.bodyAsText() shouldBe "java.lang.Exception"
        }
    }
}
