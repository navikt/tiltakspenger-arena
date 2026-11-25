package no.nav.tiltakspenger.arena.routes

import io.kotest.matchers.shouldBe
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import no.nav.tiltakspenger.arena.db.OracleTestbase
import no.nav.tiltakspenger.arena.repository.ArenaTestdata
import no.nav.tiltakspenger.arena.repository.statistikk.StatistikkRepository
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.YearMonth

/**
 * Full-vertikal route-test for GET /azure/tiltakspenger/statistikk/per-maned, /saker-med-rett-etter og /utbetalinger-per-maned.
 * Testen av per-maned bruker 2010, testen av saker-med-rett-etter bruker 2012 og testen av utbetalinger bruker 2014, som ingen andre tester har vedtak eller posteringer i, siden databasen deles på tvers av testene.
 */
class StatistikkRouteTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            OracleTestbase.start()
        }
    }

    @Test
    fun `gir saker med rett og vedtak per type per måned`() = runTest {
        ArenaTestdata.person(personId = 9600, fnr = "96800000000").medSak(sakId = 9601)
        // Sak 9601: ny rettighet fattet i februar, stans fattet i mars med virkning fra april, gjenopptak fattet i august og endring fattet i oktober.
        vedtak(96011, 9601, 9600, "O", 1, LocalDate.of(2010, 2, 10), LocalDate.of(2010, 6, 30), regDato = LocalDate.of(2010, 2, 5), innvf = "08-02-2010")
        vedtak(96012, 9601, 9600, "S", 2, LocalDate.of(2010, 4, 1), null, regDato = LocalDate.of(2010, 3, 20))
        vedtak(96013, 9601, 9600, "G", 3, LocalDate.of(2010, 9, 1), LocalDate.of(2010, 10, 31), regDato = LocalDate.of(2010, 8, 25))
        vedtak(96014, 9601, 9600, "E", 4, LocalDate.of(2010, 10, 1), LocalDate.of(2010, 11, 15), regDato = LocalDate.of(2010, 10, 3))
        // Vedtaksperioden ligger i 2011, så vedtaket gir ingen rett i 2010.
        vedtak(96015, 9601, 9600, "E", 5, LocalDate.of(2011, 2, 1), LocalDate.of(2011, 3, 1), regDato = LocalDate.of(2010, 12, 20), innvf = "05-01-2011")

        ArenaTestdata.person(personId = 9610, fnr = "96800000001").medSak(sakId = 9611)
        // Sak 9611: vedtak fattet i 2009 gir rett i januar og fra midten av desember; avslaget i juni telles ikke.
        vedtak(96111, 9611, 9610, "O", 1, LocalDate.of(2009, 11, 1), LocalDate.of(2010, 1, 15), regDato = LocalDate.of(2009, 10, 1))
        vedtak(96112, 9611, 9610, "O", 2, LocalDate.of(2010, 12, 15), null, regDato = LocalDate.of(2009, 12, 1), innvf = "10-12-2009")
        ArenaTestdata.leggTilVedtak(
            vedtakId = 96113,
            sakId = 9611,
            personId = 9610,
            utfallkode = "NEI",
            fraDato = LocalDate.of(2010, 6, 1),
            tilDato = LocalDate.of(2010, 6, 30),
            regDato = LocalDate.of(2010, 6, 1),
            lopenrVedtak = 3,
        )

        val sakerMedRett = listOf(1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1)
        val vedtakPerMåned = mapOf(2 to "O", 3 to "S", 8 to "G", 10 to "E")
        medArenaRouteTest {
            getAutentisert("/azure/tiltakspenger/statistikk/per-maned?aar=2010").skalHaOkMedJson(
                jsonArray(
                    *sakerMedRett.mapIndexed { i, antall ->
                        val type = vedtakPerMåned[i + 1]
                        fun antall(kode: String) = if (type == kode) 1 else 0
                        """
                        {
                            "måned": "2010-${(i + 1).toString().padStart(2, '0')}",
                            "sakerMedRett": $antall,
                            "nyeRettigheter": ${antall("O")},
                            "gjenopptak": ${antall("G")},
                            "endringer": ${antall("E")},
                            "stans": ${antall("S")}
                        }
                        """.trimIndent()
                    }.toTypedArray(),
                ),
            )
        }
    }

    @Test
    fun `manglende år gir 400`() = runTest {
        medArenaRouteTest {
            getAutentisert("/azure/tiltakspenger/statistikk/per-maned").status shouldBe HttpStatusCode.BadRequest
        }
    }

    @Test
    fun `gir saker og personer med rett etter datoen`() = runTest {
        val dato = LocalDate.of(2012, 6, 30)
        // Andre tester har vedtak uten sluttdato, som gir rett etter alle datoer, så testen teller bare det den selv legger til.
        val før = StatistikkRepository().hentSakerMedRettEtter(dato)

        val personMedToSaker = ArenaTestdata.person(personId = 9620, fnr = "96800000020")
        // Sak 9621: rett hele året.
        personMedToSaker.medSak(sakId = 9621)
        vedtak(96211, 9621, 9620, "O", 1, LocalDate.of(2012, 1, 1), LocalDate.of(2012, 12, 31), regDato = LocalDate.of(2012, 1, 1))
        // Sak 9622: samme person, rett fra september uten sluttdato.
        personMedToSaker.medSak(sakId = 9622)
        vedtak(96221, 9622, 9620, "O", 1, LocalDate.of(2012, 9, 1), null, regDato = LocalDate.of(2012, 8, 1))

        // Sak 9631: stans fra mai fjerner retten resten av året.
        ArenaTestdata.person(personId = 9630, fnr = "96800000030").medSak(sakId = 9631)
        vedtak(96311, 9631, 9630, "O", 1, LocalDate.of(2012, 1, 1), LocalDate.of(2012, 12, 31), regDato = LocalDate.of(2012, 1, 1))
        vedtak(96312, 9631, 9630, "S", 2, LocalDate.of(2012, 5, 1), null, regDato = LocalDate.of(2012, 4, 20))

        // Sak 9641: retten slutter på datoen.
        ArenaTestdata.person(personId = 9640, fnr = "96800000040").medSak(sakId = 9641)
        vedtak(96411, 9641, 9640, "O", 1, LocalDate.of(2012, 1, 1), LocalDate.of(2012, 6, 30), regDato = LocalDate.of(2012, 1, 1))

        // Sak 9651: gjenopptak i august etter en stans.
        ArenaTestdata.person(personId = 9650, fnr = "96800000050").medSak(sakId = 9651)
        vedtak(96511, 9651, 9650, "S", 1, LocalDate.of(2012, 3, 1), null, regDato = LocalDate.of(2012, 2, 20))
        vedtak(96512, 9651, 9650, "G", 2, LocalDate.of(2012, 8, 1), LocalDate.of(2012, 8, 31), regDato = LocalDate.of(2012, 7, 20))

        // Sak 9661: retten slutter i januar året etter.
        ArenaTestdata.person(personId = 9660, fnr = "96800000060").medSak(sakId = 9661)
        vedtak(96611, 9661, 9660, "O", 1, LocalDate.of(2012, 6, 1), LocalDate.of(2013, 1, 31), regDato = LocalDate.of(2012, 5, 20))

        val sakerPerSluttmåned = før.sakerPerSluttmåned.associate { it.sluttmåned to it.saker }.toMutableMap()
        listOf("2012-12", null, "2012-08", "2013-01").forEach { sakerPerSluttmåned.merge(it, 1, Int::plus) }
        val sortert = sakerPerSluttmåned.entries.sortedWith(compareBy(nullsLast()) { it.key?.let(YearMonth::parse) })
        sortert.sumOf { it.value } shouldBe før.sakerMedRett + 4

        medArenaRouteTest {
            getAutentisert("/azure/tiltakspenger/statistikk/saker-med-rett-etter?dato=2012-06-30").skalHaOkMedJson(
                """
                {
                    "dato": "2012-06-30",
                    "sakerMedRett": ${før.sakerMedRett + 4},
                    "personerMedRett": ${før.personerMedRett + 3},
                    "sakerPerSluttmåned": ${jsonArray(*sortert.map { """{ "sluttmåned": ${jsonTekstEllerNull(it.key)}, "saker": ${it.value} }""" }.toTypedArray())}
                }
                """.trimIndent(),
            )
        }
    }

    @Test
    fun `manglende eller ugyldig dato gir 400`() = runTest {
        medArenaRouteTest {
            getAutentisert("/azure/tiltakspenger/statistikk/saker-med-rett-etter").status shouldBe HttpStatusCode.BadRequest
            getAutentisert("/azure/tiltakspenger/statistikk/saker-med-rett-etter?dato=30.06.2012").status shouldBe HttpStatusCode.BadRequest
        }
    }

    @Test
    fun `gir utbetalinger per måned fordelt etter hverdager`() = runTest {
        // Person 9700: grunnsats for 3.–16. mars og barnetillegg for samme meldeperiode.
        ArenaTestdata.person(personId = 9700, fnr = "97800000000").medSak(sakId = 9701)
        ArenaTestdata.leggTilVedtak(vedtakId = 97011, sakId = 9701, personId = 9700, fraDato = LocalDate.of(2014, 1, 1), tilDato = LocalDate.of(2014, 12, 31))
        ArenaTestdata.leggTilVedtak(vedtakId = 97012, sakId = 9701, personId = 9700, rettighetkode = "BTIL", fraDato = LocalDate.of(2014, 1, 1), tilDato = LocalDate.of(2014, 12, 31))
        postering(9700, 97011, 2850.0, 10.0, LocalDate.of(2014, 3, 3), LocalDate.of(2014, 3, 16), posteringId = 97000001)
        postering(9700, 97012, 500.0, 10.0, LocalDate.of(2014, 3, 3), LocalDate.of(2014, 3, 16))
        // Utbetalingsgrunnlaget er allerede overført til posteringen over, så det telles ikke.
        utbetalingsgrunnlag(9700, 97011, 2850.0, LocalDate.of(2014, 3, 3), LocalDate.of(2014, 3, 16), posteringId = 97000001)

        // Person 9710: meldeperioden 21. april–4. mai har åtte hverdager i april og to i mai.
        ArenaTestdata.person(personId = 9710, fnr = "97800000010").medSak(sakId = 9711)
        ArenaTestdata.leggTilVedtak(vedtakId = 97111, sakId = 9711, personId = 9710, fraDato = LocalDate.of(2014, 1, 1), tilDato = LocalDate.of(2014, 12, 31))
        postering(9710, 97111, 1000.0, 10.0, LocalDate.of(2014, 4, 21), LocalDate.of(2014, 5, 4))

        // Person 9720: utbetalingen i juni er korrigert bort.
        ArenaTestdata.person(personId = 9720, fnr = "97800000020").medSak(sakId = 9721)
        ArenaTestdata.leggTilVedtak(vedtakId = 97211, sakId = 9721, personId = 9720, fraDato = LocalDate.of(2014, 1, 1), tilDato = LocalDate.of(2014, 12, 31))
        postering(9720, 97211, 2850.0, 10.0, LocalDate.of(2014, 6, 2), LocalDate.of(2014, 6, 15))
        postering(9720, 97211, -2850.0, -10.0, LocalDate.of(2014, 6, 2), LocalDate.of(2014, 6, 15))

        // Person 9730: spesialutbetaling i september på et eget vedtak som er relatert til grunnsatsvedtaket.
        ArenaTestdata.person(personId = 9730, fnr = "97800000030").medSak(sakId = 9731)
        ArenaTestdata.leggTilVedtak(vedtakId = 97311, sakId = 9731, personId = 9730, fraDato = LocalDate.of(2014, 1, 1), tilDato = LocalDate.of(2014, 12, 31))
        ArenaTestdata.leggTilVedtak(vedtakId = 97312, sakId = 9731, personId = 9730, rettighetkode = "SPESU", fraDato = LocalDate.of(2014, 9, 1), tilDato = LocalDate.of(2014, 9, 14), lopenrVedtak = 2, vedtakIdRelatert = 97311)
        postering(9730, 97312, 400.0, null, LocalDate.of(2014, 9, 1), LocalDate.of(2014, 9, 14), kilde = "SPESUTB")

        // Person 9740: beregnet i juli, men ikke overført til økonomisystemet.
        ArenaTestdata.person(personId = 9740, fnr = "97800000040").medSak(sakId = 9741)
        ArenaTestdata.leggTilVedtak(vedtakId = 97411, sakId = 9741, personId = 9740, fraDato = LocalDate.of(2014, 1, 1), tilDato = LocalDate.of(2014, 12, 31))
        utbetalingsgrunnlag(9740, 97411, 1425.0, LocalDate.of(2014, 7, 7), LocalDate.of(2014, 7, 20), posteringId = 97000099)

        // Person 9750: en annen rettighet enn grunnsats og barnetillegg telles ikke.
        ArenaTestdata.person(personId = 9750, fnr = "97800000050").medSak(sakId = 9751)
        ArenaTestdata.leggTilVedtak(vedtakId = 97511, sakId = 9751, personId = 9750, rettighetkode = "ANNEN", fraDato = LocalDate.of(2014, 1, 1), tilDato = LocalDate.of(2014, 12, 31))
        postering(9750, 97511, 999.0, 10.0, LocalDate.of(2014, 10, 6), LocalDate.of(2014, 10, 19))

        // Person 9760: meldeperioden har bare helgedager og går i sin helhet til august.
        ArenaTestdata.person(personId = 9760, fnr = "97800000060").medSak(sakId = 9761)
        ArenaTestdata.leggTilVedtak(vedtakId = 97611, sakId = 9761, personId = 9760, fraDato = LocalDate.of(2014, 1, 1), tilDato = LocalDate.of(2014, 12, 31))
        postering(9760, 97611, 100.0, 1.0, LocalDate.of(2014, 8, 30), LocalDate.of(2014, 8, 31))

        // Person 9770: meldeperioden krysser årsskiftet med tre hverdager i desember og sju i januar.
        ArenaTestdata.person(personId = 9770, fnr = "97800000070").medSak(sakId = 9771)
        ArenaTestdata.leggTilVedtak(vedtakId = 97711, sakId = 9771, personId = 9770, fraDato = LocalDate.of(2014, 1, 1), tilDato = LocalDate.of(2015, 1, 31))
        postering(9770, 97711, 1000.0, 10.0, LocalDate.of(2014, 12, 29), LocalDate.of(2015, 1, 11))

        val forventet = mapOf(
            3 to Forventet(personer = 1, dager = "10.00", grunnsats = "2850.00", barnetillegg = "500.00"),
            4 to Forventet(personer = 1, dager = "8.00", grunnsats = "800.00"),
            5 to Forventet(personer = 1, dager = "2.00", grunnsats = "200.00"),
            7 to Forventet(personerBeregnet = 1, beregnet = "1425.00"),
            8 to Forventet(personer = 1, dager = "1.00", grunnsats = "100.00"),
            9 to Forventet(personer = 1, grunnsats = "400.00"),
            12 to Forventet(personer = 1, dager = "3.00", grunnsats = "300.00"),
        )
        medArenaRouteTest {
            getAutentisert("/azure/tiltakspenger/statistikk/utbetalinger-per-maned?aar=2014").skalHaOkMedJson(
                jsonArray(
                    *(1..12).map { måned ->
                        val f = forventet[måned] ?: Forventet()
                        """
                        {
                            "måned": "2014-${måned.toString().padStart(2, '0')}",
                            "personerMedUtbetaling": ${f.personer},
                            "dagerUtbetalt": ${f.dager},
                            "beløpGrunnsats": ${f.grunnsats},
                            "beløpBarnetillegg": ${f.barnetillegg},
                            "personerMedBeregnetIkkeUtbetalt": ${f.personerBeregnet},
                            "beløpBeregnetIkkeUtbetalt": ${f.beregnet}
                        }
                        """.trimIndent()
                    }.toTypedArray(),
                ),
            )
        }
    }

    @Test
    fun `manglende eller ugyldig år for utbetalinger gir 400`() = runTest {
        medArenaRouteTest {
            getAutentisert("/azure/tiltakspenger/statistikk/utbetalinger-per-maned").status shouldBe HttpStatusCode.BadRequest
            getAutentisert("/azure/tiltakspenger/statistikk/utbetalinger-per-maned?aar=tjue").status shouldBe HttpStatusCode.BadRequest
        }
    }

    private data class Forventet(
        val personer: Int = 0,
        val dager: String = "0.00",
        val grunnsats: String = "0.00",
        val barnetillegg: String = "0.00",
        val personerBeregnet: Int = 0,
        val beregnet: String = "0.00",
    )

    private fun postering(
        personId: Long,
        vedtakId: Long,
        beløp: Double,
        dager: Double?,
        fraOgMed: LocalDate,
        tilOgMed: LocalDate,
        posteringId: Long? = null,
        kilde: String? = null,
    ) {
        ArenaTestdata.leggTilPostering(
            personId = personId,
            vedtakId = vedtakId,
            meldekortId = null,
            belop = beløp,
            datoPostert = tilOgMed.plusDays(3),
            datoPeriodeFra = fraOgMed,
            datoPeriodeTil = tilOgMed,
            posteringId = posteringId,
            antall = dager,
            kilde = kilde,
        )
    }

    private fun utbetalingsgrunnlag(personId: Long, vedtakId: Long, beløp: Double, fraOgMed: LocalDate, tilOgMed: LocalDate, posteringId: Long) {
        ArenaTestdata.leggTilUtbetalingsgrunnlag(
            personId = personId,
            vedtakId = vedtakId,
            meldekortId = null,
            belop = beløp,
            modDato = tilOgMed.plusDays(2),
            datoPeriodeFra = fraOgMed,
            datoPeriodeTil = tilOgMed,
            posteringId = posteringId,
            antall = 5.0,
        )
    }

    private fun vedtak(
        vedtakId: Long,
        sakId: Long,
        personId: Long,
        vedtaktype: String,
        lopenrVedtak: Long,
        fraDato: LocalDate,
        tilDato: LocalDate?,
        regDato: LocalDate,
        innvf: String? = null,
    ) {
        ArenaTestdata.leggTilVedtak(
            vedtakId = vedtakId,
            sakId = sakId,
            personId = personId,
            vedtaktypekode = vedtaktype,
            fraDato = fraDato,
            tilDato = tilDato,
            regDato = regDato,
            lopenrVedtak = lopenrVedtak,
        )
        if (innvf != null) ArenaTestdata.leggTilVedtakfakta(vedtakId = vedtakId, kode = "INNVF", verdi = innvf)
    }
}
