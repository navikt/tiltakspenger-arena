package no.nav.tiltakspenger.arena

import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.MessageProblems
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsClient

class ArenaTiltakService(
    rapidsConnection: RapidsConnection,
    private val arenaOrdsService: ArenaOrdsClient
) :
    River.PacketListener {

    companion object {
        private val LOG = KotlinLogging.logger {}

        internal object BEHOV {
            const val TILTAK_LISTE = "tiltak"
        }
    }

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandAllOrAny("@behov", listOf(BEHOV.TILTAK_LISTE))
                it.forbid("@løsning")
                it.requireKey("@id", "@behovId")
                it.requireKey("ident")
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        LOG.info { "Received packet: ${packet.toJson()}" }
        val ident = packet["ident"].asText()
        runBlocking {
            val aktiviteter = arenaOrdsService.hentArenaAktiviteter(ident)
            // Trengs det å mappe denne noe mer her, til egen domenemodell?
            packet["@løsning"] = mapOf(
                BEHOV.TILTAK_LISTE to aktiviteter.response.tiltaksaktivitetListe
            )
            LOG.info { "Sending tiltal: $aktiviteter.response.tiltaksaktivitetListe" }
            context.publish(packet.toJson())
        }
    }

    override fun onError(problems: MessageProblems, context: MessageContext) {
        LOG.debug { problems }
    }
}
