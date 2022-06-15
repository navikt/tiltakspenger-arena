package no.nav.tiltakspenger.arena.tiltakogaktivitet

import com.fasterxml.jackson.annotation.JsonAlias
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import no.nav.tiltakspenger.arena.Configuration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class ArenaOrdsTokenProviderClient(
    private val arenaOrdsUrl: String
) {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            jackson()
        }
    }

    companion object {
        private const val MINIMUM_TIME_TO_EXPIRE_BEFORE_REFRESH: Long = 60
    }

    private var tokenCache: TokenCache? = null

    suspend fun token(): String {
        if (tokenIsSoonExpired()) {
            refreshToken()
        }
        return tokenCache?.ordsToken?.accessToken!!
    }

    private suspend fun refreshToken() {
        val response: OrdsToken = client.submitForm(
            url = arenaOrdsUrl + "arena/api/oauth/token",
            formParameters = Parameters.build {
                append("grant_type", "client_credentials")
            }
        ) {
            basicAuth(
                Configuration.otherDefaultProperties.getOrDefault("ARENA_ORDS_CLIENT_ID", ""),
                Configuration.otherDefaultProperties.getOrDefault("ARENA_ORDS_CLIENT_ID", "")
            )
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
