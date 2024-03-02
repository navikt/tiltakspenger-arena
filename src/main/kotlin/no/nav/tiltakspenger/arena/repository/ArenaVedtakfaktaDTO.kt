package no.nav.tiltakspenger.arena.repository

import mu.KotlinLogging
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/*
For tiltakspenger finnes de følgende vedtakfakta:

DAGS	    DAGSATS     	                DAGSATS	                                                                    NUMBER
SATSKODE	Valgt sats	                    Valgt sats
TILTAKNAVN	Knyttet til tiltak	            Knyttet til tiltak	                                                        VARCHAR2
MASKVEDTAK	Maskinelt vedtak	            (co-am 23.01.2003) Maskinelt vedtak	                                        VARCHAR2
OPPRTDATO	Opprinnelig til-dato	        Opprinnelig til-dato	                                                    DATE
TDATO	    Gjelder til	                    Gjelder til. VF_4_VEDTAK_116, VF_3_VEDTAK_TIL_BREV,	                        DATE
DAGUTBTILT	Antall dager med utbetaling	    Antall dager i løpet av en meldekortperiode det skal utbetales ytelser	    INTEGER
FDATO	    Gjelder fra	                    Gjelder fra. VF_4_VEDTAK_116,VF_3_VEDTAK_TIL_BREV	                        DATE
INNVF	    VEDTAKSDATO	                    VEDTAKSDATO	                                                                DATE
KODETILTAK  Relatert tiltak	                Kode for knytting av tiltak til vedtaket	                                VARCHAR2

Hvis jeg ikke trekker inn sak i spørringen får jeg disse - TODO å finne ut hvorfor det er ulikt antall..
OPPRTDATO	Opprinnelig til-dato
TILTAKNAVN	Knyttet til tiltak
VEDTYPKONT	Vedtakstype som kontrolleres
DAGS	DAGSATS
FDATO	Gjelder fra
TDATO	Gjelder til
DAGUTBTILT	Antall dager med utbetaling
KODETILTAK	Relatert tiltak
INNVF	VEDTAKSDATO
SATSKODE	Valgt sats
SAKTYPKONT	Sakstype som kontrolleres
TILBBETID	Vedtak id til tilbakebetalings
MASKVEDTAK	Maskinelt vedtak

For Barnetillegg finnes de følgende:
DAGS	        DAGSATS	                    DAGSATS	                                                                                                    NUMBER
SATSKODE	    Valgt sats	                Valgt sats
TILTAKNAVN	    Knyttet til tiltak	        Knyttet til tiltak	                                                                                        VARCHAR2
BARNMSTON	    Antall barn med stønad	    (CO-LN 26092001) Brukes under dagpengeprosessen. Er en del av nøkkelen i bindingen til vedtaksaksperson.    INTEGER
MASKVEDTAK	    Maskinelt vedtak	        (co-am 23.01.2003) Maskinelt vedtak	                                                                        VARCHAR2
OPPRTDATO	    Opprinnelig til-dato	    Opprinnelig til-dato	                                                                                    DATE
TDATO	        Gjelder til	                Gjelder til. VF_4_VEDTAK_116, VF_3_VEDTAK_TIL_BREV,	                                                        DATE
DAGUTBTILT	    Antall dager med utbetaling Antall dager i løpet av en meldekortperiode det skal utbetales ytelser	                                    INTEGER
FDATO	        Gjelder fra	Gjelder fra.    VF_4_VEDTAK_116,VF_3_VEDTAK_TIL_BREV	                                                                    DATE
INNVF	        VEDTAKSDATO	                VEDTAKSDATO	                                                                                                DATE
KODETILTAK	    Relatert tiltak 	        Kode for knytting av tiltak til vedtaket	                                                                VARCHAR2

Hentet ut med SQLene
    select distinct(vf.vedtakfaktakode), vft.vedtakfaktanavn
    from vedtakfaktatype vft, vedtakfakta vf, vedtak v, sak s
    where vft.vedtakfaktakode = vf.vedtakfaktakode
    and vf.vedtak_id = v.vedtak_id
    and v.rettighetkode = 'BASI';
evt
    select distinct(vf.vedtakfaktakode), vft.vedtakfaktanavn
    from vedtakfaktatype vft, vedtakfakta vf, vedtak v, sak s
    where vft.vedtakfaktakode = vf.vedtakfaktakode
    and vf.vedtak_id = v.vedtak_id
    and v.rettighetkode = 'BASI'
    and v.sak_id = s.sak_id
    AND s.sakskode = 'INDIV';
og
    select distinct(vf.vedtakfaktakode), vft.vedtakfaktanavn
    from vedtakfaktatype vft, vedtakfakta vf, vedtak v, sak s
    where vft.vedtakfaktakode = vf.vedtakfaktakode
    and vf.vedtak_id = v.vedtak_id
    and v.rettighetkode = 'BTIL'
    and v.sak_id = s.sak_id
    AND s.sakskode = 'INDIV';

 */

