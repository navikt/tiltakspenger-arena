package no.nav.tiltakspenger.arena

import com.natpryce.konfig.*
import com.natpryce.konfig.ConfigurationProperties.Companion.systemProperties

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

    val otherDefaultProperties = mapOf(
        "application.httpPort" to 8080.toString(),
        "stsUsername" to System.getenv("SERVICEUSER_USERNAME"),
        "stsPassword" to System.getenv("SERVICEUSER_PASSWORD")
    )
    private val defaultProperties = ConfigurationMap(
        rapidsAndRivers + otherDefaultProperties
    )
    private val localProperties = ConfigurationMap(
        mapOf(
            "stsUrl" to "",
            "ytelseskontraktUrl" to "",
            "tiltakogaktivitetUrl" to "",
            "application.profile" to Profile.LOCAL.toString(),
        )
    )
    private val devProperties = ConfigurationMap(
        mapOf(
            "stsUrl" to "https://sts-q1.preprod.local/SecurityTokenServiceProvider/",
            "ytelseskontraktUrl" to "https://arena-q1.adeo.no/ail_ws/Ytelseskontrakt_v3",
            "tiltakogaktivitetUrl" to "https://arena-q1.adeo.no/ail_ws/TiltakOgAktivitet_v1",
            "application.profile" to Profile.LOCAL.toString(),
        )
    )
    private val prodProperties = ConfigurationMap(
        mapOf(
            "stsUrl" to "",
            "ytelseskontraktUrl" to "",
            "tiltakogaktivitetUrl" to "",
            "application.profile" to Profile.LOCAL.toString(),
        )
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
        val stsUsername: String = config()[Key("stsUsername", stringType)],
        val stsPassword: String = config()[Key("stsPassword", stringType)],
    )
}

enum class Profile {
    LOCAL, DEV, PROD
}
