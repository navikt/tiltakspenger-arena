package no.nav.tiltakspenger.arena.db

import com.zaxxer.hikari.HikariDataSource
import io.github.oshai.kotlinlogging.KotlinLogging
import kotliquery.Session
import kotliquery.TransactionalSession
import kotliquery.sessionOf
import no.nav.tiltakspenger.arena.Configuration
import java.time.Duration
import java.time.Instant

private val LOG = KotlinLogging.logger {}

/**
 * Antall sekunder før Oracle avbryter en statistikkforespørsel.
 * Grensen gjelder hele forespørselen, også når den består av flere spørringer.
 * Grensen ligger under klienttimeouten på 180 sekunder i tiltakspenger-saksbehandling-api, slik at spørringen ikke fortsetter etter at klienten har gitt opp.
 */
const val STATISTIKK_SPØRRINGSTIMEOUT_SEKUNDER = 170

/**
 * Gir sekundene som er igjen av [STATISTIKK_SPØRRINGSTIMEOUT_SEKUNDER] for en statistikkforespørsel som startet [start], og minst ett sekund.
 * Kaster feil når tiden er brukt opp, slik at neste spørring ikke startes.
 */
fun gjenståendeSpørretidSekunder(start: Instant, nå: Instant): Int {
    val gjenstående = Duration.ofSeconds(STATISTIKK_SPØRRINGSTIMEOUT_SEKUNDER.toLong()) - Duration.between(start, nå)
    check(gjenstående > Duration.ZERO) { "Statistikkforespørselen har overskredet tidsgrensen på $STATISTIKK_SPØRRINGSTIMEOUT_SEKUNDER sekunder" }
    return gjenstående.toSeconds().toInt().coerceAtLeast(1)
}

object Datasource {
    private const val MAX_POOLS = 3

    private fun init(): HikariDataSource {
        // Leses her (ikke som eager objekt-felt) så DB-tilkoblingen først slås opp når datakilden faktisk bygges - da har testene satt propsene via OracleTestbase.
        val url = Configuration.arenaDbUrl
        LOG.info { "Kobler til Oracle '$url" }

        return HikariDataSource().apply {
            jdbcUrl = url
            username = Configuration.arenaDbUsername
            password = Configuration.arenaDbPassword
            maximumPoolSize = MAX_POOLS
        }
    }

    val hikariDataSource: HikariDataSource by lazy {
        init()
    }

    /**
     * Egen datakilde med én tilkobling for statistikk, slik at tunge uttrekk ikke kjører samtidig eller tar tilkoblinger fra vedtaksoppslagene.
     * Oracle-driveren henter 1000 rader per rundtur i stedet for 10, så store svar gir færre rundturer.
     */
    val statistikkDataSource: HikariDataSource by lazy {
        HikariDataSource().apply {
            jdbcUrl = Configuration.arenaDbUrl
            username = Configuration.arenaDbUsername
            password = Configuration.arenaDbPassword
            maximumPoolSize = 1
            addDataSourceProperty("defaultRowPrefetch", "1000")
        }
    }

    /** Sesjon mot [statistikkDataSource] med [queryTimeoutSekunder] som tidsgrense for hver spørring. */
    fun statistikkSession(queryTimeoutSekunder: Int = STATISTIKK_SPØRRINGSTIMEOUT_SEKUNDER): Session =
        sessionOf(statistikkDataSource, queryTimeout = queryTimeoutSekunder)

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
