package no.nav.tiltakspenger.arena

private fun hentConfigForMiljø(): EnvironmentConfig {
    return when (System.getenv("NAIS_CLUSTER_NAME") ?: System.getProperty("NAIS_CLUSTER_NAME")) {
        "prod-fss" -> ProdConfig
        "dev-fss" -> DevConfig
        else -> LocalConfig
    }
}

object Configuration : EnvironmentConfig by hentConfigForMiljø() {
    fun isNais(): Boolean = profile != Profile.LOCAL

    fun isProd(): Boolean = profile == Profile.PROD
}
