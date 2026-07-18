package no.nav.tiltakspenger.arena.routes

import kotlinx.coroutines.test.runTest
import no.nav.tiltakspenger.arena.db.OracleTestbase
import no.nav.tiltakspenger.arena.repository.ArenaTestdata
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 * Full-vertikal route-test for POST /azure/tiltakspenger/utbetalingshistorikk.
 * Utbetalingshistorikken settes sammen av flere kilder; her dekkes postering-innslaget (den vanligste).
 * Testdata i 94xx.
 */
class UtbetalingshistorikkRouteTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            OracleTestbase.start()
        }
    }

    private val uri = "/azure/tiltakspenger/utbetalingshistorikk"

    @Test
    fun `henter utbetalingshistorikk fra postering`() = runTest {
        ArenaTestdata.leggTilPerson(personId = 9400, fnr = "94000000000")
        ArenaTestdata.leggTilPostering(personId = 9400, vedtakId = 940011, meldekortId = 94001)

        medArenaRouteTest {
            postAutentisert(uri, vedtakRequestBody("94000000000")).skalHaOkMedJson(
                jsonArray(forventetUtbetalingshistorikkJson(meldekortId = 94001, vedtakId = 940011)),
            )
        }
    }

    @Test
    fun `person uten utbetalingshistorikk gir tom liste`() = runTest {
        medArenaRouteTest {
            postAutentisert(uri, vedtakRequestBody("94000009999")).skalHaOkMedJson("[]")
        }
    }
}
