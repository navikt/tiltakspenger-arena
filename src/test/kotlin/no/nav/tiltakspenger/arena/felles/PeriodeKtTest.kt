package no.nav.tiltakspenger.arena.felles

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate

class PeriodeKtTest {

    private val periode1 = Periode(fra = 13.mai(2022), til = 18.mai(2022))
    private val periode2 = Periode(fra = 17.mai(2022), til = 21.mai(2022))
    private val periode3 = Periode(fra = 19.mai(2022), til = 20.mai(2022))

    @Test
    fun inneholderHele() {
        assertTrue(periode1.inneholderHele(periode1))
        assertTrue(periode2.inneholderHele(periode3))
        assertFalse(periode1.inneholderHele(periode2))
    }

    @Test
    fun overlapperMed() {
        assertTrue(periode1.overlapperMed(periode1))
        assertTrue(periode1.overlapperMed(periode2))
        assertFalse(periode1.overlapperMed(periode3))
    }

    @Test
    fun intersect() {
        val fellesperiode = Periode(fra = 17.mai(2022), til = 18.mai(2022))
        assertEquals(periode1, periode1.overlappendePeriode(periode1))
        assertEquals(fellesperiode, periode1.overlappendePeriode(periode2))
        assertEquals(fellesperiode, periode2.overlappendePeriode(periode1))
        assertNotEquals(fellesperiode, periode2.overlappendePeriode(periode2))
    }

    @Test
    fun overlapperIkkeMed() {
        val periodeSomIkkeOverlapper = Periode(fra = 13.mai(2022), til = 16.mai(2022))
        assertEquals(periodeSomIkkeOverlapper, periode1.ikkeOverlappendePeriode(periode2).first())
    }

    @Test
    fun `to komplett overlappende perioder skal gi tomt svar`() {
        assertEquals(emptyList<Periode>(), periode1.ikkeOverlappendePeriode(periode1))
    }

    @Test
    fun `to overlappende perioder`() {
        val periodeEn = Periode(fra = 13.mai(2022), til = 16.mai(2022))
        val periodeTo = Periode(fra = 13.mai(2022), til = 15.mai(2022))
        val fasit = Periode(fra = 16.mai(2022), til = 16.mai(2022))
        assertEquals(fasit, periodeEn.ikkeOverlappendePeriode(periodeTo).first())
        assertEquals(1, periodeEn.ikkeOverlappendePeriode(periodeTo).size)
    }

    @Test
    fun `ikkeOverlappendePerioder fjerner overlapp mellom flere perioder --fengsel---kvp---`() {
        val periodeEn = Periode(fra = 1.mai(2022), til = 15.mai(2022))
        val fengselPeriode = Periode(fra = 5.mai(2022), til = 6.mai(2022))
        val kvpPeriode = Periode(fra = 11.mai(2022), til = 12.mai(2022))

        val result = periodeEn.ikkeOverlappendePerioder(
            listOf(
                fengselPeriode,
                kvpPeriode,
            ),
        )
        assertEquals(3, result.size)
        assertEquals(
            listOf(
                Periode(fra = 1.mai(2022), til = 4.mai(2022)),
                Periode(fra = 7.mai(2022), til = 10.mai(2022)),
                Periode(fra = 13.mai(2022), til = 15.mai(2022)),
            ),
            result,
        )
    }

    @Test
    fun `ikkeOverlappendePerioder fjerner overlapp mellom flere perioder --fengselOgKvp---`() {
        val periodeEn = Periode(fra = 1.mai(2022), til = 15.mai(2022))
        val fengselPeriode = Periode(fra = 5.mai(2022), til = 11.mai(2022))
        val kvpPeriode = Periode(fra = 10.mai(2022), til = 12.mai(2022))

        val result = periodeEn.ikkeOverlappendePerioder(
            listOf(
                fengselPeriode,
                kvpPeriode,
            ),
        )
        assertEquals(2, result.size)
        assertEquals(
            listOf(
                Periode(fra = 1.mai(2022), til = 4.mai(2022)),
                Periode(fra = 13.mai(2022), til = 15.mai(2022)),
            ),
            result,
        )
    }

