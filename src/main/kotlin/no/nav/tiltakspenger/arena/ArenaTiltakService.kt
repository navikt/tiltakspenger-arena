package no.nav.tiltakspenger.arena

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import mu.KotlinLogging
import mu.withLoggingContext
import net.logstash.logback.argument.StructuredArguments
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsClient

private val LOG = KotlinLogging.logger {}
private val SECURELOG = KotlinLogging.logger("tjenestekall")

class ArenaTiltakService(
    rapidsConnection: RapidsConnection,
    private val arenaOrdsService: ArenaOrdsClient
) :
    River.PacketListener {

    companion object {
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
        runCatching {
            loggVedInngang(packet)

            val aktiviteter = withLoggingContext(
                "id" to packet["@id"].asText(),
                "behovId" to packet["@behovId"].asText()
            ) {
                val ident = packet["ident"].asText()
                SECURELOG.debug { "mottok ident $ident" }
                runBlocking(MDCContext()) {
                    arenaOrdsService.hentArenaAktiviteter(ident)
                    // Trengs det å mappe denne noe mer her, til egen domenemodell?
                }
            }

            packet["@løsning"] = mapOf(
                BEHOV.TILTAK_LISTE to aktiviteter.response.tiltaksaktivitetListe
            )
            loggVedUtgang(packet) { "${aktiviteter.response.tiltaksaktivitetListe}" }
            context.publish(packet.toJson())
        }.onFailure {
            loggVedFeil(it, packet)
        }.getOrThrow()
    }

    fun loggVedInngang(packet: JsonMessage) {
        LOG.info(
            "løser behov med {} og {}",
            StructuredArguments.keyValue("id", packet["@id"].asText()),
            StructuredArguments.keyValue("behovId", packet["@behovId"].asText())
        )
        SECURELOG.info(
            "løser behov med {} og {}",
            StructuredArguments.keyValue("id", packet["@id"].asText()),
            StructuredArguments.keyValue("behovId", packet["@behovId"].asText())
        )
        SECURELOG.debug { "mottok melding: ${packet.toJson()}" }
    }

    private fun loggVedUtgang(packet: JsonMessage, løsning: () -> String) {
        LOG.info(
            "har løst behov med {} og {}",
            StructuredArguments.keyValue("id", packet["@id"].asText()),
            StructuredArguments.keyValue("behovId", packet["@behovId"].asText())
        )
        SECURELOG.info(
            "har løst behov med {} og {}",
            StructuredArguments.keyValue("id", packet["@id"].asText()),
            StructuredArguments.keyValue("behovId", packet["@behovId"].asText())
        )
        SECURELOG.debug { "publiserer løsning: $løsning" }
    }

    private fun loggVedFeil(ex: Throwable, packet: JsonMessage) {
        LOG.error(
            "feil ved behandling av behov med {}, se securelogs for detaljer",
            StructuredArguments.keyValue("id", packet["@id"].asText()),
            StructuredArguments.keyValue("behovId", packet["@behovId"].asText()),
        )
        SECURELOG.error(
            "feil ${ex.message} ved behandling av behov med {} og {}",
            StructuredArguments.keyValue("id", packet["@id"].asText()),
            StructuredArguments.keyValue("behovId", packet["@behovId"].asText()),
            ex
        )
    }
}
