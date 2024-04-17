package no.nav.tiltakspenger.arena.felles

import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import org.junit.jupiter.api.Test

sealed interface TomEllerNoe
data object Tom : TomEllerNoe
data class Noe<T>(
    val noe: T,
) : TomEllerNoe

class PeriodeMedHullTest {

    @Test
    fun testTomEllerNoe() {
        val tom: TomEllerNoe = Tom
        val noe: TomEllerNoe = Noe("foobar")
        val noeLikt: Noe<String> = Noe("foobar")

        tom shouldNotBeEqual noe
        tom shouldBeEqual Tom
        noe shouldBeEqual noeLikt
    }
}
