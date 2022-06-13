package no.nav.tiltakspenger.arena.tiltakogaktivitet

import no.nav.common.json.JsonUtils.fromJson
import no.nav.common.rest.client.RestClient
import no.nav.common.rest.client.RestUtils
import no.nav.common.types.identer.Fnr
import no.nav.common.utils.UrlUtils
import no.nav.tiltakspenger.arena.tiltakogaktivitet.XmlUtils.fromXml
import okhttp3.Request
import javax.ws.rs.core.HttpHeaders.AUTHORIZATION

class ArenaOrdsClientImpl(
    private val arenaOrdsUrl: String,
    private val arenaOrdsTokenProvider: ArenaOrdsTokenProviderClient
) :
    ArenaOrdsClient {

    private val client = RestClient.baseClient()

    // fun checkHealth(): HealthCheckResult {
    //     return HealthCheckUtils.pingUrl(UrlUtils.joinPaths(arenaOrdsUrl, "arena/api/v1/test/ping"), client)
    // }

    override fun hentArenaOppfolgingsstatus(fnr: Fnr): ArenaOppfølgingsstatusDTO {
        val url = UrlUtils.joinPaths(
            arenaOrdsUrl,
            "arena/api/v1/person/oppfoelging/oppfoelgingsstatus?p_fnr=$fnr"
        )
        return fromJson(get(url), ArenaOppfølgingsstatusDTO::class.java)
    }

    override fun hentArenaOppfolginssak(fnr: Fnr): ArenaOppfølgingssakDTO {
        val url = UrlUtils.joinPaths(
            arenaOrdsUrl,
            "arena/api/v1/person/oppfoelging/oppfoelgingssak?p_fnr=$fnr"
        )
        return fromJson(get(url), ArenaOppfølgingssakDTO::class.java)
    }

    override fun hentArenaAktiviteter(fnr: Fnr): ArenaAktiviteterDTO? {
        val url = UrlUtils.joinPaths(arenaOrdsUrl, "arena/api/v1/person/oppfoelging/aktiviteter")
        val request: Request = Request.Builder()
            .url(url)
            .header("fnr", fnr.get())
            .header(AUTHORIZATION, RestUtils.createBearerToken(arenaOrdsTokenProvider.token))
            .build()
        client.newCall(request).execute().use { response ->
            RestUtils.throwIfNotSuccessful(response)
            return RestUtils.getBodyStr(response).get().let { fromXml(it, ArenaAktiviteterDTO::class.java) }
        }
    }

    private fun get(path: String): String {
        val request: Request = Request.Builder()
            .url(path)
            .header(AUTHORIZATION, RestUtils.createBearerToken(arenaOrdsTokenProvider.token))
            .build()
        client.newCall(request).execute().use { response ->
            RestUtils.throwIfNotSuccessful(response)
            return RestUtils.getBodyStr(response).get()
        }
    }
}
