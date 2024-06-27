package no.nav.tiltakspenger.arena.felles

import java.time.LocalDate
import java.time.Month

infix fun Int.januar(year: Int): LocalDate = LocalDate.of(year, Month.JANUARY, this)
infix fun Int.februar(year: Int): LocalDate = LocalDate.of(year, Month.FEBRUARY, this)
infix fun Int.mars(year: Int): LocalDate = LocalDate.of(year, Month.MARCH, this)
infix fun Int.april(year: Int): LocalDate = LocalDate.of(year, Month.APRIL, this)
infix fun Int.mai(year: Int): LocalDate = LocalDate.of(year, Month.MAY, this)
infix fun Int.juni(year: Int): LocalDate = LocalDate.of(year, Month.JUNE, this)
infix fun Int.juli(year: Int): LocalDate = LocalDate.of(year, Month.JULY, this)
infix fun Int.august(year: Int): LocalDate = LocalDate.of(year, Month.AUGUST, this)
infix fun Int.september(year: Int): LocalDate = LocalDate.of(year, Month.SEPTEMBER, this)
infix fun Int.oktober(year: Int): LocalDate = LocalDate.of(year, Month.OCTOBER, this)
infix fun Int.november(year: Int): LocalDate = LocalDate.of(year, Month.NOVEMBER, this)
infix fun Int.desember(year: Int): LocalDate = LocalDate.of(year, Month.DECEMBER, this)
