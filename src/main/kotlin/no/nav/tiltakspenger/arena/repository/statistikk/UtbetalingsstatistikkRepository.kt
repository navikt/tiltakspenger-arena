package no.nav.tiltakspenger.arena.repository.statistikk

import io.github.oshai.kotlinlogging.KotlinLogging
import kotliquery.Row
import kotliquery.queryOf
import no.nav.tiltakspenger.arena.db.Datasource
import no.nav.tiltakspenger.arena.db.gjenståendeSpørretidSekunder
import no.nav.tiltakspenger.libs.periode.Periode
import org.intellij.lang.annotations.Language
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Clock
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import kotlin.time.measureTimedValue

private val logger = KotlinLogging.logger {}

private const val GRUNNSATS = "BASI"
private const val BARNETILLEGG = "BTIL"

data class UtbetalingerForMåned(
    val måned: String,
    /** Unike personer med netto utbetalt grunnsats over null i måneden. */
    val personerMedUtbetaling: Int,
    /**
     * Utbetalte dager med grunnsats.
     * Dager med barnetillegg telles ikke, fordi de føres per barn.
     */
    val dagerUtbetalt: BigDecimal,
    val beløpGrunnsats: BigDecimal,
    val beløpBarnetillegg: BigDecimal,
    /** Unike personer med beregnet, men ikke utbetalt grunnsats over null i måneden. */
    val personerMedBeregnetIkkeUtbetalt: Int,
    /** Beregnet, men ikke utbetalt grunnsats og barnetillegg. */
    val beløpBeregnetIkkeUtbetalt: BigDecimal,
)

/** Netto beløp og dager for én person, rettighet og meldeperiode. */
private data class NettoUtbetaling(
    val personId: Long,
    val rettighet: String,
    val meldeperiode: Periode,
    val beløp: BigDecimal,
    val dager: BigDecimal,
)

/** Delen av en [NettoUtbetaling] som faller i én måned. */
private data class Månedsandel(
    val personId: Long,
    val rettighet: String,
    val måned: YearMonth,
    val beløp: BigDecimal,
    val dager: BigDecimal,
)

private fun Periode.hverdagerPerMåned(): Map<YearMonth, Int> {
    return tilDager()
        .filter { it.dayOfWeek != DayOfWeek.SATURDAY && it.dayOfWeek != DayOfWeek.SUNDAY }
        .groupingBy { YearMonth.from(it) }
        .eachCount()
}

/**
 * Fordeler beløp og dager på månedene etter andelen hverdager i hver måned.
 * Har meldeperioden ingen hverdager, går alt til måneden perioden starter i.
 */
private fun NettoUtbetaling.fordelPåMåneder(): List<Månedsandel> {
    val hverdagerPerMåned = meldeperiode.hverdagerPerMåned()
    if (hverdagerPerMåned.isEmpty()) {
        return listOf(Månedsandel(personId, rettighet, YearMonth.from(meldeperiode.fraOgMed), beløp, dager))
    }
    val hverdager = hverdagerPerMåned.values.sum().toBigDecimal()
    return hverdagerPerMåned.map { (måned, hverdagerIMåned) ->
        val andel = hverdagerIMåned.toBigDecimal().divide(hverdager, 10, RoundingMode.HALF_UP)
        Månedsandel(personId, rettighet, måned, beløp * andel, dager * andel)
    }
}

private fun BigDecimal.tilØre(): BigDecimal = setScale(2, RoundingMode.HALF_UP)

private fun List<Månedsandel>.sumAv(rettighet: String, verdi: (Månedsandel) -> BigDecimal): BigDecimal =
    filter { it.rettighet == rettighet }.fold(BigDecimal.ZERO) { sum, andel -> sum + verdi(andel) }.tilØre()

private fun List<Månedsandel>.personerMedGrunnsats(): Int =
    filter { it.rettighet == GRUNNSATS }
        .groupBy { it.personId }
        .count { (_, andeler) -> andeler.fold(BigDecimal.ZERO) { sum, andel -> sum + andel.beløp } > BigDecimal.ZERO }

