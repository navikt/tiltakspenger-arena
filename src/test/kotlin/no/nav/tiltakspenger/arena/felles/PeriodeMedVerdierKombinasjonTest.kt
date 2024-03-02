package no.nav.tiltakspenger.arena.felles

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class PeriodeMedVerdierKombinasjonTest {

    data class UtfallOgDagsatsOgAntallBarn(
        val utfall: Utfall,
        val dagsats: Long,
        val antallBarn: Int,
    ) {
        companion object {
            fun kombiner(
                dto: PeriodeMedVerdierAvUlikTypeTest.DagsatsOgAntallBarn,
                utfall: Utfall,
            ): UtfallOgDagsatsOgAntallBarn =
                UtfallOgDagsatsOgAntallBarn(utfall = utfall, dagsats = dto.dagsats, antallBarn = dto.antallBarn)
        }
    }

    @Test
    fun `eksempel på hvordan vi kan kombinere utfall og detaljer i en vedtaksperiode`() {
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
        val innvilgetPerioder: PeriodeMedVerdier<Utfall> =
            PeriodeMedVerdier.kombinerLikePerioderMedSammeTypeVerdier(alleVilkår, ::kombinerToUfall)

        val perioderMedDagsats =
            PeriodeMedVerdier(
                250L,
                Periode(1.oktober(2023), 10.oktober(2023)),
            )
                .setVerdiForDelPeriode(300L, Periode(7.oktober(2023), 10.oktober(2023)))

        val perioderMedAntallBarn =
            PeriodeMedVerdier(
                1,
                Periode(1.oktober(2023), 10.oktober(2023)),
            )

        val perioderMedDagsatsOgAntallBarn =
            perioderMedDagsats.kombiner(
                perioderMedAntallBarn,
                PeriodeMedVerdierAvUlikTypeTest.DagsatsOgAntallBarn::kombinerDagsatsOgAntallBarn,
            )

        val totaleVedtaksPerioder: PeriodeMedVerdier<UtfallOgDagsatsOgAntallBarn> =
            perioderMedDagsatsOgAntallBarn.kombiner(innvilgetPerioder, UtfallOgDagsatsOgAntallBarn::kombiner)

        totaleVedtaksPerioder.perioder().size shouldBe 4
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
