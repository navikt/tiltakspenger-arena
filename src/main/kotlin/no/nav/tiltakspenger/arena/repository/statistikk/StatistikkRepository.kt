package no.nav.tiltakspenger.arena.repository.statistikk

import kotliquery.queryOf
import kotliquery.sessionOf
import no.nav.tiltakspenger.arena.db.Datasource
import no.nav.tiltakspenger.libs.periode.Periode
import no.nav.tiltakspenger.libs.periodisering.Periodisering
import org.intellij.lang.annotations.Language
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

private val ARENA_DATOFORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy")

data class StatistikkForMåned(
    val måned: String,
    val sakerMedRett: Int,
    val nyeRettigheter: Int,
    val gjenopptak: Int,
    val endringer: Int,
    val stans: Int,
)

data class SakerMedRettEtter(
    val dato: LocalDate,
    val sakerMedRett: Int,
    val personerMedRett: Int,
)

private interface Rettighetsvedtak {
    val vedtaktype: String
    val fraDato: LocalDate
    val tilDato: LocalDate?
}

private data class Vedtaksrad(
    val sakId: Long,
    override val vedtaktype: String,
    override val fraDato: LocalDate,
    override val tilDato: LocalDate?,
    val vedtaksdato: LocalDate,
) : Rettighetsvedtak

private data class VedtaksradEtterDato(
    val sakId: Long,
    val personId: Long?,
    override val vedtaktype: String,
    override val fraDato: LocalDate,
    override val tilDato: LocalDate?,
) : Rettighetsvedtak

/**
 * Folder vedtakene for én sak i sakens rekkefølge og gir retten innenfor [periode].
 * Ny rettighet, gjenopptak og endring gir rett i vedtaksperioden, mens stans fjerner den fra stansdatoen.
 */
private fun List<Rettighetsvedtak>.rettIPeriode(periode: Periode): Periodisering<Boolean> {
    return fold(Periodisering(false, periode)) { rett, v ->
        val fra = maxOf(v.fraDato, periode.fraOgMed)
        val til = minOf(v.tilDato ?: periode.tilOgMed, periode.tilOgMed)
        if (fra > til) rett else rett.setVerdiForDelperiode(v.vedtaktype != "S", Periode(fra, til))
    }
}

class StatistikkRepository {
    /**
     * Gir per måned i [år] antall tiltakspengesaker med rett minst én dag, og antall vedtak fattet per vedtakstype.
     * Retten følger vedtakene i sakens rekkefølge (LOPENRVEDTAK): ny rettighet, gjenopptak og endring gir rett i vedtaksperioden, mens stans fjerner den fra stansdatoen.
     * Vedtaksdatoen er vedtakfaktumet INNVF, eller registreringsdatoen når det mangler.
     */
    fun hentStatistikkPerMåned(år: Int): List<StatistikkForMåned> {
        val årsperiode = Periode(LocalDate.of(år, 1, 1), LocalDate.of(år, 12, 31))
        val vedtak = sessionOf(Datasource.hikariDataSource).use { session ->
            session.run(
                queryOf(
                    sql,
                    mapOf(
                        "fom" to årsperiode.fraOgMed,
                        "tom" to årsperiode.tilOgMed,
                        // Et vedtak registrert i desember kan være fattet i januar.
                        "regFom" to årsperiode.fraOgMed.minusMonths(1),
                    ),
                ).map { rad ->
                    Vedtaksrad(
                        sakId = rad.long("sak_id"),
                        vedtaktype = rad.string("vedtaktypekode"),
                        fraDato = rad.localDate("fra_dato"),
                        tilDato = rad.localDateOrNull("til_dato"),
                        vedtaksdato = rad.stringOrNull("innvf")?.let { LocalDate.parse(it, ARENA_DATOFORMAT) }
                            ?: rad.localDate("reg_dato"),
                    )
                }.asList,
            )
        }
        val rettPerSak = vedtak.groupBy { it.sakId }.values.map { vedtakForSak -> vedtakForSak.rettIPeriode(årsperiode) }
        return (1..12).map { YearMonth.of(år, it) }.map { måned ->
            val månedsperiode = Periode(måned.atDay(1), måned.atEndOfMonth())
            val fattet = vedtak.filter { YearMonth.from(it.vedtaksdato) == måned }.groupingBy { it.vedtaktype }.eachCount()
            StatistikkForMåned(
                måned = måned.toString(),
                sakerMedRett = rettPerSak.count { rett -> rett.perioderMedVerdi.any { it.verdi && it.periode.overlapperMed(månedsperiode) } },
                nyeRettigheter = fattet["O"] ?: 0,
                gjenopptak = fattet["G"] ?: 0,
                endringer = fattet["E"] ?: 0,
                stans = fattet["S"] ?: 0,
            )
        }
    }

