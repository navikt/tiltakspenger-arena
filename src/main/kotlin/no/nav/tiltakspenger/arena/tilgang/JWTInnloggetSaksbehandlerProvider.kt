package no.nav.tiltakspenger.arena.tilgang

import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.authentication
import mu.KotlinLogging
import no.nav.security.token.support.v2.TokenValidationContextPrincipal
import no.nav.tiltakspenger.arena.Configuration
import java.util.UUID

private val LOG = KotlinLogging.logger {}

object JWTInnloggetSaksbehandlerProvider : InnloggetSaksbehandlerProvider {

    private val allAvailableRoles: List<AdRolle> = Configuration.alleAdRoller()

    override fun krevInnloggetSaksbehandler(call: ApplicationCall): Saksbehandler {
        val ident = requireNotNull(call.getClaim("azure", "NAVident")) { "NAVident er null i token" }
        val epost = requireNotNull(
            call.getClaim("azure", "preferred_username"),
        ) { "preferred_username er null i token" }
        val roller = requireNotNull(call.getListClaim("azure", "groups")) { "groups er null i token" }
            .map { UUID.fromString(it) }
            .mapFromUUIDToRoleName()

        return Saksbehandler(
            navIdent = ident,
            brukernavn = epostToBrukernavn(epost),
            epost = epost,
            roller = roller,
        )
    }

    private fun epostToBrukernavn(epost: String): String =
        epost.split("@").first().replace(".", " ")

    private fun finnRolleMedUUID(uuidFraClaim: UUID) =
        allAvailableRoles.single { configRole -> configRole.objectId == uuidFraClaim }

    private fun List<UUID>.mapFromUUIDToRoleName(): List<Rolle> =
        this.map { LOG.info { "Mapper rolle $it" }; it }
            .map { finnRolleMedUUID(it).name }

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
