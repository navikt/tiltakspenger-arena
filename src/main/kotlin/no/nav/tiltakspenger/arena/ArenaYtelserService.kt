package no.nav.tiltakspenger.arena

import mu.KotlinLogging
import mu.withLoggingContext
import net.logstash.logback.argument.StructuredArguments
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.tiltakspenger.arena.repository.SakRepository
import no.nav.tiltakspenger.arena.ytelser.ArenaSoapService
import no.nav.tiltakspenger.arena.ytelser.mapArenaYtelser
import no.nav.tiltakspenger.arena.ytelser.mapArenaYtelserFraDB
import no.nav.tiltakspenger.libs.arena.ytelse.ArenaYtelseResponsDTO
import no.nav.tiltakspenger.libs.periodisering.Periode
import no.nav.tiltakspenger.libs.periodisering.inneholderOverlapp
import no.nav.tiltakspenger.libs.periodisering.leggSammen
import java.time.LocalDate

private val LOG = KotlinLogging.logger {}
private val SECURELOG = KotlinLogging.logger("tjenestekall")

class ArenaYtelserService(
    rapidsConnection: RapidsConnection,
    private val arenaSoapService: ArenaSoapService,
    private val arenaSakRepository: SakRepository,
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
                "behovId" to packet["@behovId"].asText(),
            ) {
                val ident = packet["ident"].asText()
                val fom = null // packet["fom"].asOptionalLocalDate()
                val tom = null // packet["tom"].asOptionalLocalDate()
                val respons: ArenaYtelseResponsDTO =
                    mapArenaYtelser(arenaSoapService.getYtelser(fnr = ident, fom = fom, tom = tom))
                if (Configuration.applicationProfile() == Profile.DEV) {
                    try {
                        val dbRespons: ArenaYtelseResponsDTO =
                            mapArenaYtelserFraDB(arenaSakRepository.hentSakerForFnr(fnr = ident))
                        if (respons == dbRespons) {
                            LOG.info { "Lik response fra webservice og db" }
                        } else {
                            LOG.info { "Ulik response fra webservice og db" }
                            SECURELOG.info { "Ulik response fra webservice og db for ident $ident" }
                            SECURELOG.info { "webservice: $respons" }
                            SECURELOG.info { "db: $dbRespons" }
                        }
                        LOG.info { "Antall vedtak fra webservicen er ${respons.saker?.flatMap { it.vedtak }?.size ?: 0}" }
                        LOG.info { "Antall vedtak fra db er ${dbRespons.saker?.flatMap { it.vedtak }?.size ?: 0}" }

                        val vedtakPerioder = dbRespons.saker
                            ?.flatMap { it.vedtak }
                            ?.map {
                                Periode(
                                    it.vedtaksperiodeFom ?: LocalDate.MIN,
                                    it.vedtaksperiodeTom ?: LocalDate.MAX,
                                )
                            }
                        if (vedtakPerioder != null) {
                            if (vedtakPerioder.inneholderOverlapp()) {
                                LOG.info { "Vedtaksperiodene fra Arena overlapper hverandre" }
                            }
                            val sammenhengenePerioder = vedtakPerioder.leggSammen()
                            LOG.info { "Antall sammenhengende vedtaksperioder er ${sammenhengenePerioder.size}" }
                        }
                    } catch (e: Exception) {
                        LOG.info("Kall mot Arena db feilet", e)
                    }
                }
                packet["@løsning"] = mapOf(
                    BEHOV.ARENAYTELSER to respons,
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
            StructuredArguments.keyValue("behovId", packet["@behovId"].asText()),
        )
        SECURELOG.info(
            "løser ytelser-behov med {} og {}",
            StructuredArguments.keyValue("id", packet["@id"].asText()),
            StructuredArguments.keyValue("behovId", packet["@behovId"].asText()),
        )
        SECURELOG.debug { "mottok melding: ${packet.toJson()}" }
    }

    private fun loggVedUtgang(packet: JsonMessage) {
        LOG.info(
            "har løst ytelser-behov med {} og {}",
            StructuredArguments.keyValue("id", packet["@id"].asText()),
            StructuredArguments.keyValue("behovId", packet["@behovId"].asText()),
        )
        SECURELOG.info(
            "har løst ytelser-behov med {} og {}",
            StructuredArguments.keyValue("id", packet["@id"].asText()),
            StructuredArguments.keyValue("behovId", packet["@behovId"].asText()),
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
            ex,
        )
    }
}