    /**
     * Teller tiltakspengesaker og unike personer med rett minst én dag etter [dato].
     * Vedtak som slutter på eller før [dato] hentes ikke fordi de ikke kan påvirke retten etter datoen.
     */
    fun hentSakerMedRettEtter(dato: LocalDate): SakerMedRettEtter {
        val vedtak = sessionOf(Datasource.hikariDataSource).use { session ->
            session.run(
                queryOf(sqlEtterDato, mapOf("dato" to dato)).map { rad ->
                    VedtaksradEtterDato(
                        sakId = rad.long("sak_id"),
                        personId = rad.longOrNull("person_id"),
                        vedtaktype = rad.string("vedtaktypekode"),
                        fraDato = rad.localDate("fra_dato"),
                        tilDato = rad.localDateOrNull("til_dato"),
                    )
                }.asList,
            )
        }
        val sakerMedRett = vedtak.groupBy { it.sakId }.values.filter { vedtakForSak ->
            // Horisonten tar med vedtak med åpen sluttdato og vedtak som starter lenge etter datoen.
            val horisont = maxOf(dato.plusDays(1), vedtakForSak.maxOf { it.tilDato ?: it.fraDato })
            vedtakForSak.rettIPeriode(Periode(dato.plusDays(1), horisont)).perioderMedVerdi.any { it.verdi }
        }
        return SakerMedRettEtter(
            dato = dato,
            sakerMedRett = sakerMedRett.size,
            personerMedRett = sakerMedRett.mapNotNull { it.first().personId }.distinct().size,
        )
    }

    // Samme utvalg av vedtak som TiltakspengerVedtakDAO, men med stans, og med vedtak som overlapper året eller er registrert rundt det.
    @Language("SQL")
    private val sql =
        """
        SELECT v.sak_id, v.vedtaktypekode, v.fra_dato, v.til_dato, v.reg_dato, f.vedtakverdi AS innvf
        FROM vedtak v
        LEFT JOIN vedtakfakta f ON f.vedtak_id = v.vedtak_id AND f.vedtakfaktakode = 'INNVF'
        WHERE v.rettighetkode = 'BASI'
        AND v.vedtaktypekode IN ('O', 'E', 'G', 'S')
        AND v.utfallkode NOT IN ('AVBRUTT', 'NEI')
        AND v.vedtakstatuskode IN ('IVERK', 'AVSLU')
        AND v.fra_dato IS NOT NULL
        AND v.fra_dato <= NVL(v.til_dato, v.fra_dato)
        AND (
            (v.fra_dato <= :tom AND (v.til_dato IS NULL OR v.til_dato >= :fom))
            OR v.reg_dato BETWEEN :regFom AND :tom
        )
        ORDER BY v.sak_id, v.lopenrvedtak, v.vedtak_id
        """.trimIndent()

    // Samme utvalg av vedtak som sql, men bare vedtak som slutter etter datoen eller ikke har sluttdato.
    @Language("SQL")
    private val sqlEtterDato =
        """
        SELECT v.sak_id, v.person_id, v.vedtaktypekode, v.fra_dato, v.til_dato
        FROM vedtak v
        WHERE v.rettighetkode = 'BASI'
        AND v.vedtaktypekode IN ('O', 'E', 'G', 'S')
        AND v.utfallkode NOT IN ('AVBRUTT', 'NEI')
        AND v.vedtakstatuskode IN ('IVERK', 'AVSLU')
        AND v.fra_dato IS NOT NULL
        AND v.fra_dato <= NVL(v.til_dato, v.fra_dato)
        AND (v.til_dato IS NULL OR v.til_dato > :dato)
        ORDER BY v.sak_id, v.lopenrvedtak, v.vedtak_id
        """.trimIndent()
}
