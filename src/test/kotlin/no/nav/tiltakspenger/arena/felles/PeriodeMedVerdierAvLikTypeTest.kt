package no.nav.tiltakspenger.arena.felles

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

enum class Utfall {
    OPPFYLT,
    IKKE_OPPFYLT,
    KREVER_MANUELL_VURDERING,
}

class PeriodeMedVerdierAvLikTypeTest {
    @Test
    fun `en oppfylt periode kombinert med en delvis oppfylt periode skal gi en delvis oppfylt periode`() {
        val aap =
            PeriodeMedVerdier(
                Utfall.IKKE_OPPFYLT,
                Periode(1.oktober(2023), 10.oktober(2023)),
            )
                .setVerdiForDelPeriode(Utfall.OPPFYLT, Periode(6.oktober(2023), 10.oktober(2023)))

        val dagpenger =
            PeriodeMedVerdier(
                Utfall.OPPFYLT,
                Periode(1.oktober(2023), 10.oktober(2023)),
            )

        val vedtak: PeriodeMedVerdier<Utfall> = aap.kombiner(dagpenger, ::kombinerToUfall)
        vedtak.perioder().size shouldBe 2

        vedtak.perioder().count { it.verdi == Utfall.OPPFYLT } shouldBe 1
        vedtak.perioder()
            .find { it.verdi == Utfall.OPPFYLT }!!.periode shouldBe Periode(6.oktober(2023), 10.oktober(2023))

        vedtak.perioder().count { it.verdi == Utfall.IKKE_OPPFYLT } shouldBe 1
        vedtak.perioder()
            .find { it.verdi == Utfall.IKKE_OPPFYLT }!!.periode shouldBe Periode(1.oktober(2023), 5.oktober(2023))
    }

    @Test
    fun `å kombinere perioder med like type verdier (utfall) skal gi en periode med subperioder med riktige utfall`() {
        val aap =
            PeriodeMedVerdier(
                Utfall.OPPFYLT,
                Periode(1.oktober(2023), 10.oktober(2023)),
            )
                .setVerdiForDelPeriode(Utfall.IKKE_OPPFYLT, Periode(6.oktober(2023), 10.oktober(2023)))

        val fengsel =
            PeriodeMedVerdier(
                Utfall.OPPFYLT,
                Periode(1.oktober(2023), 10.oktober(2023)),
            )
                .setVerdiForDelPeriode(Utfall.IKKE_OPPFYLT, Periode(1.oktober(2023), 2.oktober(2023)))

        val jobbsjansen =
            PeriodeMedVerdier(
                Utfall.OPPFYLT,
                Periode(1.oktober(2023), 10.oktober(2023)),
            )
                .setVerdiForDelPeriode(Utfall.IKKE_OPPFYLT, Periode(5.oktober(2023), 7.oktober(2023)))

        val dagpenger =
            PeriodeMedVerdier(
                Utfall.OPPFYLT,
                Periode(1.oktober(2023), 10.oktober(2023)),
            )

        val alleVilkår = listOf(aap, dagpenger, fengsel, jobbsjansen)

        val vedtak = PeriodeMedVerdier.kombinerLikePerioderMedSammeTypeVerdier(alleVilkår, ::kombinerToUfall)
        println(vedtak)
        vedtak.perioder().size shouldBe 3

        vedtak.perioder()
            .count { it.verdi == Utfall.OPPFYLT } shouldBe 1
        vedtak.perioder()
            .filter { it.verdi == Utfall.OPPFYLT }
            .map { it.periode } shouldContainExactly listOf(Periode(3.oktober(2023), 4.oktober(2023)))

        vedtak.perioder()
            .count { it.verdi == Utfall.IKKE_OPPFYLT } shouldBe 2
        vedtak.perioder()
            .filter { it.verdi == Utfall.IKKE_OPPFYLT }.map { it.periode } shouldContainExactly listOf(
            Periode(1.oktober(2023), 2.oktober(2023)),
            Periode(5.oktober(2023), 10.oktober(2023)),
        )
    }

    private fun kombinerToUfall(en: Utfall, to: Utfall): Utfall {
        if (en == Utfall.KREVER_MANUELL_VURDERING || to == Utfall.KREVER_MANUELL_VURDERING) {
            return Utfall.KREVER_MANUELL_VURDERING
        }
        if (en == Utfall.IKKE_OPPFYLT || to == Utfall.IKKE_OPPFYLT) {
            return Utfall.IKKE_OPPFYLT
        }
        return Utfall.OPPFYLT
    }
}