private val log = KotlinLogging.logger {}

enum class ArenaVedtakFakta(val navn: String) {
    DAGS("DAGSATS"),
    SATSKODE("Valgt sats"),
    TILTAKNAVN("Knyttet til tiltak"),
    MASKVEDTAK("Maskinelt vedtak"),
    OPPRTDATO("Opprinnelig til-dato"),
    TDATO("Gjelder til"),
    DAGUTBTILT("Antall dager med utbetaling"),
    FDATO("Gjelder fra"),
    INNVF("VEDTAKSDATO"),
    KODETILTAK("Relatert tiltak"),
    BARNMSTON(" Antall barn med stønad"), // Bare aktuell for barnetillegg
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
    val gjelderFra: LocalDate?,
    val gjelderTil: LocalDate?,
    val satsKode: String?,
    val maskineltVedtak: String?,
)

data class ArenaBarnetilleggVedtakfaktaDTO(
    val beslutningsdato: LocalDate?,
    val dagsats: Int?,
    val antallDager: Double?,
    val relatertTiltak: String?,
    val relatertTiltakNavn: String?,
    val opprinneligTilDato: LocalDate?,
    val antallBarn: Int?,
    val gjelderFra: LocalDate?,
    val gjelderTil: LocalDate?,
    val satsKode: String?,
    val maskineltVedtak: String?,
)

fun List<ArenaVedtakfaktaDTO>.toArenaBarnetilleggVedtakfaktaDTO() =
    ArenaBarnetilleggVedtakfaktaDTO(
        beslutningsdato = this.beslutningsdato(),
        dagsats = this.dagsats(),
        antallDager = this.antallDager(),
        relatertTiltak = this.relatertTiltak(),
        relatertTiltakNavn = this.relatertTiltakNavn(),
        opprinneligTilDato = this.opprinneligTilDato(),
        antallBarn = this.antallBarn(),
        gjelderFra = this.gjelderFra(),
        gjelderTil = this.gjelderTil(),
        satsKode = this.satsKode(),
        maskineltVedtak = this.maskineltVedtak(),
    )

fun List<ArenaVedtakfaktaDTO>.toArenaTiltakspengerVedtakfaktaDTO() =
    ArenaTiltakspengerVedtakfaktaDTO(
        beslutningsdato = this.beslutningsdato(),
        dagsats = this.dagsats(),
        antallDager = this.antallDager(),
        relatertTiltak = this.relatertTiltak(),
        relatertTiltakNavn = this.relatertTiltakNavn(),
        opprinneligTilDato = this.opprinneligTilDato(),
        gjelderFra = this.gjelderFra(),
        gjelderTil = this.gjelderTil(),
        satsKode = this.satsKode(),
        maskineltVedtak = this.maskineltVedtak(),
    )

