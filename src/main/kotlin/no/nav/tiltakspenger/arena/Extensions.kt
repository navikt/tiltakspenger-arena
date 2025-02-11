package no.nav.tiltakspenger.arena

import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.authentication
import no.nav.security.token.support.v3.TokenValidationContextPrincipal

internal fun ApplicationCall.getClaim(issuer: String, name: String): String? =
    this.authentication.principal<TokenValidationContextPrincipal>()
        ?.context
        ?.getClaims(issuer)
        ?.getStringClaim(name)

fun ApplicationCall.fødselsnummer(): String? {
    return this.getClaim("tokendings", "pid")
}
