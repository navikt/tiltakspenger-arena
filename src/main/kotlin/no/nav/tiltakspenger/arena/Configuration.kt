package no.nav.tiltakspenger.arena

import com.natpryce.konfig.ConfigurationMap
import com.natpryce.konfig.ConfigurationProperties.Companion.systemProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.Key
import com.natpryce.konfig.overriding
import com.natpryce.konfig.stringType
import no.nav.tiltakspenger.arena.tilgang.AdRolle
import no.nav.tiltakspenger.arena.tilgang.Rolle
import java.util.UUID

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
        "ROLE_FORTROLIG" to System.getenv("ROLE_FORTROLIG"),
        "ROLE_STRENGT_FORTROLIG" to System.getenv("ROLE_STRENGT_FORTROLIG"),
        "ROLE_SKJERMING" to System.getenv("ROLE_SKJERMING"),
        "PDL_CLIENT_ID" to System.getenv("UDI_PROXY_CLIENT_ID"),
    )
    private val defaultProperties = ConfigurationMap(rapidsAndRivers + otherDefaultProperties)
    private val localProperties = ConfigurationMap(
        mapOf(
            "stsUrl" to "",
            "ytelseskontraktUrl" to "",
            "application.profile" to Profile.LOCAL.toString(),
            "ARENA_ORDS_URL" to "",
            "ROLE_FORTROLIG" to "ea930b6b-9397-44d9-b9e6-f4cf527a632a",
            "ROLE_STRENGT_FORTROLIG" to "5ef775f2-61f8-4283-bf3d-8d03f428aa14",
            "ROLE_SKJERMING" to "dbe4ad45-320b-4e9a-aaa1-73cca4ee124d",
        ),
    )
    private val devProperties = ConfigurationMap(
        mapOf(
            "stsUrl" to "https://sts-q1.preprod.local/SecurityTokenServiceProvider/",
            "ytelseskontraktUrl" to "https://arena-q2.adeo.no/ail_ws/Ytelseskontrakt_v3",
            "application.profile" to Profile.DEV.toString(),
            "ARENA_ORDS_URL" to "https://arena-ords-q2.nais.preprod.local",
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

    fun alleAdRoller(): List<AdRolle> = listOf(
        AdRolle(Rolle.FORTROLIG_ADRESSE, UUID.fromString(config()[Key("ROLE_FORTROLIG", stringType)])),
        AdRolle(
            Rolle.STRENGT_FORTROLIG_ADRESSE,
            UUID.fromString(config()[Key("ROLE_STRENGT_FORTROLIG", stringType)]),
        ),
        AdRolle(Rolle.SKJERMING, UUID.fromString(config()[Key("ROLE_SKJERMING", stringType)])),
    )

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

    data class AzureAdConfig(
        val clientId: String = "AZURE_APP_CLIENT_ID".configProperty(),
        val clientSecret: String = "AZURE_APP_CLIENT_SECRET".configProperty(),
        val jwtAudience: String = "AZURE_APP_CLIENT_ID".configProperty(),
        val tokenEndpoint: String = "AZURE_OPENID_CONFIG_TOKEN_ENDPOINT".configProperty().removeSuffix("/"),
        val azureAppWellKnownUrl: String = "AZURE_APP_WELL_KNOWN_URL".configProperty().removeSuffix("/"),
    )

    fun applicationProfile() = when (System.getenv("NAIS_CLUSTER_NAME") ?: System.getProperty("NAIS_CLUSTER_NAME")) {
        "dev-fss" -> Profile.DEV
        "prod-fss" -> Profile.PROD
        else -> Profile.LOCAL
    }

    private fun String.configProperty(): String = config()[Key(this, stringType)]
}

enum class Profile {
    LOCAL, DEV, PROD
}
