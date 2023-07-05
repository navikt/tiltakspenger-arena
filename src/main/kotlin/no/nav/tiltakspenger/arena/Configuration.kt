package no.nav.tiltakspenger.arena

import com.natpryce.konfig.ConfigurationMap
import com.natpryce.konfig.ConfigurationProperties.Companion.systemProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.Key
import com.natpryce.konfig.overriding
import com.natpryce.konfig.stringType

object Configuration {

    val rapidsAndRivers = mapOf(
        "RAPID_APP_NAME" to "tiltakspenger-arena",
        "KAFKA_BROKERS" to System.getenv("KAFKA_BROKERS"),
        "KAFKA_CREDSTORE_PASSWORD" to System.getenv("KAFKA_CREDSTORE_PASSWORD"),
        "KAFKA_TRUSTSTORE_PATH" to System.getenv("KAFKA_TRUSTSTORE_PATH"),
        "KAFKA_KEYSTORE_PATH" to System.getenv("KAFKA_KEYSTORE_PATH"),
        "KAFKA_RAPID_TOPIC" to "tpts.rapid.v1",
        "KAFKA_RESET_POLICY" to "latest",
        "KAFKA_CONSUMER_GROUP_ID" to "tiltakspenger-arena-v1",
    )

    private val otherDefaultProperties = mapOf(
        "application.httpPort" to 8080.toString(),
        "SERVICEUSER_TPTS_USERNAME" to System.getenv("SERVICEUSER_TPTS_USERNAME"),
        "SERVICEUSER_TPTS_PASSWORD" to System.getenv("SERVICEUSER_TPTS_PASSWORD"),
        "ARENA_ORDS_CLIENT_ID" to System.getenv("ARENA_ORDS_CLIENT_ID"),
        "ARENA_ORDS_CLIENT_SECRET" to System.getenv("ARENA_ORDS_CLIENT_SECRET"),
    )
    private val defaultProperties = ConfigurationMap(rapidsAndRivers + otherDefaultProperties)
    private val localProperties = ConfigurationMap(
        mapOf(
            "stsUrl" to "",
            "ytelseskontraktUrl" to "",
            "application.profile" to Profile.LOCAL.toString(),
            "ARENA_ORDS_URL" to "",
        ),
    )
    private val devProperties = ConfigurationMap(
        mapOf(
            "stsUrl" to "https://sts-q1.preprod.local/SecurityTokenServiceProvider/",
            "ytelseskontraktUrl" to "https://arena-q1.adeo.no/ail_ws/Ytelseskontrakt_v3",
            "application.profile" to Profile.DEV.toString(),
            "ARENA_ORDS_URL" to "https://arena-ords-q1.nais.preprod.local",
        ),
    )
    private val prodProperties = ConfigurationMap(
        mapOf(
            "stsUrl" to "https://sts.adeo.no/SecurityTokenServiceProvider/",
            "ytelseskontraktUrl" to "https://arena.adeo.no/ail_ws/Ytelseskontrakt_v3",
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

    data class ArenaSoapConfig(
        val ytelseskontraktUrl: String = config()[Key("ytelseskontraktUrl", stringType)],
        val stsUrl: String = config()[Key("stsUrl", stringType)],
        val stsUsername: String = config()[Key("SERVICEUSER_TPTS_USERNAME", stringType)],
        val stsPassword: String = config()[Key("SERVICEUSER_TPTS_PASSWORD", stringType)],
    )

    data class ArenaOrdsConfig(
        val arenaOrdsUrl: String = config()[Key("ARENA_ORDS_URL", stringType)],
        val arenaOrdsClientId: String = config()[Key("ARENA_ORDS_CLIENT_ID", stringType)],
        val arenaOrdsClientSecret: String = config()[Key("ARENA_ORDS_CLIENT_SECRET", stringType)],
    )

    fun applicationProfile() = when (System.getenv("NAIS_CLUSTER_NAME") ?: System.getProperty("NAIS_CLUSTER_NAME")) {
        "dev-fss" -> Profile.DEV
        "prod-fss" -> Profile.PROD
        else -> Profile.LOCAL
    }
}

enum class Profile {
    LOCAL, DEV, PROD
}
