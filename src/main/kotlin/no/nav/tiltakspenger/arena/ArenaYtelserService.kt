package no.nav.tiltakspenger.arena

import mu.KotlinLogging
import mu.withLoggingContext
import net.logstash.logback.argument.StructuredArguments
import no.nav.helse.rapids_rivers.*
import no.nav.tiltakspenger.arena.ytelser.ArenaSoapService
import no.nav.tiltakspenger.arena.ytelser.mapArenaYtelser
import no.nav.tiltakspenger.libs.arena.ytelse.ArenaYtelseResponsDTO

private val LOG = KotlinLogging.logger {}
private val SECURELOG = KotlinLogging.logger("tjenestekall")

class ArenaYtelserService(
    rapidsConnection: RapidsConnection,
    private val arenaSoapService: ArenaSoapService
) :
    River.PacketListener {

    companion object {
        internal object BEHOV {
            const val ARENAYTELSER = "arenaytelser"
        }
    }

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandAllOrAny("@behov", listOf(BEHOV.ARENAYTELSER))
                it.forbid("@løsning")
                it.requireKey("@id", "@behovId")
                it.requireKey("ident")
                it.interestedIn("fom")
                it.interestedIn("tom")
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        runCatching {
            loggVedInngang(packet)

            withLoggingContext(
                "id" to packet["@id"].asText(),
                "behovId" to packet["@behovId"].asText()
            ) {
                val ident = packet["ident"].asText()
                val fom = packet["fom"].asOptionalLocalDate()
                val tom = packet["tom"].asOptionalLocalDate()
                val respons: ArenaYtelseResponsDTO =
                    mapArenaYtelser(arenaSoapService.getYtelser(fnr = ident, fom = fom, tom = tom))
                packet["@løsning"] = mapOf(
                    BEHOV.ARENAYTELSER to respons
                )
                loggVedUtgang(packet)
                context.publish(ident, packet.toJson())
            }
        }.onFailure {
            loggVedFeil(it, packet)
        }.getOrThrow()
    }

    private fun loggVedInngang(packet: JsonMessage) {
        LOG.info(
            "løser ytelser-behov med {} og {}",
            StructuredArguments.keyValue("id", packet["@id"].asText()),
            StructuredArguments.keyValue("behovId", packet["@behovId"].asText())
        )
        SECURELOG.info(
            "løser ytelser-behov med {} og {}",
            StructuredArguments.keyValue("id", packet["@id"].asText()),
            StructuredArguments.keyValue("behovId", packet["@behovId"].asText())
        )
        SECURELOG.debug { "mottok melding: ${packet.toJson()}" }
    }

    private fun loggVedUtgang(packet: JsonMessage) {
        LOG.info(
            "har løst ytelser-behov med {} og {}",
            StructuredArguments.keyValue("id", packet["@id"].asText()),
            StructuredArguments.keyValue("behovId", packet["@behovId"].asText())
        )
        SECURELOG.info(
            "har løst ytelser-behov med {} og {}",
            StructuredArguments.keyValue("id", packet["@id"].asText()),
            StructuredArguments.keyValue("behovId", packet["@behovId"].asText())
        )
        SECURELOG.debug { "publiserer melding: ${packet.toJson()}" }
    }

    private fun loggVedFeil(ex: Throwable, packet: JsonMessage) {
        LOG.error(
            "feil ved behandling av ytelser-behov med {}, se securelogs for detaljer",
            StructuredArguments.keyValue("id", packet["@id"].asText()),
        )
        SECURELOG.error(
            "feil \"${ex.message}\" ved behandling av ytelser-behov med {} og {}",
            StructuredArguments.keyValue("id", packet["@id"].asText()),
            StructuredArguments.keyValue("packet", packet.toJson()),
            ex
        )
    }
}
