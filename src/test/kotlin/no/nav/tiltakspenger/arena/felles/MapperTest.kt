package no.nav.tiltakspenger.arena.felles

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junitpioneer.jupiter.DefaultTimeZone
import java.time.LocalDate
import javax.xml.datatype.DatatypeFactory

internal class MapperTest {

    @Test
    fun `frem og tilbake er like langt`() {
        val localDate = LocalDate.of(2022, 10, 30)
        assertEquals(localDate, localDate.toXMLGregorian().toLocalDate())
    }

    @DefaultTimeZone("UTC")
    @Test
    fun `frem og tilbake er like langt selv om timezone er UTC`() {
        val localDate = LocalDate.of(2022, 10, 30)
        assertEquals(localDate, localDate.toXMLGregorian().toLocalDate())
    }

    @DefaultTimeZone("UTC")
    @Test
    fun `fra gregorian til localdate skal gi riktig date selv om timezone er UTC`() {
        val xgc = DatatypeFactory.newInstance().newXMLGregorianCalendar("2022-10-30")
        assertEquals(LocalDate.of(2022, 10, 30), xgc.toLocalDate())
    }

    @DefaultTimeZone("Asia/Bangkok")
    @Test
    fun `fra gregorian til localdate skal gi riktig date selv om timezone er Bangkok`() {
        val xgc = DatatypeFactory.newInstance().newXMLGregorianCalendar("2022-10-30")
        assertEquals(LocalDate.of(2022, 10, 30), xgc.toLocalDate())
    }

    @DefaultTimeZone("Asia/Bangkok")
    @Test
    fun `fra localdate til gregorian skal gi riktig date selv om timezone er Bangkok`() {
        val localDate = LocalDate.of(2022, 10, 30)
        assertEquals("2022-10-30T00:00:00.000+07:00", localDate.toXMLGregorian().toString())
    }
}
