package no.nav.tiltakspenger.arena.tiltakogaktivitet

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
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

private object SecurelogWrapper : Logger {
    override fun log(message: String) {
        LOG.info("HttpClient detaljer logget til securelog")
        SECURELOG.info(message)
    }
}

class ArenaOrdsClientImpl(
    private val arenaOrdsConfig: Configuration.ArenaOrdsConfig,
    private val arenaOrdsTokenProvider: ArenaOrdsTokenProviderClient,
    private val client: HttpClient = cioHttpClient(),
) : ArenaOrdsClient {

    // fun checkHealth(): HealthCheckResult {
    //     return HealthCheckUtils.pingUrl(UrlUtils.joinPaths(arenaOrdsUrl, "arena/api/v1/test/ping"), client)
    // }

    override suspend fun hentArenaOppfolgingsstatus(fnr: String): ArenaOppfølgingsstatusDTO {
        val url = arenaOrdsConfig.arenaOrdsUrl + "/arena/api/v1/person/oppfoelging/oppfoelgingsstatus?p_fnr=$fnr"
        val response: ArenaOppfølgingsstatusDTO = client.get(urlString = url) {
            bearerAuth(arenaOrdsTokenProvider.token())
        }.body()
        return response
    }

    override suspend fun hentArenaOppfolgingssak(fnr: String): ArenaOppfølgingssakDTO {
        val url = arenaOrdsConfig.arenaOrdsUrl + "/arena/api/v1/person/oppfoelging/oppfoelgingssak?p_fnr=$fnr"
        val response: ArenaOppfølgingssakDTO = client.get(urlString = url) {
            bearerAuth(arenaOrdsTokenProvider.token())
        }.body()
        SECURELOG.info { response }
        return response
    }

    override suspend fun hentArenaAktiviteter(fnr: String): ArenaAktiviteterDTO {
        val url = arenaOrdsConfig.arenaOrdsUrl + "/arena/api/v1/person/oppfoelging/aktiviteter"
        LOG.info { url }
        val response: ArenaAktiviteterDTO = client.get(urlString = url) {
            bearerAuth(arenaOrdsTokenProvider.token())
            header("fnr", fnr)
        }.body()
        SECURELOG.info { response }
        return response
    }
}

private fun cioHttpClient() = HttpClient(CIO) { setupHttpClient() }

@Suppress("ThrowsCount")
fun HttpClientConfig<*>.setupHttpClient() {
    this.install(ContentNegotiation) {
        register(ContentType.Text.Xml, JacksonXmlConverter())
    }
    this.install(Logging) {
        logger = SecurelogWrapper
        level = LogLevel.NONE
    }
    this.expectSuccess = true
    // https://confluence.adeo.no/pages/viewpage.action?pageId=470748287
    // Man kan få 204, 401 og 500
    // Det er strengt tatt ikke nødvendig å styre med custom exceptions her, med mulig unntak av 204.
    // Men det var interessant å lære litt om Ktor Client.

    this.HttpResponseValidator {
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
