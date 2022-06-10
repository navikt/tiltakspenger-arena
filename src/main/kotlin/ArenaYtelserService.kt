import mu.KotlinLogging
import no.nav.helse.rapids_rivers.*
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
                it.requireKey("ident")
                // Hva brukes egentlig interestedIn til??
                it.interestedIn("@behov")
                it.interestedIn("@id")
                it.interestedIn("fom")
                it.interestedIn("tom")
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        LOG.info { "Received packet: ${packet.toJson()}" }
        val ident = packet.get("ident").asText()
        val fom = packet.get("fom").asOptionalLocalDate()
        val tom = packet.get("fom").asOptionalLocalDate()
        val ytelser = YtelseSak.of(arenaSoapService.getYtelser(fnr = ident, fom = fom, tom = tom))
        context.publish(ident, ytelser.asRapidMessage())
    }

    override fun onSevere(error: MessageProblems.MessageException, context: MessageContext) {
        LOG.error { error }
    }

    override fun onError(problems: MessageProblems, context: MessageContext) {
        LOG.error { problems }
    }

    private fun List<YtelseSak>.asRapidMessage(): String {
        return "{}"
    }
}