    @Test
    fun `man kan trekke en periode fra en annen periode`() {
        val periodeEn = Periode(fra = 3.mai(2022), til = 15.mai(2022))
        val periodeTo = Periode(fra = 6.mai(2022), til = 12.mai(2022))
        val perioder = periodeEn.trekkFra(listOf(periodeTo))
        assertEquals(2, perioder.size)
        assertEquals(3.mai(2022), perioder[0].fra)
        assertEquals(5.mai(2022), perioder[0].til)
        assertEquals(13.mai(2022), perioder[1].fra)
        assertEquals(15.mai(2022), perioder[1].til)
    }

    @Test
    fun `man kan trekke en periode fra en annen ikke-overlappende periode`() {
        val periodeEn = Periode(fra = 3.mai(2022), til = 15.mai(2022))
        val periodeTo = Periode(fra = 6.mai(2022), til = 18.mai(2022))
        val perioder = periodeEn.trekkFra(listOf(periodeTo))
        assertEquals(1, perioder.size)
        assertEquals(3.mai(2022), perioder[0].fra)
        assertEquals(5.mai(2022), perioder[0].til)
    }

    @Test
    fun `man kan trekke flere perioder fra en annen periode`() {
        val periodeEn = Periode(fra = 3.mai(2022), til = 15.mai(2022))
        val periodeTo = Periode(fra = 6.mai(2022), til = 8.mai(2022))
        val periodeTre = Periode(fra = 10.mai(2022), til = 12.mai(2022))
        val perioder = periodeEn.trekkFra(listOf(periodeTo, periodeTre))
        assertEquals(3, perioder.size)
        assertEquals(3.mai(2022), perioder[0].fra)
        assertEquals(5.mai(2022), perioder[0].til)
        assertEquals(9.mai(2022), perioder[1].fra)
        assertEquals(9.mai(2022), perioder[1].til)
        assertEquals(13.mai(2022), perioder[2].fra)
        assertEquals(15.mai(2022), perioder[2].til)
    }

    @Test
    fun `man kan trekke flere connected perioder fra en annen periode`() {
        val periodeEn = Periode(fra = 3.mai(2022), til = 15.mai(2022))
        val periodeTo = Periode(fra = 6.mai(2022), til = 9.mai(2022))
        val periodeTre = Periode(fra = 10.mai(2022), til = 12.mai(2022))
        val perioder = periodeEn.trekkFra(listOf(periodeTo, periodeTre))
        assertEquals(2, perioder.size)
        assertEquals(3.mai(2022), perioder[0].fra)
        assertEquals(5.mai(2022), perioder[0].til)
        assertEquals(13.mai(2022), perioder[1].fra)
        assertEquals(15.mai(2022), perioder[1].til)
    }

