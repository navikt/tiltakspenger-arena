package no.nav.tiltakspenger.arena

import com.natpryce.konfig.ConfigurationMap
import com.natpryce.konfig.ConfigurationProperties.Companion.systemProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.Key
import com.natpryce.konfig.intType
import com.natpryce.konfig.overriding
import com.natpryce.konfig.stringType

object Configuration {

    private val defaultProperties = ConfigurationMap(
        mapOf(
            "application.httpPort" to 8080.toString(),
            "SERVICEUSER_TPTS_USERNAME" to System.getenv("SERVICEUSER_TPTS_USERNAME"),
            "SERVICEUSER_TPTS_PASSWORD" to System.getenv("SERVICEUSER_TPTS_PASSWORD"),
            "ARENA_ORDS_CLIENT_ID" to System.getenv("ARENA_ORDS_CLIENT_ID"),
            "ARENA_ORDS_CLIENT_SECRET" to System.getenv("ARENA_ORDS_CLIENT_SECRET"),
            "NAIS_TOKEN_INTROSPECTION_ENDPOINT" to System.getenv("NAIS_TOKEN_INTROSPECTION_ENDPOINT"),
        ),
    )
    private val localProperties = ConfigurationMap(
        mapOf(
            "stsUrl" to "",
            "application.profile" to Profile.LOCAL.toString(),
            "ARENA_ORDS_URL" to "",
            "NAIS_TOKEN_INTROSPECTION_ENDPOINT" to "",
        ),
    )
    private val devProperties = ConfigurationMap(
        mapOf(
            "stsUrl" to "https://sts-q1.preprod.local/SecurityTokenServiceProvider/",
            "application.profile" to Profile.DEV.toString(),
            "ARENA_ORDS_URL" to "https://arena-ords-q2.nais.preprod.local",
        ),
    )
    private val prodProperties = ConfigurationMap(
        mapOf(
            "stsUrl" to "https://sts.adeo.no/SecurityTokenServiceProvider/",
            "application.profile" to Profile.PROD.toString(),
            "ARENA_ORDS_URL" to "https://arena-ords.nais.adeo.no",
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

    data class ArenaOrdsConfig(
        val arenaOrdsUrl: String = config()[Key("ARENA_ORDS_URL", stringType)],
        val arenaOrdsClientId: String = config()[Key("ARENA_ORDS_CLIENT_ID", stringType)],
        val arenaOrdsClientSecret: String = config()[Key("ARENA_ORDS_CLIENT_SECRET", stringType)],
    )

    val naisTokenIntrospectionEndpoint: String = config()[Key("NAIS_TOKEN_INTROSPECTION_ENDPOINT", stringType)]

    fun applicationProfile() = when (System.getenv("NAIS_CLUSTER_NAME") ?: System.getProperty("NAIS_CLUSTER_NAME")) {
        "dev-fss" -> Profile.DEV
        "prod-fss" -> Profile.PROD
        else -> Profile.LOCAL
    }

    fun httpPort() = config()[Key("application.httpPort", intType)]
}

enum class Profile {
    LOCAL,
    DEV,
    PROD,
}