class UtbetalingsstatistikkRepository(private val clock: Clock) {
    /**
     * Gir utbetalte tiltakspenger i Arena per måned i [år]: personer med utbetalt grunnsats, utbetalte dager og beløp for grunnsats og barnetillegg.
     * Posteringene nettes per person, rettighet og meldeperiode slik at korrigeringer trekkes fra.
     * Beløp og dager i en meldeperiode fordeles på månedene etter andelen hverdager i hver måned.
     * Dette er en tilnærming, fordi posteringen bare har summen for perioden.
     * Spesialutbetalinger regnes til rettigheten på det relaterte vedtaket.
     * Utbetalingsgrunnlag som ennå ikke er overført til økonomisystemet, telles for seg og blandes aldri med posteringene.
     */
    fun hentUtbetalingerPerMåned(år: Int): List<UtbetalingerForMåned> {
        val årsperiode = Periode(LocalDate.of(år, 1, 1), LocalDate.of(år, 12, 31))
        val params = mapOf("fom" to årsperiode.fraOgMed, "tom" to årsperiode.tilOgMed)
        val start = Instant.now(clock)
        val (rader, spørretid) = measureTimedValue {
            val posteringer = Datasource.statistikkSession().use { session ->
                session.run(queryOf(sqlPostering, params).map(::tilNettoUtbetaling).asList)
            }
            val gjenståendeSekunder = gjenståendeSpørretidSekunder(start, Instant.now(clock))
            val utbetalingsgrunnlag = Datasource.statistikkSession(gjenståendeSekunder).use { session ->
                session.run(queryOf(sqlUtbetalingsgrunnlag, params).map(::tilNettoUtbetaling).asList)
            }
            Pair(posteringer, utbetalingsgrunnlag)
        }
        val (statistikk, foldetid) = measureTimedValue {
            val utbetalt = rader.first.flatMap { it.fordelPåMåneder() }.groupBy { it.måned }
            val beregnet = rader.second.flatMap { it.fordelPåMåneder() }.groupBy { it.måned }
            (1..12).map { YearMonth.of(år, it) }.map { måned ->
                val utbetaltIMåned = utbetalt[måned].orEmpty()
                val beregnetIMåned = beregnet[måned].orEmpty()
                UtbetalingerForMåned(
                    måned = måned.toString(),
                    personerMedUtbetaling = utbetaltIMåned.personerMedGrunnsats(),
                    dagerUtbetalt = utbetaltIMåned.sumAv(GRUNNSATS) { it.dager },
                    beløpGrunnsats = utbetaltIMåned.sumAv(GRUNNSATS) { it.beløp },
                    beløpBarnetillegg = utbetaltIMåned.sumAv(BARNETILLEGG) { it.beløp },
                    personerMedBeregnetIkkeUtbetalt = beregnetIMåned.personerMedGrunnsats(),
                    beløpBeregnetIkkeUtbetalt = beregnetIMåned.fold(BigDecimal.ZERO) { sum, andel -> sum + andel.beløp }.tilØre(),
                )
            }
        }
        logger.info {
            "Hentet utbetalinger per måned for $år: ${rader.first.size} grupper fra postering og ${rader.second.size} fra utbetalingsgrunnlag, spørring $spørretid, folding $foldetid"
        }
        return statistikk
    }

    private fun tilNettoUtbetaling(rad: Row): NettoUtbetaling {
        return NettoUtbetaling(
            personId = rad.long("person_id"),
            rettighet = rad.string("rettighetkode"),
            meldeperiode = Periode(rad.localDate("dato_periode_fra"), rad.localDate("dato_periode_til")),
            beløp = rad.bigDecimalOrNull("belop") ?: BigDecimal.ZERO,
            dager = rad.bigDecimalOrNull("antall") ?: BigDecimal.ZERO,
        )
    }

    // Netto per person, rettighet og meldeperiode for meldeperioder som overlapper året.
    @Language("SQL")
    private val sqlPostering =
        """
        SELECT p.person_id,
               COALESCE(vr.rettighetkode, v.rettighetkode) AS rettighetkode,
               p.dato_periode_fra,
               p.dato_periode_til,
               SUM(p.belop) AS belop,
               SUM(p.antall) AS antall
        FROM postering p
        JOIN vedtak v ON v.vedtak_id = p.vedtak_id
        LEFT JOIN vedtak vr ON p.tabellnavnalias_kilde = 'SPESUTB' AND vr.vedtak_id = v.vedtak_id_relatert
        WHERE p.dato_periode_fra <= :tom
        AND p.dato_periode_til >= :fom
        AND p.dato_periode_fra <= p.dato_periode_til
        AND COALESCE(vr.rettighetkode, v.rettighetkode) IN ('BASI', 'BTIL')
        GROUP BY p.person_id, COALESCE(vr.rettighetkode, v.rettighetkode), p.dato_periode_fra, p.dato_periode_til
        HAVING SUM(p.belop) <> 0 OR SUM(p.antall) <> 0
        """.trimIndent()

    // Beregnet, men ikke overført, med samme form og en vakt mot rader som allerede er flyttet til postering.
    @Language("SQL")
    private val sqlUtbetalingsgrunnlag =
        """
        SELECT u.person_id,
               COALESCE(vr.rettighetkode, v.rettighetkode) AS rettighetkode,
               u.dato_periode_fra,
               u.dato_periode_til,
               SUM(u.belop) AS belop,
               SUM(u.antall) AS antall
        FROM utbetalingsgrunnlag u
        JOIN vedtak v ON v.vedtak_id = u.vedtak_id
        LEFT JOIN vedtak vr ON u.tabellnavnalias_kilde = 'SPESUTB' AND vr.vedtak_id = v.vedtak_id_relatert
        WHERE u.dato_periode_fra <= :tom
        AND u.dato_periode_til >= :fom
        AND u.dato_periode_fra <= u.dato_periode_til
        AND COALESCE(vr.rettighetkode, v.rettighetkode) IN ('BASI', 'BTIL')
        AND NOT EXISTS (SELECT 1 FROM postering p WHERE p.postering_id = u.postering_id)
        GROUP BY u.person_id, COALESCE(vr.rettighetkode, v.rettighetkode), u.dato_periode_fra, u.dato_periode_til
        HAVING SUM(u.belop) <> 0 OR SUM(u.antall) <> 0
        """.trimIndent()
}
