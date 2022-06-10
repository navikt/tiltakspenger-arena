package no.nav.tiltakspenger.arena

import no.nav.tiltakspenger.arena.ytelser.ArenaSoapConfig
import no.nav.tiltakspenger.arena.ytelser.ArenaSoapService

internal object Configuration {
    val rapidsAndRivers =
        mapOf(
            "RAPID_APP_NAME" to "tiltakspenger-arena",
            "KAFKA_BROKERS" to System.getenv("KAFKA_BROKERS"),
            "KAFKA_CREDSTORE_PASSWORD" to System.getenv("KAFKA_CREDSTORE_PASSWORD"),
            "KAFKA_TRUSTSTORE_PATH" to System.getenv("KAFKA_TRUSTSTORE_PATH"),
            "KAFKA_KEYSTORE_PATH" to System.getenv("KAFKA_KEYSTORE_PATH"),
            "KAFKA_RAPID_TOPIC" to "tpts.rapid.v1",
            "KAFKA_RESET_POLICY" to "latest",
            "KAFKA_CONSUMER_GROUP_ID" to "tiltakspenger-arena-v1"
        )

    // Hører dette egentlig hjemme et annet sted?
    val arenaSoapConfig: ArenaSoapConfig = ArenaSoapConfig(
        ytelseskontraktUrl = "",
        stsUrl = "",
        stsUsername = "",
        stsPassword = ""
    )

    // Hører dette egentlig hjemme et annet sted?
    val arenaSoapService: ArenaSoapService =
        ArenaSoapService(ytelseskontraktV3Service = arenaSoapConfig.ytelseskontraktV3())
}
