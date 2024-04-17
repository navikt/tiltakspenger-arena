package no.nav.tiltakspenger.arena.clients.skjerming

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.jackson.JacksonConverter
import kotlinx.coroutines.runBlocking
import java.net.URL
import java.time.Duration

class SkjermedePersoner(
    private val tokenSupplier: AzureTokenProvider,
    private val baseUrl: URL,
    private val scope: String,
    private val ktorHttpClient: HttpClient = HttpClient {
        install(ContentNegotiation) {
            register(ContentType.Application.Json, JacksonConverter(objectMapper))
        }
        install(HttpTimeout) {
            connectTimeoutMillis = Duration.ofSeconds(60).toMillis()
            requestTimeoutMillis = Duration.ofSeconds(60).toMillis()
            socketTimeoutMillis = Duration.ofSeconds(60).toMillis()
        }
    },
) {

    internal fun erSkjermetPerson(fødselsnummer: String, behovId: String): Boolean =
        runBlocking {
            val httpResponse = ktorHttpClient.preparePost("$baseUrl/skjermet") {
                header("Authorization", "Bearer ${tokenSupplier.bearerToken(scope).token}")
                header("Nav-Call-Id", behovId)
                accept(ContentType.Application.Json)
                contentType(ContentType.Application.Json)
                setBody(SkjermetDataRequestDTO(fødselsnummer))
            }.execute()
            when (httpResponse.status.value) {
                200 -> {
                    val response = httpResponse.call.response.body<Boolean>()
                    return@runBlocking response
                }

                else -> {
                    throw RuntimeException("error (responseCode=${httpResponse.status.value}) from Skjerming")
                }
            }
        }
}

private data class SkjermetDataRequestDTO(
    val personident: String,
)
