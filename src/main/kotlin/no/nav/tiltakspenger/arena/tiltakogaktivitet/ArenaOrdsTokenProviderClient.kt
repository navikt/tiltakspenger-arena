package no.nav.tiltakspenger.arena.tiltakogaktivitet

import com.fasterxml.jackson.annotation.JsonAlias
import no.nav.common.rest.client.RestClient
import no.nav.common.rest.client.RestUtils
import no.nav.common.utils.AuthUtils
import no.nav.common.utils.EnvironmentUtils
import no.nav.common.utils.UrlUtils
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.ws.rs.core.HttpHeaders.AUTHORIZATION
import javax.ws.rs.core.HttpHeaders.CACHE_CONTROL

class ArenaOrdsTokenProviderClient(
    private val arenaOrdsUrl: String,
    private val client: OkHttpClient = RestClient.baseClient()
) {
    private var tokenCache: TokenCache? = null

    val token: String
        get() {
            if (tokenIsSoonExpired()) {
                refreshToken()
            }
            return tokenCache?.ordsToken?.accessToken!!
        }

    private fun refreshToken() {
        val basicAuth = AuthUtils.basicCredentials(
            EnvironmentUtils.getRequiredProperty(ARENA_ORDS_CLIENT_ID_PROPERTY),
            EnvironmentUtils.getRequiredProperty(ARENA_ORDS_CLIENT_SECRET_PROPERTY)
        )
        val request: Request = Request.Builder()
            .url(UrlUtils.joinPaths(arenaOrdsUrl, "arena/api/oauth/token"))
            .header(CACHE_CONTROL, "no-cache")
            .header(AUTHORIZATION, basicAuth)
            .post("grant_type=client_credentials".toRequestBody("application/x-www-form-urlencoded".toMediaType()))
            .build()
        client.newCall(request).execute().use { response ->
            RestUtils.throwIfNotSuccessful(response)
            val ordsToken = RestUtils.parseJsonResponseOrThrow(response, OrdsToken::class.java)
            tokenCache = TokenCache(ordsToken)
        }
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

    companion object {
        const val ARENA_ORDS_CLIENT_ID_PROPERTY = "ARENA_ORDS_CLIENT_ID"
        const val ARENA_ORDS_CLIENT_SECRET_PROPERTY = "ARENA_ORDS_CLIENT_SECRET"
        private const val MINIMUM_TIME_TO_EXPIRE_BEFORE_REFRESH: Long = 60
    }
}
