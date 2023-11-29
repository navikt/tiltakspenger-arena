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
    // BARNMSTON er bare i bruk på BTIL vedtak!
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

data class ArenaTiltakspengerVedtakfaktaDTO(
    val beslutningsdato: LocalDate?,
    val dagsats: Int?,
    val antallDager: Double?,
    val relatertTiltak: String?,
    val relatertTiltakNavn: String?,
    val opprinneligTilDato: LocalDate?,
    val antallBarn: Int?,
)

fun List<ArenaVedtakfaktaDTO>.toArenaTiltakspengerVedtakfaktaDTO() =
    ArenaTiltakspengerVedtakfaktaDTO(
        beslutningsdato = this.beslutningsdato(),
        dagsats = this.dagsats(),
        antallDager = this.antallDager(),
        relatertTiltak = this.relatertTiltak(),
        relatertTiltakNavn = this.relatertTiltakNavn(),
        opprinneligTilDato = this.opprinneligTilDato(),
        antallBarn = this.antallBarn(),
    )

// Kan være null for tiltakspenger, selv om det kanskje er litt rart?
private fun List<ArenaVedtakfaktaDTO>.beslutningsdato(): LocalDate? =
    this.find { it.vedtakfaktaKode == ArenaLovligeVedtakFaktaForTiltakspenger.INNVF.name }?.vedtakfaktaVerdi
        .also { log.info { "${ArenaLovligeVedtakFaktaForTiltakspenger.INNVF.name}: $it" } }
        ?.let { LocalDate.parse(it, DateTimeFormatter.ofPattern("dd-MM-yyyy")) }

private fun List<ArenaVedtakfaktaDTO>.dagsats(): Int? =
    this.find { it.vedtakfaktaKode == ArenaLovligeVedtakFaktaForTiltakspenger.DAGS.name }?.vedtakfaktaVerdi
        .also { log.info { "${ArenaLovligeVedtakFaktaForTiltakspenger.DAGS.name}: $it" } }
        ?.toInt()

private fun List<ArenaVedtakfaktaDTO>.antallBarn(): Int? =
    this.find { it.vedtakfaktaKode == ArenaLovligeVedtakFaktaForTiltakspenger.BARNMSTON.name }?.vedtakfaktaVerdi
        .also { log.info { "${ArenaLovligeVedtakFaktaForTiltakspenger.BARNMSTON.name}: $it" } }
        ?.toInt()

private fun List<ArenaVedtakfaktaDTO>.antallDager(): Double? =
    this.find { it.vedtakfaktaKode == ArenaLovligeVedtakFaktaForTiltakspenger.DAGUTBTILT.name }?.vedtakfaktaVerdi
        .also { log.info { "${ArenaLovligeVedtakFaktaForTiltakspenger.DAGUTBTILT.name}: $it" } }
        ?.toDouble()

private fun List<ArenaVedtakfaktaDTO>.relatertTiltak(): String? =
    this.find { it.vedtakfaktaKode == ArenaLovligeVedtakFaktaForTiltakspenger.KODETILTAK.name }?.vedtakfaktaVerdi
        .also { log.info { "${ArenaLovligeVedtakFaktaForTiltakspenger.KODETILTAK.name}: $it" } }

private fun List<ArenaVedtakfaktaDTO>.relatertTiltakNavn(): String? =
    this.find { it.vedtakfaktaKode == ArenaLovligeVedtakFaktaForTiltakspenger.TILTAKNAVN.name }?.vedtakfaktaVerdi
        .also { log.info { "${ArenaLovligeVedtakFaktaForTiltakspenger.TILTAKNAVN.name}: $it" } }

private fun List<ArenaVedtakfaktaDTO>.opprinneligTilDato(): LocalDate? =
    this.find { it.vedtakfaktaKode == ArenaLovligeVedtakFaktaForTiltakspenger.OPPRTDATO.name }?.vedtakfaktaVerdi
        .also { log.info { "${ArenaLovligeVedtakFaktaForTiltakspenger.OPPRTDATO.name}: $it" } }
        ?.let { LocalDate.parse(it, DateTimeFormatter.ofPattern("dd-MM-yyyy")) }
