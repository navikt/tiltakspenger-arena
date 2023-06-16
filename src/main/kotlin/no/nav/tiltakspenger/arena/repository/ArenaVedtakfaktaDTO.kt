package no.nav.tiltakspenger.arena.repository

import mu.KotlinLogging
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/*
For tiltakspenger finnes de følgende vedtakfakta:

BARNMSTON	Antall barn med stønad
DAGS	DAGSATS
DAGUTBTILT	Antall dager med utbetaling
FDATO	Gjelder fra
INNVF	VEDTAKSDATO
KODETILTAK	Relatert tiltak
MASKVEDTAK	Maskinelt vedtak
OPPRTDATO	Opprinnelig til-dato
SATSKODE	Valgt sats
TDATO	Gjelder til
TILTAKNAVN	Knyttet til tiltak

(Hentet ut med SQLen
select distinct(vf.vedtakfaktakode), vft.vedtakfaktanavn
from vedtakfaktatype vft, vedtakfakta vf, vedtak v, sak s
where vft.vedtakfaktakode = vf.vedtakfaktakode
and vf.vedtak_id = v.vedtak_id
and v.sak_id = s.sak_id
AND s.sakskode = 'INDIV';
)
 */

private val log = KotlinLogging.logger {}

data class ArenaVedtakfaktaDTO(
    val vedtakId: Long,
    val vedtakfaktaKode: String,
    val vedtakfaktaVerdi: String?,
)

// Kan være null for tiltakspenger, selv om det kanskje er litt rart?
fun List<ArenaVedtakfaktaDTO>.beslutningsdato(): LocalDate? =
    this.find { it.vedtakfaktaKode == "INNVF" }?.vedtakfaktaVerdi
        .also { log.info { "INNVF: $it" } }
        ?.let { LocalDate.parse(it, DateTimeFormatter.ofPattern("dd-MM-yyyy")) }

// Ser ikke ut til å gjelde for tiltakspenger? Kan iallefall være null!
fun List<ArenaVedtakfaktaDTO>.vedtakBruttoBeløp(): Int? =
    this.find { it.vedtakfaktaKode == "GRUNN" }?.vedtakfaktaVerdi
        .also { log.info { "GRUNN: $it" } }
        ?.toInt()

fun List<ArenaVedtakfaktaDTO>.dagsats(): Int? =
    this.find { it.vedtakfaktaKode == "DAGS" }?.vedtakfaktaVerdi
        .also { log.info { "DAGS: $it" } }
        ?.toInt()
