package no.nav.tiltakspenger.arena.tiltakogaktivitet

import com.fasterxml.jackson.annotation.JsonAlias
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.basicAuth
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import io.ktor.serialization.jackson.jackson
import no.nav.tiltakspenger.arena.Configuration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class ArenaOrdsTokenProviderClient(private val arenaOrdsConfig: Configuration.ArenaOrdsConfig) {
    companion object {
        private const val MINIMUM_TIME_TO_EXPIRE_BEFORE_REFRESH: Long = 60
    }

    private var tokenCache: TokenCache? = null

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            jackson()
        }
    }

    suspend fun token(): String {
        if (tokenIsSoonExpired()) {
            refreshToken()
        }
        return tokenCache?.ordsToken?.accessToken!!
    }

    private suspend fun refreshToken() {
        val response: OrdsToken = client.submitForm(
            url = arenaOrdsConfig.arenaOrdsUrl + "/arena/api/oauth/token",
            formParameters = Parameters.build {
                append("grant_type", "client_credentials")
            }
        ) {
            basicAuth(arenaOrdsConfig.arenaOrdsClientId, arenaOrdsConfig.arenaOrdsClientSecret)
            header(HttpHeaders.CacheControl, "no-cache")
        }.body()
        tokenCache = TokenCache(response)
    }

    private fun tokenIsSoonExpired(): Boolean {
        return tokenCache == null || timeToRefresh().isBefore(LocalDateTime.now())
    }

    private fun timeToRefresh(): LocalDateTime =
        tokenCache!!.time.plus(
            tokenCache!!.ordsToken.expiresIn - MINIMUM_TIME_TO_EXPIRE_BEFORE_REFRESH,
            ChronoUnit.SECONDS
        )

    data class TokenCache(
        val ordsToken: OrdsToken,
        val time: LocalDateTime = LocalDateTime.now()
    )

    data class OrdsToken(
        @JsonAlias("access_token")
        val accessToken: String?,
        @JsonAlias("token_type")
        val tokenType: String?,
        @JsonAlias("expires_in")
        val expiresIn: Long = 0
    )
}
