package no.nav.tiltakspenger.arena.repository.vedtakfakta

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class ArenaVedtakfaktaDTOTest {

    private fun vedtakfakta(kode: ArenaVedtakFakta, verdi: String?) =
        ArenaVedtakfaktaDTO(
            vedtakId = 1L,
            vedtakfaktaKode = kode.name,
            vedtakfaktaVerdi = verdi,
        )

    @Test
    fun `heltallsfelter parses som heltall`() {
        val dto = listOf(
            vedtakfakta(ArenaVedtakFakta.DAGS, "285"),
            vedtakfakta(ArenaVedtakFakta.BARNMSTON, "2"),
        ).toArenaBarnetilleggVedtakfaktaDTO()

        dto.dagsats shouldBe 285
        dto.antallBarn shouldBe 2
    }

    @Test
    fun `heltallsfelter med desimalverdi rundes av til nærmeste heltall`() {
        val dto = listOf(
            vedtakfakta(ArenaVedtakFakta.DAGS, "284.5"),
            vedtakfakta(ArenaVedtakFakta.BARNMSTON, "0.961538461538462"),
        ).toArenaBarnetilleggVedtakfaktaDTO()

        dto.dagsats shouldBe 285
        dto.antallBarn shouldBe 1
    }

    @Test
    fun `desimalverdi under en halv rundes ned`() {
        val dto = listOf(
            vedtakfakta(ArenaVedtakFakta.BARNMSTON, "0.4"),
        ).toArenaBarnetilleggVedtakfaktaDTO()

        dto.antallBarn shouldBe 0
    }

    @Test
    fun `manglende felter gir null`() {
        val dto = emptyList<ArenaVedtakfaktaDTO>().toArenaBarnetilleggVedtakfaktaDTO()

        dto.dagsats shouldBe null
        dto.antallBarn shouldBe null
    }
}
