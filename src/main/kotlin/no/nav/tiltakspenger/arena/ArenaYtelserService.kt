package no.nav.tiltakspenger.arena

import mu.KotlinLogging
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.MessageProblems
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.helse.rapids_rivers.asOptionalLocalDate
import no.nav.tiltakspenger.arena.ytelser.ArenaSoapService
import no.nav.tiltakspenger.arena.ytelser.YtelseSak

class ArenaYtelserService(rapidsConnection: RapidsConnection, private val arenaSoapService: ArenaSoapService) :
    River.PacketListener {

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandAllOrAny("@behov", listOf("ytelser"))
                it.forbid("@løsning")
                it.requireKey("@id", "@behovId") // Hva er forskjellen på den ene og den andre her?
                it.requireKey("ident")
                it.interestedIn("fom")
                it.interestedIn("tom")
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        LOG.info { "Received packet: ${packet.toJson()}" }
        val ident = packet["ident"].asText()
        val fom = packet["fom"].asOptionalLocalDate()
        val tom = packet["tom"].asOptionalLocalDate()
        val ytelser = YtelseSak.of(arenaSoapService.getYtelser(fnr = ident, fom = fom, tom = tom))
        val løsning = ytelser.first().ytelsestype ?: ""
        packet["@løsning"] = løsning
        LOG.info { "Sending ytelse: $ytelser og løsning: $løsning" }
        context.publish(packet.toJson())
    }

    override fun onSevere(error: MessageProblems.MessageException, context: MessageContext) {
        LOG.error { error }
    }

    override fun onError(problems: MessageProblems, context: MessageContext) {
        LOG.debug { problems }
    }
}
