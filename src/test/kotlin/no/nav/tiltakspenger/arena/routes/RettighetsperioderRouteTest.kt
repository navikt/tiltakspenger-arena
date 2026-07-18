package no.nav.tiltakspenger.arena.routes

import kotlinx.coroutines.test.runTest
import no.nav.tiltakspenger.arena.db.OracleTestbase
import no.nav.tiltakspenger.arena.repository.ArenaTestdata
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 * Full-vertikal route-test for POST /azure/tiltakspenger/rettighetsperioder.
 * Responsen er kun perioden (fraOgMed/tilOgMed), filtrert til TILTAKSPENGER(_OG_BARNETILLEGG).
 * Testdata i 92xx-serien.
 */
class RettighetsperioderRouteTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            OracleTestbase.start()
        }
    }

    private val uri = "/azure/tiltakspenger/rettighetsperioder"

    @Test
    fun `henter rettighetsperiode for person med tiltakspengevedtak`() = runTest {
        ArenaTestdata.person(personId = 9200, fnr = "92000000000")
            .medSak(sakId = 92001)
            .medTiltakspengevedtak(vedtakId = 920011)

        medArenaRouteTest {
            postAutentisert(uri, vedtakRequestBody("92000000000")).skalHaOkMedJson(
                jsonArray(forventetRettighetsperiodeJson()),
            )
        }
    }

    @Test
    fun `person uten vedtak gir tom liste`() = runTest {
        medArenaRouteTest {
            postAutentisert(uri, vedtakRequestBody("92000009999")).skalHaOkMedJson("[]")
        }
    }
}
