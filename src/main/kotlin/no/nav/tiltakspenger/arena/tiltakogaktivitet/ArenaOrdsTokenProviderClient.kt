package no.nav.tiltakspenger.arena.tiltakogaktivitet

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.basicAuth
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import io.ktor.serialization.jackson.jackson
import no.nav.tiltakspenger.arena.Configuration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging

private val LOG = KotlinLogging.logger {}
private val SECURELOG = KotlinLogging.logger("tjenestekall")

private object TokenProviderSecurelogWrapper : Logger {
    override fun log(message: String) {
        LOG.info("HttpClient detaljer logget til securelog")
        //Midlertidig:
        LOG.info(message)
        SECURELOG.info(message)
    }
}
class ArenaOrdsTokenProviderClient(private val arenaOrdsConfig: Configuration.ArenaOrdsConfig) {
    companion object {
        private const val MINIMUM_TIME_TO_EXPIRE_BEFORE_REFRESH: Long = 60
    }

    private var tokenCache: TokenCache? = null

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            jackson()
        }
        install(Logging) {
            logger = TokenProviderSecurelogWrapper
            level = LogLevel.BODY
        }
    }

    fun token(): String {
        if (tokenIsSoonExpired()) {
            return refreshToken().accessToken
        }
        return tokenCache!!.ordsToken.accessToken
    }

    private fun refreshToken(): OrdsToken {
        val response: OrdsToken =
            runBlocking {
                client.submitForm(
                    url = arenaOrdsConfig.arenaOrdsUrl + "/arena/api/oauth/token",
                    formParameters = Parameters.build {
                        append("grant_type", "client_credentials")
                    }
                ) {
                    basicAuth(arenaOrdsConfig.arenaOrdsClientId, arenaOrdsConfig.arenaOrdsClientSecret)
                    header(HttpHeaders.CacheControl, "no-cache")
                }.body()
            }
        tokenCache = TokenCache(response)
        return response
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class OrdsToken(
        @JsonAlias("access_token")
        val accessToken: String,
        @JsonAlias("token_type")
        val tokenType: String?,
        @JsonAlias("expires_in")
        val expiresIn: Long = 0
    )
}
