package no.nav.tiltakspenger.arena.auth.texas.client

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import no.nav.tiltakspenger.arena.auth.texas.log
import no.nav.tiltakspenger.arena.httpClientApache
import no.nav.tiltakspenger.libs.logging.Sikkerlogg

class TexasClient(
    private val introspectionUrl: String,
    private val httpClient: HttpClient = httpClientApache(),
) {
    suspend fun introspectToken(token: String, identityProvider: String): TexasIntrospectionResponse {
        val texasIntrospectionRequest = TexasIntrospectionRequest(
            identityProvider = identityProvider,
            token = token,
        )
        val response =
            httpClient.post(introspectionUrl) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                setBody(texasIntrospectionRequest)
            }
        if (!response.status.isSuccess()) {
            log.error { "Kall for autentisering mot Texas feilet med responskode ${response.status.value}, se sikker logg for detaljer" }
            val feilmelding = response.bodyAsText()
            Sikkerlogg.error { "Kall for autentisering mot Texas feilet, melding: $feilmelding" }
            throw RuntimeException("Kall for autentisering mot Texas feilet")
        }
        return response.body<TexasIntrospectionResponse>()
    }
}

data class TexasIntrospectionRequest(
    @JsonProperty("identity_provider") val identityProvider: String,
    val token: String,
)

data class TexasIntrospectionResponse(
    val active: Boolean,
    @JsonInclude(JsonInclude.Include.NON_NULL) val error: String?,
    @JsonAnySetter @get:JsonAnyGetter val other: Map<String, Any?> = mutableMapOf(),
)
