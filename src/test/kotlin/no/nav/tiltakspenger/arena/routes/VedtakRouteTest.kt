package no.nav.tiltakspenger.arena.routes

import kotlinx.coroutines.test.runTest
import no.nav.tiltakspenger.arena.db.OracleTestbase
import no.nav.tiltakspenger.arena.repository.ArenaTestdata
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.time.LocalDate

/**
 * Full-vertikal route-test for POST /azure/tiltakspenger/vedtak.
 * Samme respons-DTO som /vedtaksperioder, men uten rettighetsfilter.
 * Asserter på JSON-teksten.
 * Testdata i 91xx-serien.
 */
class VedtakRouteTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            OracleTestbase.start()
        }
    }

    private val uri = "/azure/tiltakspenger/vedtak"

    @Test
    fun `henter vedtak for person med tiltakspengevedtak`() = runTest {
        ArenaTestdata.person(personId = 9100, fnr = "91000000000")
            .medSak(sakId = 91001)
            .medTiltakspengevedtak(vedtakId = 910011)

        medArenaRouteTest {
            postAutentisert(uri, vedtakRequestBody("91000000000")).skalHaOkMedJson(
                jsonArray(
                    forventetVedtaksperiodeJson(vedtakId = 910011, sakId = 91001, saksnummer = "202391001"),
                ),
            )
        }
    }

    @Test
    fun `gap mellom to tiltakspengevedtak gir INGENTING-periode`() = runTest {
        // /vedtak er ufiltrert, så gapet (februar) mellom vedtakene kommer med som INGENTING med MVP-defaultene (vedtakId/sakId 0, tom sak).
        // /vedtaksperioder filtrerer bort slike.
        ArenaTestdata.person(personId = 9101, fnr = "91000000001")
            .medSak(sakId = 91011)
            .medTiltakspengevedtak(vedtakId = 910111, tilDato = LocalDate.of(2023, 1, 31))
            .medTiltakspengevedtak(vedtakId = 910112, fraDato = LocalDate.of(2023, 3, 1))

        medArenaRouteTest {
            postAutentisert(uri, vedtakRequestBody("91000000001")).skalHaOkMedJson(
                jsonArray(
                    forventetVedtaksperiodeJson(
                        vedtakId = 910111,
                        sakId = 91011,
                        saksnummer = "202391011",
                        tilOgMed = "2023-01-31",
                    ),
                    forventetVedtaksperiodeJson(
                        vedtakId = 0,
                        sakId = 0,
                        saksnummer = "",
                        fraOgMed = "2023-02-01",
                        tilOgMed = "2023-02-28",
                        rettighet = "INGENTING",
                        antallDager = 0.0,
                        dagsatsTiltakspenger = 0,
                        relaterteTiltak = "",
                        sakOpprettetDato = "-999999999-01-01",
                        sakStatus = "",
                    ),
                    forventetVedtaksperiodeJson(
                        vedtakId = 910112,
                        sakId = 91011,
                        saksnummer = "202391011",
                        fraOgMed = "2023-03-01",
                    ),
                ),
            )
        }
    }

    @Test
    fun `person uten vedtak gir tom liste`() = runTest {
        medArenaRouteTest {
            postAutentisert(uri, vedtakRequestBody("91000009999")).skalHaOkMedJson("[]")
        }
    }
}
