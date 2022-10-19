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
    fun `finner rett del av en tekst som har en skråstrek som del av rettighetsnavnet`() {
        // given
        val rettighetsnavn = "Dagp. v/perm fra fiskeindustri"

        // when
        val type = YtelseVedtakDTO.YtelseVedtakVedtakstype.fromNavn(rettighetsnavn)

        // then
        assertEquals(YtelseVedtakDTO.YtelseVedtakVedtakstype.FISK, type)
    }

    @Test
    fun `finner rett del av en tekst som ikke har en skråstrek`() {
        // given
        val rettighetsnavn = "Tiltaksplass"

        // when
        val type = YtelseVedtakDTO.YtelseVedtakVedtakstype.fromNavn(rettighetsnavn)

        // then
        assertEquals(YtelseVedtakDTO.YtelseVedtakVedtakstype.TILTAK, type)
    }

    @Test
    fun `finner rett del av en tekst som har en skråstrek som ikke er en del av rettighetsnavnet`() {
        // given
        val rettighetsnavn = "Tiltaksplass / Endring"

        // when
        val type = YtelseVedtakDTO.YtelseVedtakVedtakstype.fromNavn(rettighetsnavn)

        // then
        assertEquals(YtelseVedtakDTO.YtelseVedtakVedtakstype.TILTAK, type)
    }
}
