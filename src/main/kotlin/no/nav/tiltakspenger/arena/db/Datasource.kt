package no.nav.tiltakspenger.arena.db

import com.zaxxer.hikari.HikariDataSource
import io.github.oshai.kotlinlogging.KotlinLogging
import kotliquery.Session
import kotliquery.TransactionalSession
import kotliquery.sessionOf
import no.nav.tiltakspenger.arena.Configuration

private val LOG = KotlinLogging.logger {}

object Datasource {
    private const val MAX_POOLS = 3
    private val arenaDbConfig = Configuration.ArenaDbConfig()

    private fun init(): HikariDataSource {
        val url = arenaDbConfig.arenaDbUrl
        LOG.info { "Kobler til Oracle '$url" }

        return HikariDataSource().apply {
            driverClassName = "oracle.jdbc.driver.OracleDriver"
            jdbcUrl = url
            username = arenaDbConfig.arenaDbUsername
            password = arenaDbConfig.arenaDbPassword
            maximumPoolSize = MAX_POOLS
        }
    }

    val hikariDataSource: HikariDataSource by lazy {
        init()
    }

    inline fun <T> withTx(
        existing: TransactionalSession?,
        block: (TransactionalSession) -> T,
    ): T {
        if (existing != null) return block(existing)

        return sessionOf(hikariDataSource).use { session: Session ->
            session.transaction { createdTx -> block(createdTx) }
        }
    }
}
