package no.nav.tiltakspenger.arena

import com.natpryce.konfig.ConfigurationMap
import com.natpryce.konfig.ConfigurationProperties.Companion.systemProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.Key
import com.natpryce.konfig.intType
import com.natpryce.konfig.overriding
import com.natpryce.konfig.stringType
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object Configuration {
    private val defaultProperties = ConfigurationMap(
        mapOf(
            "application.httpPort" to 8080.toString(),
            "NAIS_TOKEN_INTROSPECTION_ENDPOINT" to System.getenv("NAIS_TOKEN_INTROSPECTION_ENDPOINT"),
            "NAIS_TOKEN_ENDPOINT" to System.getenv("NAIS_TOKEN_ENDPOINT"),
            "NAIS_TOKEN_EXCHANGE_ENDPOINT" to System.getenv("NAIS_TOKEN_EXCHANGE_ENDPOINT"),
            "ARENADB_URL" to fromFileOrSystemProperty("/secrets/dbconfig/jdbc_url", "ARENADB_URL"),
            "ARENADB_USERNAME" to fromFileOrSystemProperty("/secrets/dbcreds/username", "ARENADB_USERNAME"),
            "ARENADB_PASSWORD" to fromFileOrSystemProperty("/secrets/dbcreds/password", "ARENADB_PASSWORD"),
        ),
    )
    private val localProperties = ConfigurationMap(
        mapOf(
            "application.profile" to Profile.LOCAL.toString(),
            "NAIS_TOKEN_INTROSPECTION_ENDPOINT" to "",
            "NAIS_TOKEN_ENDPOINT" to "",
            "NAIS_TOKEN_EXCHANGE_ENDPOINT" to "",
        ),
    )
    private val devProperties = ConfigurationMap(
        mapOf(
            "application.profile" to Profile.DEV.toString(),
        ),
    )
    private val prodProperties = ConfigurationMap(
        mapOf(
            "application.profile" to Profile.PROD.toString(),
        ),
    )

    private fun config() = when (System.getenv("NAIS_CLUSTER_NAME") ?: System.getProperty("NAIS_CLUSTER_NAME")) {
        "dev-fss" ->
            systemProperties() overriding EnvironmentVariables overriding devProperties overriding defaultProperties

        "prod-fss" ->
            systemProperties() overriding EnvironmentVariables overriding prodProperties overriding defaultProperties

        else -> {
            systemProperties() overriding EnvironmentVariables overriding localProperties overriding defaultProperties
        }
    }

    data class ArenaDbConfig(
        val arenaDbUrl: String = config()[Key("ARENADB_URL", stringType)],
        val arenaDbUsername: String = config()[Key("ARENADB_USERNAME", stringType)],
        val arenaDbPassword: String = config()[Key("ARENADB_PASSWORD", stringType)],
    )

    val naisTokenIntrospectionEndpoint: String = config()[Key("NAIS_TOKEN_INTROSPECTION_ENDPOINT", stringType)]
    val naisTokenEndpoint: String = config()[Key("NAIS_TOKEN_ENDPOINT", stringType)]
    val tokenExchangeEndpoint: String = config()[Key("NAIS_TOKEN_EXCHANGE_ENDPOINT", stringType)]

    fun applicationProfile() = when (System.getenv("NAIS_CLUSTER_NAME") ?: System.getProperty("NAIS_CLUSTER_NAME")) {
        "dev-fss" -> Profile.DEV
        "prod-fss" -> Profile.PROD
        else -> Profile.LOCAL
    }

    fun httpPort() = config()[Key("application.httpPort", intType)]

    private fun fromFileOrSystemProperty(filename: String, property: String): String {
        if (applicationProfile() == Profile.LOCAL) {
            return System.getProperty(property)
        }
        try {
            val file: Path = Paths.get(filename)
            val lines = Files.readAllLines(file)
            return lines.first()
        } catch (exception: IOException) {
            throw RuntimeException("Failed to read property value from $filename", exception)
        }
    }
}

enum class Profile {
    LOCAL,
    DEV,
    PROD,
}
