package no.nav.tiltakspenger.arena.routes

import kotlinx.coroutines.test.runTest
import no.nav.tiltakspenger.arena.db.OracleTestbase
import no.nav.tiltakspenger.arena.repository.ArenaTestdata
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 * Full-vertikal route-test for GET /azure/tiltakspenger/utbetalingshistorikk/detaljer (query-params vedtakId + meldekortId).
 * Henter vedtakfakta for vedtaket og anmerkninger for meldekortet.
 * Testdata i 95xx-serien.
 */
class UtbetalingshistorikkDetaljerRouteTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            OracleTestbase.start()
        }
    }

    private val uri = "/azure/tiltakspenger/utbetalingshistorikk/detaljer"

    @Test
    fun `henter vedtakfakta og anmerkninger`() = runTest {
        ArenaTestdata.leggTilVedtakfakta(vedtakId = 95001, kode = "DAGS", verdi = "285")
        ArenaTestdata.leggTilVedtakfakta(vedtakId = 95001, kode = "ANTALL", verdi = "2")
        ArenaTestdata.leggTilVedtakfakta(vedtakId = 95001, kode = "BEL", verdi = "1995")
        ArenaTestdata.leggTilVedtakfakta(vedtakId = 95001, kode = "FDATO", verdi = "01-03-2021")
        ArenaTestdata.leggTilVedtakfakta(vedtakId = 95001, kode = "TDATO", verdi = "14-03-2021")
        ArenaTestdata.leggTilAnmerkning(anmerkningId = 950021, objektId = 95002, vedtakId = 95001)

        medArenaRouteTest {
            getAutentisert("$uri?vedtakId=95001&meldekortId=95002").skalHaOkMedJson(
                forventetUtbetalingshistorikkDetaljerJson(
                    anmerkninger = listOf(forventetAnmerkningJson()),
                ),
            )
        }
    }

    @Test
    fun `uten query-params gir tomt svar`() = runTest {
        medArenaRouteTest {
            getAutentisert(uri).skalHaOkMedJson(
                """{ "vedtakfakta": null, "anmerkninger": [] }""",
            )
        }
    }
}
