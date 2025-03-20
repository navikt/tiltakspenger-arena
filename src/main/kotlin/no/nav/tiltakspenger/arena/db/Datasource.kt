package no.nav.tiltakspenger.arena.db

import com.zaxxer.hikari.HikariDataSource
import io.github.oshai.kotlinlogging.KotlinLogging

private val LOG = KotlinLogging.logger {}

object Datasource {
    private const val MAX_POOLS = 3
    const val DB_USERNAME_KEY = "ARENADB_USERNAME"
    const val DB_PASSWORD_KEY = "ARENADB_PASSWORD"
    const val DB_URL = "ARENADB_URL"

    private fun getEnvOrProp(key: String) = System.getenv(key) ?: System.getProperty(key)

    private fun init(): HikariDataSource {
        val url = getEnvOrProp(DB_URL)
        LOG.info { "Kobler til Oracle '$url" }

        return HikariDataSource().apply {
            driverClassName = "oracle.jdbc.driver.OracleDriver"
            jdbcUrl = url
            username = getEnvOrProp(DB_USERNAME_KEY)
            password = getEnvOrProp(DB_PASSWORD_KEY)
            maximumPoolSize = MAX_POOLS
        }
    }

    val hikariDataSource: HikariDataSource by lazy {
        init()
    }
}
