package no.nav.tiltakspenger.arena

/**
 * Ferdig setning som legges på slutten av vanlige logglinjer som har en tilhørende
 * sikkerlogg-linje. Lenken går til appens logger i Google Cloud Console for riktig miljø,
 * der sikkerloggen (team-logs) kan leses.
 */
val SE_SIKKERLOGG: String by lazy {
    val prosjekt = when (Configuration.applicationProfile()) {
        Profile.PROD -> "tpts-prod-b5ff"
        else -> "tpts-dev-6211"
    }
    "Se sikkerlogg for mer kontekst: " +
        "https://console.cloud.google.com/logs/query;query=resource.labels.container_name%3D%22tiltakspenger-arena%22?project=$prosjekt"
}
