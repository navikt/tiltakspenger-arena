package no.nav.tiltakspenger.arena.routes

import kotlinx.coroutines.test.runTest
import no.nav.tiltakspenger.arena.db.OracleTestbase
import no.nav.tiltakspenger.arena.repository.ArenaTestdata
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

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
    fun `person uten vedtak gir tom liste`() = runTest {
        medArenaRouteTest {
            postAutentisert(uri, vedtakRequestBody("91000009999")).skalHaOkMedJson("[]")
        }
    }
}
