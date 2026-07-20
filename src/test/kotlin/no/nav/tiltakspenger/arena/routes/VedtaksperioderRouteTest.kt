package no.nav.tiltakspenger.arena.routes

import kotlinx.coroutines.test.runTest
import no.nav.tiltakspenger.arena.db.OracleTestbase
import no.nav.tiltakspenger.arena.repository.ArenaTestdata
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.time.LocalDate

/**
 * Full-vertikal route-test for POST /azure/tiltakspenger/vedtaksperioder: HTTP → prod ktor-pipeline → VedtakDetaljerService → SakRepository → Oracle-testcontainer → JSON.
 * Asserter på JSON-teksten (ikke via DTO), slik at en DTO-refaktorering brekker testene.
 *
 * Testene deler mest mulig testdata (standard tiltakspengevedtak via [ArenaTestdata]s defaults) og overstyrer kun identiteten (fnr/sak/vedtak-id) og det den enkelte testen faktisk verifiserer.
 * Auth-avvisning (401) er dekket av [TiltakspengerRoutesAuthTest].
 */
class VedtaksperioderRouteTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            OracleTestbase.start()
        }
    }

    private val uri = "/azure/tiltakspenger/vedtaksperioder"

    @Test
    fun `henter standard tiltakspengevedtak`() = runTest {
        ArenaTestdata.person(personId = 900, fnr = "90000000000")
            .medSak(sakId = 9001)
            .medTiltakspengevedtak(vedtakId = 90011)

        medArenaRouteTest {
            postAutentisert(uri, vedtakRequestBody("90000000000")).skalHaOkMedJson(
                jsonArray(
                    forventetVedtaksperiodeJson(vedtakId = 90011, sakId = 9001, saksnummer = "20239001"),
                ),
            )
        }
    }

    @Test
    fun `desimalt antall barn rundes av hele veien ut til responsen`() = runTest {
        // Prod-casen som startet dette arbeidet: BARNMSTON = 0.961538461538462 skal bli antallBarn = 1.
        // Alt annet er et standard tiltakspengevedtak; kun barnetillegget er unikt her.
        ArenaTestdata.person(personId = 901, fnr = "90000000001")
            .medSak(sakId = 9011)
            .medTiltakspengevedtak(vedtakId = 90111)
            .medBarnetilleggvedtak(vedtakId = 90112, antallBarn = "0.961538461538462")

        medArenaRouteTest {
            postAutentisert(uri, vedtakRequestBody("90000000001")).skalHaOkMedJson(
                jsonArray(
                    forventetVedtaksperiodeJson(
                        vedtakId = 90111,
                        sakId = 9011,
                        saksnummer = "20239011",
                        rettighet = "TILTAKSPENGER_OG_BARNETILLEGG",
                        dagsatsBarnetillegg = 53,
                        antallBarn = 1,
                    ),
                ),
            )
        }
    }

    @Test
    fun `vedtak med aapen sluttdato gir tilOgMed null`() = runTest {
        ArenaTestdata.person(personId = 902, fnr = "90000000002")
            .medSak(sakId = 9021)
            .medTiltakspengevedtak(vedtakId = 90211, tilDato = null)

        medArenaRouteTest {
            postAutentisert(uri, vedtakRequestBody("90000000002")).skalHaOkMedJson(
                jsonArray(
                    forventetVedtaksperiodeJson(vedtakId = 90211, sakId = 9021, saksnummer = "20239021", tilOgMed = null),
                ),
            )
        }
    }

    @Test
    fun `barnetillegg utover vedtaksperioden klippes til overlappet`() = runTest {
        // Barnetillegget (mars-mai) stikker utenfor saksperioden (jan-mars); kun overlappet (mars) skal få barnetillegg.
        ArenaTestdata.person(personId = 903, fnr = "90000000003")
            .medSak(sakId = 9031)
            .medTiltakspengevedtak(vedtakId = 90311)
            .medBarnetilleggvedtak(vedtakId = 90312, antallBarn = "1", fraDato = LocalDate.of(2023, 3, 1), tilDato = LocalDate.of(2023, 5, 31))

        medArenaRouteTest {
            postAutentisert(uri, vedtakRequestBody("90000000003")).skalHaOkMedJson(
                jsonArray(
                    forventetVedtaksperiodeJson(
                        vedtakId = 90311,
                        sakId = 9031,
                        saksnummer = "20239031",
                        tilOgMed = "2023-02-28",
                    ),
                    forventetVedtaksperiodeJson(
                        vedtakId = 90311,
                        sakId = 9031,
                        saksnummer = "20239031",
                        fraOgMed = "2023-03-01",
                        rettighet = "TILTAKSPENGER_OG_BARNETILLEGG",
                        dagsatsBarnetillegg = 53,
                        antallBarn = 1,
                    ),
                ),
            )
        }
    }

    @Test
    fun `barnetillegg i gap mellom tiltakspengevedtak gir rettighet BARNETILLEGG`() = runTest {
        // To tiltakspengevedtak (jan og mars) med barnetillegg for hele jan-mars: gapet (februar) har bare barnetillegg, og responsen viser MVP-defaultene (vedtakId/sakId 0, tom sak) for gapet.
        ArenaTestdata.person(personId = 904, fnr = "90000000004")
            .medSak(sakId = 9041)
            .medTiltakspengevedtak(vedtakId = 90411, tilDato = LocalDate.of(2023, 1, 31))
            .medTiltakspengevedtak(vedtakId = 90412, fraDato = LocalDate.of(2023, 3, 1))
            .medBarnetilleggvedtak(vedtakId = 90413, antallBarn = "1")

        medArenaRouteTest {
            postAutentisert(uri, vedtakRequestBody("90000000004")).skalHaOkMedJson(
                jsonArray(
                    forventetVedtaksperiodeJson(
                        vedtakId = 90411,
                        sakId = 9041,
                        saksnummer = "20239041",
                        tilOgMed = "2023-01-31",
                        rettighet = "TILTAKSPENGER_OG_BARNETILLEGG",
                        dagsatsBarnetillegg = 53,
                        antallBarn = 1,
                    ),
                    forventetVedtaksperiodeJson(
                        vedtakId = 0,
                        sakId = 0,
                        saksnummer = "",
                        fraOgMed = "2023-02-01",
                        tilOgMed = "2023-02-28",
                        rettighet = "BARNETILLEGG",
                        antallDager = 0.0,
                        dagsatsTiltakspenger = 0,
                        dagsatsBarnetillegg = 53,
                        antallBarn = 1,
                        relaterteTiltak = "",
                        sakOpprettetDato = "-999999999-01-01",
                        sakStatus = "",
                    ),
                    forventetVedtaksperiodeJson(
                        vedtakId = 90412,
                        sakId = 9041,
                        saksnummer = "20239041",
                        fraOgMed = "2023-03-01",
                        rettighet = "TILTAKSPENGER_OG_BARNETILLEGG",
                        dagsatsBarnetillegg = 53,
                        antallBarn = 1,
                    ),
                ),
            )
        }
    }

    @Test
    fun `person uten vedtak gir tom liste`() = runTest {
        medArenaRouteTest {
            postAutentisert(uri, vedtakRequestBody("90000009999")).skalHaOkMedJson("[]")
        }
    }
}
