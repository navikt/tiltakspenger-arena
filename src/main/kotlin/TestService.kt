import mu.KotlinLogging
import no.nav.helse.rapids_rivers.*

private val LOG = KotlinLogging.logger {}

class TestService(rapidsConnection: RapidsConnection) : River.PacketListener {
    init {
        River(rapidsConnection).apply {
            validate {
                it.interestedIn("@behov")
                it.interestedIn("@id")
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        LOG.info { "Received packet: ${packet.toJson()}" }
    }

    override fun onSevere(error: MessageProblems.MessageException, context: MessageContext) {
        LOG.error { error }
    }

    override fun onError(problems: MessageProblems, context: MessageContext) {
        LOG.error { problems }
    }
}
