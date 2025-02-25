package no.nav.tiltakspenger.arena.tiltakogaktivitet

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import mu.KotlinLogging
import no.nav.tiltakspenger.arena.Configuration
import no.nav.tiltakspenger.arena.httpClientXml
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsException.OtherException
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsException.PersonNotFoundException
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsException.UnauthorizedException
import no.nav.tiltakspenger.libs.logging.sikkerlogg

private val LOG = KotlinLogging.logger {}

// Lenke til dokumentasjon i confluence
// Aktiviteter: https://confluence.adeo.no/pages/viewpage.action?pageId=470748287
// Mulige responskoder er 204, 401 og 500
class ArenaOrdsClientImpl(
    private val arenaOrdsConfig: Configuration.ArenaOrdsConfig,
    private val arenaOrdsTokenProvider: ArenaOrdsTokenProviderClient,
    private val client: HttpClient = httpClientXml(),
) : ArenaOrdsClient {

    override suspend fun hentArenaAktiviteter(fnr: String): ArenaAktiviteterDTO {
        val url = arenaOrdsConfig.arenaOrdsUrl + "/arena/api/v1/person/oppfoelging/aktiviteter"
        val response = client.get(urlString = url) {
            bearerAuth(arenaOrdsTokenProvider.token())
            header("fnr", fnr)
        }
        if (!response.status.isSuccess()) {
            when (response.status) {
                HttpStatusCode.Unauthorized -> {
                    val exceptionResponseText = response.bodyAsText()
                    throw UnauthorizedException(exceptionResponseText, RuntimeException("Unauthorized ved henting av aktiviteter"))
                }
                HttpStatusCode.InternalServerError -> {
                    val exceptionResponseText = response.bodyAsText()
                    throw OtherException(exceptionResponseText, RuntimeException("Internal server error ved henting av aktiviteter"))
                }
                else -> {
                    val text = response.bodyAsText()
                    LOG.error { "Noe gikk galt ved henting av aktiviteter, responskode ${response.status}" }
                    sikkerlogg.error { "Kunne ikke hente aktiviteter: $text" }
                    throw RuntimeException("Kunne ikke hente aktiviteter")
                }
            }
        } else if (response.status == HttpStatusCode.NoContent) {
            val text = response.bodyAsText()
            LOG.warn { "Bruker (person) finnes ikke i Arena" }
            sikkerlogg.warn { "Bruker (person) finnes ikke i Arena: $text" }
            throw PersonNotFoundException("Bruker (person) finnes ikke i Arena: $text")
        }
        val arenaAktiviteter = response.body<ArenaAktiviteterDTO>()
        return arenaAktiviteter
    }
}
