package no.nav.tiltakspenger.arena.routes

import io.kotest.assertions.json.shouldEqualJson
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.test.runTest
import no.nav.tiltakspenger.arena.db.OracleTestbase
import no.nav.tiltakspenger.arena.repository.ArenaTestdata
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 * Full-vertikal route-test for POST /azure/tiltakspenger/vedtaksperioder: HTTP → prod ktor-pipeline
 * → VedtakDetaljerService → SakRepository → Oracle-testcontainer → JSON. Asserter på JSON-teksten
 * (ikke via DTO), slik at en DTO-refaktorering brekker testene.
 *
 * Testene deler mest mulig testdata (standard tiltakspengevedtak via [ArenaTestdata]s defaults) og
 * overstyrer kun identiteten (fnr/sak/vedtak-id) og det den enkelte testen faktisk verifiserer.
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
                forventetPerioderJson(
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
                forventetPerioderJson(
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
                forventetPerioderJson(
                    forventetVedtaksperiodeJson(vedtakId = 90211, sakId = 9021, saksnummer = "20239021", tilOgMed = null),
                ),
            )
        }
    }

    @Test
    fun `person uten vedtak gir tom liste`() = runTest {
        medArenaRouteTest {
            postAutentisert(uri, vedtakRequestBody("90000009999")).bodyAsText() shouldEqualJson "[]"
        }
    }
}
