package no.nav.tiltakspenger.arena.clients.azure

import no.nav.tiltakspenger.arena.Configuration
import java.time.LocalDateTime

class AzureAdClient(
    private val azureAdConfiguration: Configuration.AzureAdConfig,
    private val pdlClientId: String,
) {

    suspend fun hentMaskinTilMaskinTokenScopetMotPDL(): Token {
        val formUrlEncode = listOf(
            "client_id" to azureAdConfiguration.clientId,
            "scope" to "api://$pdlClientId/.default",
            "client_secret" to azureAdConfiguration.clientSecret,
            "grant_type" to "client_credentials",
        ).formUrlEncode()

        return apacheHttpClient.post {
            url(azureAdConfiguration.tokenEndpoint)
            setBody(TextContent(formUrlEncode, ContentType.Application.FormUrlEncoded))
        }.body()
    }
}

data class Token(
    val access_token: String,
    val token_type: String,
    val expires_in: Int,
) {

    private val expirationTime: LocalDateTime = LocalDateTime.now().plusSeconds(expires_in - 20L)

    fun hasExpired(): Boolean = expirationTime.isBefore(LocalDateTime.now())
}

fun Token?.shouldBeRenewed(): Boolean = this?.hasExpired() ?: true
