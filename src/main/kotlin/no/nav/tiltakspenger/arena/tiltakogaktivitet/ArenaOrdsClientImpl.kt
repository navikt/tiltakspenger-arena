package no.nav.tiltakspenger.arena.tiltakogaktivitet

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import no.nav.tiltakspenger.arena.Configuration

class ArenaOrdsClientImpl(
    private val arenaOrdsConfig: Configuration.ArenaOrdsConfig,
    private val arenaOrdsTokenProvider: ArenaOrdsTokenProviderClient
) : ArenaOrdsClient {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            jackson()
            jackson(contentType = ContentType.Application.Xml)
        }
    }

    // fun checkHealth(): HealthCheckResult {
    //     return HealthCheckUtils.pingUrl(UrlUtils.joinPaths(arenaOrdsUrl, "arena/api/v1/test/ping"), client)
    // }

    override suspend fun hentArenaOppfolgingsstatus(fnr: String): ArenaOppfølgingsstatusDTO {
        val url = arenaOrdsConfig.arenaOrdsUrl + "arena/api/v1/person/oppfoelging/oppfoelgingsstatus?p_fnr=$fnr"
        val response: ArenaOppfølgingsstatusDTO = client.get(urlString = url) {
            bearerAuth(arenaOrdsTokenProvider.token())
        }.body()
        return response
    }

    override suspend fun hentArenaOppfolgingssak(fnr: String): ArenaOppfølgingssakDTO {
        val url = arenaOrdsConfig.arenaOrdsUrl + "arena/api/v1/person/oppfoelging/oppfoelgingssak?p_fnr=$fnr"
        val response: ArenaOppfølgingssakDTO = client.get(urlString = url) {
            bearerAuth(arenaOrdsTokenProvider.token())
        }.body()
        return response
    }

    override suspend fun hentArenaAktiviteter(fnr: String): ArenaAktiviteterDTO {
        val url = arenaOrdsConfig.arenaOrdsUrl + "arena/api/v1/person/oppfoelging/aktiviteter"
        val response: ArenaAktiviteterDTO = client.get(urlString = url) {
            bearerAuth(arenaOrdsTokenProvider.token())
            header("fnr", fnr)
        }.body()
        return response
    }
}
