package no.nav.tiltakspenger.arena

import io.ktor.server.testing.ApplicationTestBuilder
import io.mockk.mockk
import no.nav.tiltakspenger.arena.service.meldekort.MeldekortService
import no.nav.tiltakspenger.arena.service.utbetalingshistorikk.UtbetalingshistorikkService
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.RettighetDetaljerService
import no.nav.tiltakspenger.arena.service.vedtakdetaljer.VedtakDetaljerService
import no.nav.tiltakspenger.libs.ktor.common.oppstart.Readiness
import no.nav.tiltakspenger.libs.texas.client.TexasHttpClient

/**
 * Spinner opp appen i `testApplication` via prod-modulen [tiltakApi], slik at testene treffer den
 * faktiske ktor-pipelinen (content negotiation, auth, routing, health) og ikke en duplisert kopi.
 * Servicene defaulter til mockk; send inn ekte instanser for full-vertikale route-tester (se
 * `routes/RouteTestUtils.kt`).
 */
fun ApplicationTestBuilder.configureTestApplication(
    texasClient: TexasHttpClient = mockk(),
    vedtakDetaljerService: VedtakDetaljerService = mockk(),
    rettighetDetaljerService: RettighetDetaljerService = mockk(),
    meldekortService: MeldekortService = mockk(),
    utbetalingshistorikkService: UtbetalingshistorikkService = mockk(),
) {
    application {
        tiltakApi(
            vedtakDetaljerService = vedtakDetaljerService,
            rettighetDetaljerService = rettighetDetaljerService,
            meldekortService = meldekortService,
            utbetalingshistorikkService = utbetalingshistorikkService,
            texasClient = texasClient,
            readiness = Readiness(),
        )
    }
}
