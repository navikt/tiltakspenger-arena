package no.nav.tiltakspenger.arena.tiltakogaktivitet

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.basicAuth
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import io.ktor.http.isSuccess
import no.nav.tiltakspenger.arena.Configuration
import no.nav.tiltakspenger.arena.httpClientApache
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

private val logger = KotlinLogging.logger {}

class ArenaOrdsTokenProviderClient(
    private val arenaOrdsConfig: Configuration.ArenaOrdsConfig,
    private val httpClient: HttpClient = httpClientApache(),
) {
    companion object {
        private const val MINIMUM_TIME_TO_EXPIRE_BEFORE_REFRESH: Long = 60
    }

    private var tokenCache: TokenCache? = null

    suspend fun token(): String {
        if (tokenIsSoonExpired()) {
            return refreshToken().accessToken
        }
        return tokenCache!!.ordsToken.accessToken
    }

    private suspend fun refreshToken(): OrdsToken {
        val response = httpClient.submitForm(
            url = arenaOrdsConfig.arenaOrdsUrl + "/arena/api/oauth/token",
            formParameters = Parameters.build {
                append("grant_type", "client_credentials")
            },
        ) {
            basicAuth(arenaOrdsConfig.arenaOrdsClientId, arenaOrdsConfig.arenaOrdsClientSecret)
            header(HttpHeaders.CacheControl, "no-cache")
        }
        if (!response.status.isSuccess()) {
            logger.error { "Kunne ikke hente token fra Arena-ords, responskode: ${response.status.value}" }
            throw RuntimeException("Kunne ikke hente token fra Arena-ords")
        }
        val ordsToken = response.body<OrdsToken>()
        tokenCache = TokenCache(ordsToken)
        return ordsToken
    }

    private fun tokenIsSoonExpired(): Boolean {
        return tokenCache == null || timeToRefresh().isBefore(LocalDateTime.now())
    }

    private fun timeToRefresh(): LocalDateTime =
        tokenCache!!.time.plus(
            tokenCache!!.ordsToken.expiresIn - MINIMUM_TIME_TO_EXPIRE_BEFORE_REFRESH,
            ChronoUnit.SECONDS,
        )

    data class TokenCache(
        val ordsToken: OrdsToken,
        val time: LocalDateTime = LocalDateTime.now(),
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class OrdsToken(
        @JsonAlias("access_token")
        val accessToken: String,
        @JsonAlias("token_type")
        val tokenType: String?,
        @JsonAlias("expires_in")
        val expiresIn: Long = 0,
    )
}
