package no.nav.tiltakspenger.arena.felles

import mu.KotlinLogging
import java.text.ParseException
import java.time.LocalDate
import java.time.ZoneId
import java.util.GregorianCalendar
import javax.xml.datatype.DatatypeConfigurationException
import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar

private val LOG = KotlinLogging.logger {}
private val SECURELOG = KotlinLogging.logger("tjenestekall")

// TO DO: Er ikke sikker på om vi burde angi Europe/Oslo eller ikke.
//  (Gjelder både tilXmlGregorianCalenadar og toLocalDate..)

fun LocalDate?.toXMLGregorian(): XMLGregorianCalendar? {
    return try {
        this?.atStartOfDay(ZoneId.systemDefault()) // ZoneId.of("Europe/Oslo") ??
            ?.let { GregorianCalendar.from(it) }
            ?.let { DatatypeFactory.newInstance().newXMLGregorianCalendar(it) }
    } catch (exception: ParseException) {
        LOG.error("Feil logget til securelog")
        SECURELOG.error(exception) { "Noe feilet" }
        null
    } catch (exception: DatatypeConfigurationException) {
        LOG.error("Feil logget til securelog")
        SECURELOG.error(exception) { "Noe feilet" }
        null
    }
}

fun XMLGregorianCalendar?.toLocalDate(): LocalDate? =
    try {
        // https://codereview.stackexchange.com/questions/214711/xmlgregoriancalendar-to-localdatetime
        this?.toGregorianCalendar()
            ?.toZonedDateTime()
            // ?.withZoneSameInstant(ZoneId.of("Europe/Oslo"))
            ?.toLocalDate()
    } catch (exception: ParseException) {
        LOG.error("Feil logget til securelog")
        SECURELOG.error(exception) { "Noe feilet" }
        null
    } catch (exception: DatatypeConfigurationException) {
        LOG.error("Feil logget til securelog")
        SECURELOG.error(exception) { "Noe feilet" }
        null
    }
