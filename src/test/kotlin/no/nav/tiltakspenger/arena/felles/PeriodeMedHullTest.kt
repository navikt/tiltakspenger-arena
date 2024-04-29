package no.nav.tiltakspenger.arena.felles

import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.shouldBe
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

    @Test
    fun testTomPeriode() {
        val periode = Periode(1.oktober(2023), 10.oktober(2023))
        val periodeMedVerdier1 = PeriodeMedVerdier<TomEllerNoe>(
            totalePeriode = periode,
            defaultVerdi = Tom,
        )
        periodeMedVerdier1.perioder().size shouldBe 1
        val periodeMedVerdier2 = periodeMedVerdier1.setVerdiForDelPeriode(
            Tom,
            Periode(1.oktober(2023), 5.oktober(2023)),
        )
        periodeMedVerdier2.perioder().size shouldBe 1

        val periodeMedVerdier3 = periodeMedVerdier2.setVerdiForDelPeriode(
            Noe("foobar"),
            Periode(3.oktober(2023), 5.oktober(2023)),
        )
        periodeMedVerdier3.perioder().size shouldBe 3

        periodeMedVerdier1.erTom() shouldBe true
    }
}

private fun PeriodeMedVerdier<TomEllerNoe>.erTom(): Boolean =
    this.perioder().all { it.verdi == Tom }
