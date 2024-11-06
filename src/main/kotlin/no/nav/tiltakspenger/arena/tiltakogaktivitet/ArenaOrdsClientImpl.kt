package no.nav.tiltakspenger.arena.tiltakogaktivitet

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import mu.KotlinLogging
import no.nav.tiltakspenger.arena.Configuration
import no.nav.tiltakspenger.arena.felles.JacksonXmlConverter
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsException.OtherException
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsException.PersonNotFoundException
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsException.UnauthorizedException

private val LOG = KotlinLogging.logger {}
private val SECURELOG = KotlinLogging.logger("tjenestekall")

// Lenke til dokumentasjon i confluence
// Aktiviteter: https://confluence.adeo.no/pages/viewpage.action?pageId=470748287
//
class ArenaOrdsClientImpl(
    private val arenaOrdsConfig: Configuration.ArenaOrdsConfig,
    private val arenaOrdsTokenProvider: ArenaOrdsTokenProviderClient,
    private val client: HttpClient = cioHttpClient(),
) : ArenaOrdsClient {

    override suspend fun hentArenaAktiviteter(fnr: String): ArenaAktiviteterDTO {
        val url = arenaOrdsConfig.arenaOrdsUrl + "/arena/api/v1/person/oppfoelging/aktiviteter"
        val response: ArenaAktiviteterDTO = client.get(urlString = url) {
            bearerAuth(arenaOrdsTokenProvider.token())
            header("fnr", fnr)
        }.body()
        return response
    }
}

private fun cioHttpClient() = HttpClient(CIO) { setupHttpClient() }

fun HttpClientConfig<*>.setupHttpClient() {
    install(ContentNegotiation) {
        register(ContentType.Text.Xml, JacksonXmlConverter())
    }
    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                LOG.info("HttpClient detaljer logget til securelog")
                SECURELOG.info(message)
            }
        }
    }
    expectSuccess = true
    // https://confluence.adeo.no/pages/viewpage.action?pageId=470748287
    // Man kan få 204, 401 og 500
    // Det er strengt tatt ikke nødvendig å styre med custom exceptions her, med mulig unntak av 204.
    // Men det var interessant å lære litt om Ktor Client.
    HttpResponseValidator {
        validateResponse { response ->
            val statusCode = response.status
            if (statusCode == HttpStatusCode.NoContent) {
                val text = response.bodyAsText()
                LOG.warn { "Bruker (person) finnes ikke i Arena" }
                SECURELOG.warn { "Bruker (person) finnes ikke i Arena: $text" }
                throw PersonNotFoundException("Bruker (person) finnes ikke i Arena: $text")
            }
        }
        handleResponseExceptionWithRequest { exception, _ ->
            when (exception) {
                is ClientRequestException -> {
                    val exceptionResponse = exception.response
                    if (exceptionResponse.status == HttpStatusCode.Unauthorized) {
                        val exceptionResponseText = exceptionResponse.bodyAsText()
                        throw UnauthorizedException(exceptionResponseText, exception)
                    }
                }

                is ServerResponseException -> {
                    val exceptionResponse = exception.response
                    if (exceptionResponse.status == HttpStatusCode.InternalServerError) {
                        val exceptionResponseText = exceptionResponse.bodyAsText()
                        throw OtherException(exceptionResponseText, exception)
                    }
                }

                else -> return@handleResponseExceptionWithRequest
            }
        }
    }
}