    @Test
    fun `man kan ikke trekke flere overlappende perioder fra en annen periode`() {
        val periodeEn = Periode(fra = 3.mai(2022), til = 15.mai(2022))
        val periodeTo = Periode(fra = 6.mai(2022), til = 10.mai(2022))
        val periodeTre = Periode(fra = 9.mai(2022), til = 12.mai(2022))
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            periodeEn.trekkFra(listOf(periodeTo, periodeTre))
        }
    }

    @Test
    fun `hvis man legger sammen to perioder som overlapper får man en periode`() {
        val periodeEn = Periode(fra = 3.mai(2022), til = 15.mai(2022))
        val periodeTo = Periode(fra = 10.mai(2022), til = 20.mai(2022))
        val periodeTre = Periode(fra = 3.mai(2022), til = 20.mai(2022))
        assertEquals(periodeTre, listOf(periodeEn, periodeTo).leggSammen().first())
    }

    @Test
    fun `hvis man legger sammen to tilstøtende perioder får man en periode`() {
        val periodeEn = Periode(fra = 6.mai(2022), til = 15.mai(2022))
        val periodeTo = Periode(fra = 1.mai(2022), til = 5.mai(2022))
        val periodeTre = Periode(fra = 1.mai(2022), til = 15.mai(2022))
        assertEquals(1, listOf(periodeEn, periodeTo).leggSammen().size)
        assertEquals(periodeTre, listOf(periodeEn, periodeTo).leggSammen().first())
    }

    @Test
    fun `hvis man legger sammen to tilstøtende perioder og en tredje separat periode får man to perioder`() {
        val periodeEn = Periode(fra = 6.mai(2022), til = 15.mai(2022))
        val periodeTo = Periode(fra = 1.mai(2022), til = 5.mai(2022))
        val periodeFasitEn = Periode(fra = 1.mai(2022), til = 15.mai(2022))
        val periodeTre = Periode(fra = 18.mai(2022), til = 20.mai(2022))
        val periodeFasitTo = Periode(fra = 18.mai(2022), til = 20.mai(2022))
        assertEquals(periodeFasitEn, listOf(periodeEn, periodeTo, periodeTre).leggSammen().first())
        assertEquals(periodeFasitTo, listOf(periodeEn, periodeTo, periodeTre).leggSammen().last())
    }

    @Test
    fun `to overlappende perioder gir overlapp`() {
        val periodeEn = Periode(fra = 6.mai(2022), til = 15.mai(2022))
        val periodeTo = Periode(fra = 1.mai(2022), til = 7.mai(2022))
        assertEquals(true, listOf(periodeEn, periodeTo).inneholderOverlapp())
    }

    @Test
    fun `to ikke-overlappende perioder gir ikke overlapp`() {
        val periodeEn = Periode(fra = 8.mai(2022), til = 15.mai(2022))
        val periodeTo = Periode(fra = 1.mai(2022), til = 7.mai(2022))
        assertEquals(false, listOf(periodeEn, periodeTo).inneholderOverlapp())
    }

    @Test
    fun `tre ikke-overlappende perioder der den tredje er mellom de to første gir ikke overlapp`() {
        val periodeEn = Periode(fra = 1.mai(2022), til = 10.mai(2022))
        val periodeTo = Periode(fra = 20.mai(2022), til = 25.mai(2022))
        val periodeTre = Periode(fra = 11.mai(2022), til = 19.mai(2022))
        assertEquals(false, listOf(periodeEn, periodeTo, periodeTre).inneholderOverlapp())
    }

    @Test
    fun `tre ikke-overlappende perioder der den tredje med den andre gir overlapp`() {
        val periodeEn = Periode(fra = 1.mai(2022), til = 10.mai(2022))
        val periodeTo = Periode(fra = 20.mai(2022), til = 25.mai(2022))
        val periodeTre = Periode(fra = 11.mai(2022), til = 20.mai(2022))
        assertEquals(true, listOf(periodeEn, periodeTo, periodeTre).inneholderOverlapp())
    }

    @Test
    fun testTrekkFraPerioder() {
        val perioder1 = listOf(Periode(LocalDate.of(2020, 10, 1), LocalDate.of(2023, 10, 10)))
        val perioder2 = listOf(Periode(LocalDate.of(2020, 10, 1), LocalDate.of(2023, 10, 10)))
        val tomPeriode = perioder1.trekkFra(perioder2)
        tomPeriode.size shouldBe 0
    }

    @Test
    fun `perioder som er adjacent skal legges sammen og bli en periode`() {
        val periode1 = Periode(fra = 13.mai(2022), til = 18.mai(2022))
        val periode2 = Periode(fra = 19.mai(2022), til = 21.mai(2022))
        val periode3 = Periode(fra = 22.mai(2022), til = 27.mai(2022))
        val periode4 = Periode(fra = 27.mai(2022), til = 30.mai(2022))
        val nyePerioder = listOf(periode1, periode2).leggSammenMed(listOf(periode3, periode4))

        nyePerioder.size shouldBe 1
        nyePerioder.first() shouldBe Periode(fra = 13.mai(2022), til = 30.mai(2022))
    }

    @Test
    fun `perioder som er adjacent skal ikke ha noen overlappende periode`() {
        val periode1 = Periode(fra = 13.mai(2022), til = 18.mai(2022))
        val periode2 = Periode(fra = 19.mai(2022), til = 21.mai(2022))
        val periode3 = Periode(fra = 22.mai(2022), til = 27.mai(2022))
        val periode4 = Periode(fra = 27.mai(2022), til = 30.mai(2022))
        val overlappendePerioder = listOf(periode1, periode2).overlappendePerioder(listOf(periode3, periode4))

        overlappendePerioder.size shouldBe 0
    }

    @Test
    fun `perioder som er delvis overlappende skal ha en overlappende periode`() {
        val periode1 = Periode(fra = 13.mai(2022), til = 18.mai(2022))
        val periode2 = Periode(fra = 19.mai(2022), til = 25.mai(2022))
        val periode3 = Periode(fra = 22.mai(2022), til = 23.mai(2022))
        val periode4 = Periode(fra = 24.mai(2022), til = 30.mai(2022))
        val overlappendePerioder = listOf(periode1, periode2).overlappendePerioder(listOf(periode3, periode4))

        overlappendePerioder.size shouldBe 1
        overlappendePerioder.first() shouldBe Periode(fra = 22.mai(2022), til = 25.mai(2022))
    }

    @Test
    fun `perioder som er fullstendig overlappende skal ha en overlappende periode som er lik den minste perioden`() {
        val periode1 = Periode(fra = 13.mai(2022), til = 25.mai(2022))
        val periode2 = Periode(fra = 19.mai(2022), til = 1.juni(2022))
        val periode3 = Periode(fra = 22.mai(2022), til = 23.mai(2022))
        val periode4 = Periode(fra = 24.mai(2022), til = 30.mai(2022))
        val overlappendePerioder = listOf(periode1, periode2).overlappendePerioder(listOf(periode3, periode4))

        overlappendePerioder.size shouldBe 1
        overlappendePerioder.first() shouldBe Periode(fra = 22.mai(2022), til = 30.mai(2022))
    }

    @Test
    fun `overlappende perioder skal fungere uansett hvilken liste som er subjekt og objekt`() {
        val periode1 = Periode(fra = 13.mai(2022), til = 25.mai(2022))
        val periode2 = Periode(fra = 19.mai(2022), til = 1.juni(2022))
        val periode3 = Periode(fra = 22.mai(2022), til = 23.mai(2022))
        val periode4 = Periode(fra = 24.mai(2022), til = 30.mai(2022))
        val overlappendePerioder = listOf(periode1, periode2).overlappendePerioder(listOf(periode3, periode4))
        val overlappendePerioder2 = listOf(periode3, periode4).overlappendePerioder(listOf(periode1, periode2))

        overlappendePerioder.size shouldBe 1
        overlappendePerioder2.size shouldBe 1
        overlappendePerioder.first() shouldBe overlappendePerioder2.first()
    }

    @Test
    fun `perioder som er adjacent med periode skal ikke ha noen overlappende periode`() {
        val periode1 = Periode(fra = 13.mai(2022), til = 21.mai(2022))
        val periode3 = Periode(fra = 22.mai(2022), til = 27.mai(2022))
        val periode4 = Periode(fra = 27.mai(2022), til = 30.mai(2022))
        val overlappendePerioder = periode1.overlappendePerioder(listOf(periode3, periode4))

        overlappendePerioder.size shouldBe 0
    }

    @Test
    fun `perioder som er delvis overlappende med periode skal ha en overlappende periode`() {
        val periode1 = Periode(fra = 13.mai(2022), til = 25.mai(2022))
        val periode3 = Periode(fra = 22.mai(2022), til = 23.mai(2022))
        val periode4 = Periode(fra = 24.mai(2022), til = 30.mai(2022))
        val overlappendePerioder = periode1.overlappendePerioder(listOf(periode3, periode4))

        overlappendePerioder.size shouldBe 1
        overlappendePerioder.first() shouldBe Periode(fra = 22.mai(2022), til = 25.mai(2022))
    }

    @Test
    fun `perioder som er fullstendig overlappende med periode skal ha en overlappende periode som er lik den minste perioden`() {
        val periode1 = Periode(fra = 13.mai(2022), til = 1.juni(2022))
        val periode3 = Periode(fra = 22.mai(2022), til = 23.mai(2022))
        val periode4 = Periode(fra = 24.mai(2022), til = 30.mai(2022))
        val overlappendePerioder = periode1.overlappendePerioder(listOf(periode3, periode4))

        overlappendePerioder.size shouldBe 1
        overlappendePerioder.first() shouldBe Periode(fra = 22.mai(2022), til = 30.mai(2022))
    }
}
