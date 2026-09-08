package no.nav.tiltakspenger.arena

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

enum class Profile {
    LOCAL,
    DEV,
    PROD,
}

sealed interface EnvironmentConfig {
    val profile: Profile
    val httpPort: Int
    val logbackConfigurationFile: String

    val tokenEndpoint: String
    val tokenIntrospectionEndpoint: String
    val tokenExchangeEndpoint: String

    /** Oracle-tilkoblingen mot Arena; brukernavn og passord hentes fra Vault-monterte filer i nais, og fra system-properties lokalt (satt av OracleTestbase). */
    val arenaDbUrl: String
    val arenaDbUsername: String
    val arenaDbPassword: String
}

data object LocalConfig : EnvironmentConfig {
    override val profile = Profile.LOCAL
    override val httpPort = 8080
    override val logbackConfigurationFile = "logback.xml"

    // Appen har ingen lokal Texas-instans i docker-compose; endepunktene er tomme lokalt.
    override val tokenEndpoint = ""
    override val tokenIntrospectionEndpoint = ""
    override val tokenExchangeEndpoint = ""

    // Testcontaineren (OracleTestbase) setter disse som system-properties når den er startet, derfor må de leses på nytt ved hvert oppslag.
    override val arenaDbUrl: String
        get() = System.getProperty("ARENADB_URL") ?: error("System property ARENADB_URL er ikke satt")
    override val arenaDbUsername: String
        get() = System.getProperty("ARENADB_USERNAME") ?: error("System property ARENADB_USERNAME er ikke satt")
    override val arenaDbPassword: String
        get() = System.getProperty("ARENADB_PASSWORD") ?: error("System property ARENADB_PASSWORD er ikke satt")
}

data object DevConfig : EnvironmentConfig {
    override val profile = Profile.DEV
    override val httpPort = 8080
    override val logbackConfigurationFile = "logback.xml"

    override val tokenEndpoint: String = System.getenv("NAIS_TOKEN_ENDPOINT")
    override val tokenIntrospectionEndpoint: String = System.getenv("NAIS_TOKEN_INTROSPECTION_ENDPOINT")
    override val tokenExchangeEndpoint: String = System.getenv("NAIS_TOKEN_EXCHANGE_ENDPOINT")

    // Lazy: DB-secrets leses fra Vault-monterte filer først når datakilden faktisk bygges.
    override val arenaDbUrl: String by lazy { lesSecretFil("/secrets/dbconfig/jdbc_url") }
    override val arenaDbUsername: String by lazy { lesSecretFil("/secrets/dbcreds/username") }
    override val arenaDbPassword: String by lazy { lesSecretFil("/secrets/dbcreds/password") }
}

data object ProdConfig : EnvironmentConfig {
    override val profile = Profile.PROD
    override val httpPort = 8080
    override val logbackConfigurationFile = "logback.xml"

    override val tokenEndpoint: String = System.getenv("NAIS_TOKEN_ENDPOINT")
    override val tokenIntrospectionEndpoint: String = System.getenv("NAIS_TOKEN_INTROSPECTION_ENDPOINT")
    override val tokenExchangeEndpoint: String = System.getenv("NAIS_TOKEN_EXCHANGE_ENDPOINT")

    // Lazy: DB-secrets leses fra Vault-monterte filer først når datakilden faktisk bygges.
    override val arenaDbUrl: String by lazy { lesSecretFil("/secrets/dbconfig/jdbc_url") }
    override val arenaDbUsername: String by lazy { lesSecretFil("/secrets/dbcreds/username") }
    override val arenaDbPassword: String by lazy { lesSecretFil("/secrets/dbcreds/password") }
}

private fun lesSecretFil(sti: String): String {
    try {
        return Files.readAllLines(Paths.get(sti)).first()
    } catch (exception: IOException) {
        throw RuntimeException("Failed to read property value from $sti", exception)
    }
}
