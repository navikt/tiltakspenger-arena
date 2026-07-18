package no.nav.tiltakspenger.arena.routes

import kotlinx.coroutines.test.runTest
import no.nav.tiltakspenger.arena.db.OracleTestbase
import no.nav.tiltakspenger.arena.repository.ArenaTestdata
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.time.LocalDate

/**
 * Full-vertikal route-test for POST /azure/tiltakspenger/meldekort.
 * Asserter på JSON-teksten, inkludert nested periode og dager.
 * Egne testdata (93xx-serien); hver test bruker unik periodekode siden MELDEKORTPERIODE deler nøkkel (aar, periodekode) på tvers.
 */
class MeldekortRouteTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            OracleTestbase.start()
        }
    }

    private val uri = "/azure/tiltakspenger/meldekort"

    @Test
    fun `henter meldekort med dager`() = runTest {
        ArenaTestdata.person(personId = 9300, fnr = "93000000000")
            .medMeldekort(meldekortId = 93001, periodekode = "71", datoFra = LocalDate.of(2023, 1, 2), datoTil = LocalDate.of(2023, 1, 15))
            .medDag(ukenr = 1, dagnr = 1)
            .medDag(ukenr = 1, dagnr = 2, statusArbeidsdag = "J", timerArbeidet = 7.5)
            .medDag(ukenr = 1, dagnr = 3, statusKurs = "N", statusFerie = "J", statusSyk = "J", statusAnnetFravaer = "J")

        medArenaRouteTest {
            postAutentisert(uri, vedtakRequestBody("93000000000")).skalHaOkMedJson(
                jsonArray(
                    forventetMeldekortJson(
                        meldekortId = "93001",
                        periodekode = 71,
                        fraOgMed = "2023-01-02",
                        tilOgMed = "2023-01-15",
                        totaltArbeidetTimer = 7,
                        dager = listOf(
                            forventetMeldekortdagJson(ukeNr = 1, dagNr = 1),
                            forventetMeldekortdagJson(ukeNr = 1, dagNr = 2, arbeidsdag = true, arbeidetTimer = 7),
                            forventetMeldekortdagJson(ukeNr = 1, dagNr = 3, kurs = false, ferie = true, syk = true, annetFravaer = true),
                        ),
                    ),
                ),
            )
        }
    }

    @Test
    fun `person uten meldekort gir tom liste`() = runTest {
        medArenaRouteTest {
            postAutentisert(uri, vedtakRequestBody("93000009999")).skalHaOkMedJson("[]")
        }
    }
}
