package no.nav.tiltakspenger.arena.ytelser

import io.mockk.every
import io.mockk.mockk
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.YtelseskontraktV3
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.meldinger.HentYtelseskontraktListeResponse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class ArenaSoapServiceTest {

    @Test
    fun getYtelser() {
        // given
        val ident = "1"
        val fom = LocalDate.of(2022, 6, 1)
        val tom = LocalDate.of(2022, 6, 20)
        val kontrakt = mockk<YtelseskontraktV3>()
        every {
            kontrakt.hentYtelseskontraktListe(
                match {
                    it.personidentifikator == ident && it.periode.fom == fom && it.periode.tom == tom
                },
            )
        } returns HentYtelseskontraktListeResponse()
        val service = ArenaSoapService(kontrakt)

        // when
        val ytelser = service.getYtelser(ident, fom, tom)

        // then
        assertNotNull(ytelser)
    }
}
