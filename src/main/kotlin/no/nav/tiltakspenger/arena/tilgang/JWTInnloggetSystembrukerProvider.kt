package no.nav.tiltakspenger.arena.tilgang

import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.authentication
import mu.KotlinLogging
import no.nav.security.token.support.v2.TokenValidationContextPrincipal

private val LOG = KotlinLogging.logger {}

object JWTInnloggetSystembrukerProvider : InnloggetSystembrukerProvider {

    private val allAvailableRoles: List<Rolle> = listOf(
        Rolle.SKJERMING,
        Rolle.FORTROLIG_ADRESSE,
        Rolle.STRENGT_FORTROLIG_ADRESSE,
    )

    override fun krevInnloggetSystembruker(call: ApplicationCall): Systembruker {
        val azpName = requireNotNull(call.getClaim("azure", "azp_name")) { "azp_name er null i token" }
        val rollerFraToken = requireNotNull(call.getListClaim("azure", "roles")) { "roles er null i token" }
            .map { it.uppercase() }
            .also { LOG.info { "Vi fant disse rollene i systemtoken $it" } }

        val roller = allAvailableRoles.filter { rolle ->
            rollerFraToken.contains(rolle.name)
        }

        return Systembruker(
            brukernavn = azpName,
            roller = roller,
        )
    }

    private fun ApplicationCall.getListClaim(issuer: String, name: String): List<String>? =
        this.authentication.principal<TokenValidationContextPrincipal>()
            ?.context
            ?.getClaims(issuer)
            ?.getAsList(name)

    private fun ApplicationCall.getClaim(issuer: String, name: String): String? =
        this.authentication.principal<TokenValidationContextPrincipal>()
            ?.context
            ?.getClaims(issuer)
            ?.getStringClaim(name)
}
