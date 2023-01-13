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
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsException
import no.nav.tiltakspenger.arena.tiltakogaktivitet.mapArenaTiltak
import no.nav.tiltakspenger.libs.arena.tiltak.ArenaTiltaksaktivitetResponsDTO

private val LOG = KotlinLogging.logger {}
private val SECURELOG = KotlinLogging.logger("tjenestekall")

class ArenaTiltakService(
    rapidsConnection: RapidsConnection,
    private val arenaOrdsService: ArenaOrdsClient
) :
    River.PacketListener {

    companion object {
        internal object BEHOV {
            const val ARENATILTAK = "arenatiltak"
        }
    }

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandAllOrAny("@behov", listOf(BEHOV.ARENATILTAK))
                it.forbid("@løsning")
                it.requireKey("@id", "@behovId")
                it.requireKey("ident")
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
                SECURELOG.debug { "mottok ident $ident" }
                val response: ArenaTiltaksaktivitetResponsDTO = try {
                    runBlocking(MDCContext()) {
                        mapArenaTiltak(arenaOrdsService.hentArenaAktiviteter(ident).response.tiltaksaktivitetListe)
                    }
                } catch (e: ArenaOrdsException.PersonNotFoundException) {
                    LOG.warn { "Person ikke funnet i Arena Tiltak ${e.message}" }
                    ArenaTiltaksaktivitetResponsDTO(
                        tiltaksaktiviteter = emptyList(),
                        feil = null
                    )
                }

                packet["@løsning"] = mapOf(
                    BEHOV.ARENATILTAK to response
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
            "løser tiltak-behov med {} og {}",
            StructuredArguments.keyValue("id", packet["@id"].asText()),
            StructuredArguments.keyValue("behovId", packet["@behovId"].asText())
        )
        SECURELOG.info(
            "løser tiltak-behov med {} og {}",
            StructuredArguments.keyValue("id", packet["@id"].asText()),
            StructuredArguments.keyValue("behovId", packet["@behovId"].asText())
        )
        SECURELOG.debug { "mottok melding: ${packet.toJson()}" }
    }

    private fun loggVedUtgang(packet: JsonMessage) {
        LOG.info(
            "har løst tiltak-behov med {} og {}",
            StructuredArguments.keyValue("id", packet["@id"].asText()),
            StructuredArguments.keyValue("behovId", packet["@behovId"].asText())
        )
        SECURELOG.info(
            "har løst tiltak-behov med {} og {}",
            StructuredArguments.keyValue("id", packet["@id"].asText()),
            StructuredArguments.keyValue("behovId", packet["@behovId"].asText())
        )
        SECURELOG.debug { "publiserer melding: ${packet.toJson()}" }
    }

    private fun loggVedFeil(ex: Throwable, packet: JsonMessage) {
        LOG.error(
            "feil ved behandling av tiltak-behov med {}, se securelogs for detaljer",
            StructuredArguments.keyValue("id", packet["@id"].asText()),
        )
        SECURELOG.error(
            "feil \"${ex.message}\" ved behandling av tiltak-behov med {} og {}",
            StructuredArguments.keyValue("id", packet["@id"].asText()),
            StructuredArguments.keyValue("packet", packet.toJson()),
            ex
        )
    }
}
