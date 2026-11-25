package no.nav.tiltakspenger.arena.db

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.Instant

class DatasourceTest {
    private val start = Instant.parse("2026-11-25T12:00:00Z")

    @Test
    fun `gir sekundene som er igjen av tidsgrensen, og minst ett sekund`() {
        gjenståendeSpørretidSekunder(start, start) shouldBe 170
        gjenståendeSpørretidSekunder(start, start.plusMillis(100_400)) shouldBe 69
        gjenståendeSpørretidSekunder(start, start.plusMillis(169_500)) shouldBe 1
    }

    @Test
    fun `kaster feil når tidsgrensen er overskredet`() {
        shouldThrow<IllegalStateException> { gjenståendeSpørretidSekunder(start, start.plusSeconds(170)) }
        shouldThrow<IllegalStateException> { gjenståendeSpørretidSekunder(start, start.plusSeconds(200)) }
    }
}
