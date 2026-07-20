package no.nav.tiltakspenger.arena.routes

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.LocalDate

class VedtakRequestTest {

    // Enhetstest fordi: maskeringen er kun observerbar når objektet logges, ikke i noe API-svar.
    @Test
    fun `toString maskerer ident`() {
        VedtakRequest(ident = "12345678910", fom = LocalDate.of(2023, 1, 1), tom = null).toString() shouldBe
            "VedtakRequest(ident=***********, fom=2023-01-01, tom=null)"
    }

    // Enhetstest fordi: en request med utelatt fom/tom gir samme route-respons som eksplisitt min/maks-dato — defaultingen er ikke skillbar gjennom et route-kall.
    @Test
    fun `utelatt fom og tom tolkes som ubegrenset periode`() {
        val request = VedtakRequest(ident = "12345678910", fom = null, tom = null)

        request.fomEllerMin shouldBe LocalDate.of(1900, 1, 1)
        request.tomEllerMaks shouldBe LocalDate.of(2999, 12, 31)
    }
}