// Kan være null for tiltakspenger, selv om det kanskje er litt rart?
private fun List<ArenaVedtakfaktaDTO>.beslutningsdato(): LocalDate? =
    this.find { it.vedtakfaktaKode == ArenaVedtakFakta.INNVF.name }?.vedtakfaktaVerdi
        .also { log.info { "${ArenaVedtakFakta.INNVF.name}: $it" } }
        ?.let { LocalDate.parse(it, DateTimeFormatter.ofPattern("dd-MM-yyyy")) }

private fun List<ArenaVedtakfaktaDTO>.dagsats(): Int? =
    this.find { it.vedtakfaktaKode == ArenaVedtakFakta.DAGS.name }?.vedtakfaktaVerdi
        .also { log.info { "${ArenaVedtakFakta.DAGS.name}: $it" } }
        ?.toInt()

private fun List<ArenaVedtakfaktaDTO>.antallBarn(): Int? =
    this.find { it.vedtakfaktaKode == ArenaVedtakFakta.BARNMSTON.name }?.vedtakfaktaVerdi
        .also { log.info { "${ArenaVedtakFakta.BARNMSTON.name}: $it" } }
        ?.toInt()

private fun List<ArenaVedtakfaktaDTO>.antallDager(): Double? =
    this.find { it.vedtakfaktaKode == ArenaVedtakFakta.DAGUTBTILT.name }?.vedtakfaktaVerdi
        .also { log.info { "${ArenaVedtakFakta.DAGUTBTILT.name}: $it" } }
        ?.toDouble()

private fun List<ArenaVedtakfaktaDTO>.relatertTiltak(): String? =
    this.find { it.vedtakfaktaKode == ArenaVedtakFakta.KODETILTAK.name }?.vedtakfaktaVerdi
        .also { log.info { "${ArenaVedtakFakta.KODETILTAK.name}: $it" } }

private fun List<ArenaVedtakfaktaDTO>.relatertTiltakNavn(): String? =
    this.find { it.vedtakfaktaKode == ArenaVedtakFakta.TILTAKNAVN.name }?.vedtakfaktaVerdi
        .also { log.info { "${ArenaVedtakFakta.TILTAKNAVN.name}: $it" } }

private fun List<ArenaVedtakfaktaDTO>.opprinneligTilDato(): LocalDate? =
    this.find { it.vedtakfaktaKode == ArenaVedtakFakta.OPPRTDATO.name }?.vedtakfaktaVerdi
        .also { log.info { "${ArenaVedtakFakta.OPPRTDATO.name}: $it" } }
        ?.let { LocalDate.parse(it, DateTimeFormatter.ofPattern("dd-MM-yyyy")) }

private fun List<ArenaVedtakfaktaDTO>.gjelderFra(): LocalDate? =
    this.find { it.vedtakfaktaKode == ArenaVedtakFakta.FDATO.name }?.vedtakfaktaVerdi
        .also { log.info { "${ArenaVedtakFakta.FDATO.name}: $it" } }
        ?.let { LocalDate.parse(it, DateTimeFormatter.ofPattern("dd-MM-yyyy")) }

private fun List<ArenaVedtakfaktaDTO>.gjelderTil(): LocalDate? =
    this.find { it.vedtakfaktaKode == ArenaVedtakFakta.TDATO.name }?.vedtakfaktaVerdi
        .also { log.info { "${ArenaVedtakFakta.TDATO.name}: $it" } }
        ?.let { LocalDate.parse(it, DateTimeFormatter.ofPattern("dd-MM-yyyy")) }

private fun List<ArenaVedtakfaktaDTO>.satsKode(): String? =
    this.find { it.vedtakfaktaKode == ArenaVedtakFakta.SATSKODE.name }?.vedtakfaktaVerdi
        .also { log.info { "${ArenaVedtakFakta.SATSKODE.name}: $it" } }

private fun List<ArenaVedtakfaktaDTO>.maskineltVedtak(): String? =
    this.find { it.vedtakfaktaKode == ArenaVedtakFakta.MASKVEDTAK.name }?.vedtakfaktaVerdi
        .also { log.info { "${ArenaVedtakFakta.MASKVEDTAK.name}: $it" } }
