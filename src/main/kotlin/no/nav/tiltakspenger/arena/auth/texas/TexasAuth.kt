package no.nav.tiltakspenger.arena.auth.texas

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.PipelineCall
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.request.authorization
import io.ktor.server.response.respond
import io.ktor.util.AttributeKey
import no.nav.tiltakspenger.arena.auth.texas.client.TexasClient
import no.nav.tiltakspenger.arena.auth.texas.client.TexasIntrospectionResponse

class AuthPluginConfiguration(
    var client: TexasClient? = null,
)

val log = KotlinLogging.logger("TexasAuth")

val tillatteInnloggingsnivaer = listOf("idporten-loa-high", "Level4")

private val fnrAttributeKey = AttributeKey<String>("fnr")

val TexasAuthTokenX =
    createRouteScopedPlugin(
        name = "TexasAuthTokenX",
        createConfiguration = ::AuthPluginConfiguration,
    ) {
        val client = pluginConfig.client ?: throw IllegalArgumentException("TexasAuth plugin: client must be set")

        pluginConfig.apply {
            onCall { call ->
                val tokenClaims = validateAndGetClaims(call) { token ->
                    client.introspectToken(
                        token = token,
                        identityProvider = "tokenx",
                    )
                } ?: return@onCall

                val fnr = tokenClaims["pid"]?.toString()
                if (fnr == null) {
                    log.warn { "Fant ikke fnr i pid-claim" }
                    call.respond(HttpStatusCode.InternalServerError)
                    return@onCall
                }

                val level = tokenClaims["acr"]?.toString()
                if (level == null || level !in tillatteInnloggingsnivaer) {
                    log.warn { "unauthenticated: må ha innloggingsnivå 4" }
                    call.respond(HttpStatusCode.Unauthorized)
                    return@onCall
                }

                call.attributes.put(fnrAttributeKey, fnr)
            }
        }
    }

val TexasAuthEntraId =
    createRouteScopedPlugin(
        name = "TexasAuthEntraId",
        createConfiguration = ::AuthPluginConfiguration,
    ) {
        val client = pluginConfig.client ?: throw IllegalArgumentException("TexasAuth plugin: client must be set")

        pluginConfig.apply {
            onCall { call ->
                validateAndGetClaims(call) { token ->
                    client.introspectToken(
                        token = token,
                        identityProvider = "azuread",
                    )
                } ?: return@onCall
            }
        }
    }

private suspend fun validateAndGetClaims(
    call: PipelineCall,
    introspectToken: suspend (token: String) -> TexasIntrospectionResponse,
): Map<String, Any?>? {
    val token = call.bearerToken()
    if (token == null) {
        log.warn { "unauthenticated: no Bearer token found in Authorization header" }
        call.respond(HttpStatusCode.Unauthorized)
        return null
    }

    val introspectResponse =
        try {
            introspectToken(token)
        } catch (e: Exception) {
            log.error { "unauthenticated: introspect request failed: ${e.message}" }
            call.respond(HttpStatusCode.Unauthorized)
            return null
        }

    if (!introspectResponse.active) {
        log.warn { "unauthenticated: ${introspectResponse.error}" }
        call.respond(HttpStatusCode.Unauthorized)
        return null
    }

    return introspectResponse.other
}

fun ApplicationCall.bearerToken(): String? =
    request
        .authorization()
        ?.takeIf { it.startsWith("Bearer ", ignoreCase = true) }
        ?.removePrefix("Bearer ")
        ?.removePrefix("bearer ")

fun ApplicationCall.fnr(): String {
    return this.attributes[fnrAttributeKey]
}
