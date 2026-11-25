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

/**
 * Full-vertikal route-test for GET /azure/tiltakspenger/statistikk/per-maned og /saker-med-rett-etter.
 * Testen av per-maned bruker 2010 og testen av saker-med-rett-etter bruker 2012, som ingen andre tester har vedtak i, siden databasen deles på tvers av testene.
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

        medArenaRouteTest {
            getAutentisert("/azure/tiltakspenger/statistikk/saker-med-rett-etter?dato=2012-06-30").skalHaOkMedJson(
                """
                {
                    "dato": "2012-06-30",
                    "sakerMedRett": ${før.sakerMedRett + 3},
                    "personerMedRett": ${før.personerMedRett + 2}
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
