@file:Suppress("Indentation")

package no.nav.tiltakspenger.arena.ytelser

import mu.KotlinLogging
import org.slf4j.MDC
import javax.xml.namespace.QName
import javax.xml.soap.SOAPException
import javax.xml.ws.ProtocolException
import javax.xml.ws.handler.MessageContext
import javax.xml.ws.handler.soap.SOAPHandler
import javax.xml.ws.handler.soap.SOAPMessageContext

private val LOG = KotlinLogging.logger {}

/**
 * Cut'n'paste from https://github.com/navikt/modiapersonoversikt-api/blob/eb85e37863ba5691a0285209dc8655eaaf11483a/web/src/main/java/no/nav/modiapersonoversikt/infrastructure/jaxws/handlers/MDCOutHandler.java, with some cleanup.
 */
internal class MDCOutHandler : SOAPHandler<SOAPMessageContext?> {
    companion object {
        // QName for the callId header
        private val CALLID_QNAME = QName("uri:no.nav.applikasjonsrammeverk", MDCConstants.MDC_CALL_ID)
    }

    override fun handleMessage(context: SOAPMessageContext?): Boolean {
        val outbound = context!![MessageContext.MESSAGE_OUTBOUND_PROPERTY] as Boolean

        // OUTBOUND processing
        if (outbound) {
            val callId = MDC.get(MDCConstants.MDC_CALL_ID)
                ?: throw NullPointerException(
                    "CallId skal være tilgjengelig i MDC på dette tidspunkt. Om du er en webapp, må du legge til et " +
                        "MDCFilter i web.xml (oppskrift på dette: " +
                        "http://confluence.adeo.no/display/Modernisering/MDCFilter). " +
                        "Om du er noe annet må du generere callId selv og legge på MDC. " +
                        "Hjelpemetoder finnes i no.nav.modig.common.MDCOperations."
                )
            LOG.debug("Add the callId to the SOAP message: $callId")
            try {
                val envelope = context.message.soapPart.envelope
                val header = envelope.header
                val callIdElement = header.addChildElement(CALLID_QNAME)
                callIdElement.value = callId
            } catch (e: SOAPException) {
                LOG.error("SOAPException logget til securelog")
                LOG.error(e) { e.message }
                throw ProtocolException(e)
            }
        }
        return true
    }

    override fun handleFault(context: SOAPMessageContext?): Boolean {
        return true
    }

    override fun close(context: MessageContext) {
        // Does nothing.
    }

    override fun getHeaders(): Set<QName> {
        return setOf(CALLID_QNAME)
    }
}

interface MDCConstants {
    companion object {
        const val MDC_USER_ID = "userId"
        const val MDC_CONSUMER_ID = "consumerId"
        const val MDC_CALL_ID = "callId"
        const val MDC_REQUEST_ID = "requestId"
        const val MDC_JOB_ID = "jobId"
        const val MDC_JOB_NAME = "jobName"
    }
}
