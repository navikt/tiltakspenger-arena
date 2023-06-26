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

enum class ArenaLovligeVedtakFaktaForTiltakspenger(val navn: String) {
    BARNMSTON("Antall barn med stønad"),
    DAGS("DAGSATS"),
    DAGUTBTILT("Antall dager med utbetaling"),
    FDATO("Gjelder fra"),
    INNVF("VEDTAKSDATO"),
    KODETILTAK("Relatert tiltak"),
    MASKVEDTAK("Maskinelt vedtak"),
    OPPRTDATO("Opprinnelig til-dato"),
    SATSKODE("Valgt sats"),
    TDATO("Gjelder til"),
    TILTAKNAVN("Knyttet til tiltak"),
}

data class ArenaVedtakfaktaDTO(
    val vedtakId: Long,
    val vedtakfaktaKode: String,
    val vedtakfaktaVerdi: String?,
)

// Kan være null for tiltakspenger, selv om det kanskje er litt rart?
fun List<ArenaVedtakfaktaDTO>.beslutningsdato(): LocalDate? =
    this.find { it.vedtakfaktaKode == ArenaLovligeVedtakFaktaForTiltakspenger.INNVF.name }?.vedtakfaktaVerdi
        .also { log.info { "${ArenaLovligeVedtakFaktaForTiltakspenger.INNVF.name}: $it" } }
        ?.let { LocalDate.parse(it, DateTimeFormatter.ofPattern("dd-MM-yyyy")) }

fun List<ArenaVedtakfaktaDTO>.dagsats(): Int? =
    this.find { it.vedtakfaktaKode == ArenaLovligeVedtakFaktaForTiltakspenger.DAGS.name }?.vedtakfaktaVerdi
        .also { log.info { "${ArenaLovligeVedtakFaktaForTiltakspenger.DAGS.name}: $it" } }
        ?.toInt()
