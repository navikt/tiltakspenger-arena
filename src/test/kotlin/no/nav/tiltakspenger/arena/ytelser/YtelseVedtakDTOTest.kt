package no.nav.tiltakspenger.arena.ytelser

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class YtelseVedtakDTOTest {

    @Test
    fun `finner rett del av en tekst som har to skråstreker`() {
        // given
        val rettighetsnavn = "Dagp. v/perm fra fiskeindustri / Endring"

        // when
        val type = YtelseVedtakDTO.YtelseVedtakVedtakstype.fromNavn(rettighetsnavn)

        // then
        assertEquals(YtelseVedtakDTO.YtelseVedtakVedtakstype.FISK, type)
    }

    @Test
    fun `finner rett del av en tekst som har en skråstrek`() {
        // given
        val rettighetsnavn = "Tiltaksplass / Endring"

        // when
        val type = YtelseVedtakDTO.YtelseVedtakVedtakstype.fromNavn(rettighetsnavn)

        // then
        assertEquals(YtelseVedtakDTO.YtelseVedtakVedtakstype.TILTAK, type)
    }
}
