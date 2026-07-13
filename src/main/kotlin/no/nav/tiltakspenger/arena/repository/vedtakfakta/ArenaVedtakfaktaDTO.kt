package no.nav.tiltakspenger.arena.repository.vedtakfakta

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tiltakspenger.arena.Avvikstype
import no.nav.tiltakspenger.arena.SE_SIKKERLOGG
import no.nav.tiltakspenger.libs.logging.Sikkerlogg
import java.math.RoundingMode
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

private val ARENA_DATOFORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy")

/**
 * Vedtakfaktakodene vi bruker fra Arena-tabellen VEDTAKFAKTA. Kdoc-en på hvert felt er
 * vedtakfaktanavnet fra tabellen VEDTAKFAKTATYPE (jf. SQL-ene øverst i fila).
 */
// TODO: Vurder å gå bort fra denne enumen nå som vi har lesetilgang til tabellen VEDTAKFAKTATYPE
//  (vedtakfaktakode -> vedtakfaktanavn). Da kan gyldige koder og navn slås opp fra Arena i stedet
//  for å vedlikeholdes manuelt her, og vi unngår at koder som endres eller legges til i Arena
//  stille faller utenfor. Enumen gir på sin side kompileringssikkerhet for kodene vi faktisk
//  mapper på, så en mellomvei er å beholde enumen og heller validere den mot VEDTAKFAKTATYPE
//  i en test mot testcontainer-basen.
enum class ArenaVedtakFakta {
    /** DAGSATS */
    DAGS,

    /** Valgt sats */
    SATSKODE,

    /** Knyttet til tiltak */
    TILTAKNAVN,

    /** Maskinelt vedtak */
    MASKVEDTAK,

    /** Opprinnelig til-dato */
    OPPRTDATO,

    /** Gjelder til */
    TDATO,

    /** Antall dager med utbetaling: antall dager i løpet av en meldekortperiode det skal utbetales ytelser */
    DAGUTBTILT,

    /** Gjelder fra */
    FDATO,

    /** VEDTAKSDATO */
    INNVF,

    /**
     * Relatert tiltak.
     *
     * https://nav-it.slack.com/archives/C01DE03F4LS/p1710748569601879
     *
     * KODETILTAK inneholder referanse til tiltaksgjennomføring (tiltakgjennomforing.tiltakgjennomforing_id)
     */
    KODETILTAK,

    /** Antall barn med stønad. Bare aktuell for barnetillegg */
    BARNMSTON,

    /** Antall utbetalinger */
    ANTALL,

    /** Beløp per utbetaling */
    BEL,

    /** Alternativ betalingsmottaker */
    EKSTID,
}

data class ArenaVedtakfaktaDTO(
    val vedtakId: Long,
    val vedtakfaktaKode: String,
    val vedtakfaktaVerdi: String?,
)

/**
 * Kontekst for logglinjene som skrives når vedtakfakta mappes. [fnr] er PII og logges kun til
 * sikkerlogg; [sakId] og [saksnummer] logges også til vanlig logg (sammen med vedtakId fra radene).
 */
data class VedtakfaktaLoggkontekst(
    val fnr: String? = null,
    val sakId: Long? = null,
    val saksnummer: String? = null,
) {
    /** [fnr] er PII og skal ikke bli med om noen logger hele objektet. Samme maskering som [no.nav.tiltakspenger.libs.common.Fnr]. */
    override fun toString() = "VedtakfaktaLoggkontekst(fnr=***********, sakId=$sakId, saksnummer=$saksnummer)"
}

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
    val tiltakGjennomføringsId: String?,
    val relatertTiltakNavn: String?,
    val opprinneligTilDato: LocalDate?,
    val antallBarn: Int?,
    val gjelderFra: LocalDate?,
    val gjelderTil: LocalDate?,
    val satsKode: String?,
    val maskineltVedtak: String?,
)

data class ArenaUtbetalingshistorikkVedtakfaktaDTO(
    val dagsats: Int?,
    val gjelderFra: LocalDate?,
    val gjelderTil: LocalDate?,
    val antallUtbetalinger: Int?,
    val belopPerUtbetalinger: Int?,
    val alternativBetalingsmottaker: String?,
)

fun List<ArenaVedtakfaktaDTO>.toArenaTiltakspengerVedtakfaktaDTO(kontekst: VedtakfaktaLoggkontekst): ArenaTiltakspengerVedtakfaktaDTO {
    val avvik = mutableListOf<String>()
    return ArenaTiltakspengerVedtakfaktaDTO(
        beslutningsdato = dato(ArenaVedtakFakta.INNVF),
        dagsats = heltall(ArenaVedtakFakta.DAGS, avvik),
        antallDager = desimaltall(ArenaVedtakFakta.DAGUTBTILT),
        relatertTiltak = tekst(ArenaVedtakFakta.KODETILTAK),
        relatertTiltakNavn = tekst(ArenaVedtakFakta.TILTAKNAVN),
        opprinneligTilDato = dato(ArenaVedtakFakta.OPPRTDATO),
        gjelderFra = dato(ArenaVedtakFakta.FDATO),
        gjelderTil = dato(ArenaVedtakFakta.TDATO),
        satsKode = tekst(ArenaVedtakFakta.SATSKODE),
        maskineltVedtak = tekst(ArenaVedtakFakta.MASKVEDTAK),
    ).also { loggMapping("tiltakspenger-vedtakfakta", it, kontekst, avvik) }
}

fun List<ArenaVedtakfaktaDTO>.toArenaBarnetilleggVedtakfaktaDTO(kontekst: VedtakfaktaLoggkontekst): ArenaBarnetilleggVedtakfaktaDTO {
    val avvik = mutableListOf<String>()
    return ArenaBarnetilleggVedtakfaktaDTO(
        beslutningsdato = dato(ArenaVedtakFakta.INNVF),
        dagsats = heltall(ArenaVedtakFakta.DAGS, avvik),
        antallDager = desimaltall(ArenaVedtakFakta.DAGUTBTILT),
        tiltakGjennomføringsId = tekst(ArenaVedtakFakta.KODETILTAK),
        relatertTiltakNavn = tekst(ArenaVedtakFakta.TILTAKNAVN),
        opprinneligTilDato = dato(ArenaVedtakFakta.OPPRTDATO),
        antallBarn = heltall(ArenaVedtakFakta.BARNMSTON, avvik),
        gjelderFra = dato(ArenaVedtakFakta.FDATO),
        gjelderTil = dato(ArenaVedtakFakta.TDATO),
        satsKode = tekst(ArenaVedtakFakta.SATSKODE),
        maskineltVedtak = tekst(ArenaVedtakFakta.MASKVEDTAK),
    ).also { loggMapping("barnetillegg-vedtakfakta", it, kontekst, avvik) }
}

fun List<ArenaVedtakfaktaDTO>.tilArenaUtbetalingshistorikkVedtakfaktaDTO(kontekst: VedtakfaktaLoggkontekst): ArenaUtbetalingshistorikkVedtakfaktaDTO {
    val avvik = mutableListOf<String>()
    return ArenaUtbetalingshistorikkVedtakfaktaDTO(
        dagsats = heltall(ArenaVedtakFakta.DAGS, avvik),
        gjelderFra = dato(ArenaVedtakFakta.FDATO),
        gjelderTil = dato(ArenaVedtakFakta.TDATO),
        antallUtbetalinger = heltall(ArenaVedtakFakta.ANTALL, avvik),
        belopPerUtbetalinger = heltall(ArenaVedtakFakta.BEL, avvik),
        alternativBetalingsmottaker = tekst(ArenaVedtakFakta.EKSTID),
    ).also { loggMapping("utbetalingshistorikk-vedtakfakta", it, kontekst, avvik) }
}

/**
 * Logger kun når det finnes avvik: én warn-linje med resultatet og avvikene bakt inn, og samme
 * linje + ident til sikkerlogg. Når alt gikk bra logges ingenting her — request-linjen i ruta
 * dekker suksess.
 */
private fun List<ArenaVedtakfaktaDTO>.loggMapping(
    type: String,
    resultat: Any,
    kontekst: VedtakfaktaLoggkontekst,
    avvik: List<String>,
) {
    if (avvik.isEmpty()) return
    val melding = "${Avvikstype.DESIMAL_HELTALL}: hentet $type for ${kontekstStreng(kontekst)}: $resultat. Avvik: ${avvik.joinToString("; ")}"
    log.warn { "$melding. $SE_SIKKERLOGG" }
    Sikkerlogg.warn { "$melding. Ident: ${kontekst.fnr}" }
}

private fun List<ArenaVedtakfaktaDTO>.kontekstStreng(kontekst: VedtakfaktaLoggkontekst): String =
    listOfNotNull(
        firstOrNull()?.vedtakId?.let { "vedtakId $it" },
        kontekst.sakId?.let { "sakId $it" },
        kontekst.saksnummer?.let { "saksnummer $it" },
    ).joinToString(", ").ifEmpty { "ukjent vedtak" }

private fun List<ArenaVedtakfaktaDTO>.tekst(fakta: ArenaVedtakFakta): String? =
    find { it.vedtakfaktaKode == fakta.name }?.vedtakfaktaVerdi

private fun List<ArenaVedtakfaktaDTO>.dato(fakta: ArenaVedtakFakta): LocalDate? =
    tekst(fakta)?.let { LocalDate.parse(it, ARENA_DATOFORMAT) }

private fun List<ArenaVedtakfaktaDTO>.desimaltall(fakta: ArenaVedtakFakta): Double? =
    tekst(fakta)?.toDouble()

// Vedtakfakta lagres som tekst i Arena, og felter vi forventer er heltall kan inneholde
// desimaltall (sett i prod: BARNMSTON=0.961538461538462). Runder av til nærmeste heltall
// og registrerer et avvik som tas med i den ene logglinjen for mappingen.
private fun List<ArenaVedtakfaktaDTO>.heltall(fakta: ArenaVedtakFakta, avvik: MutableList<String>): Int? =
    tekst(fakta)?.let { verdi ->
        val tall = verdi.toBigDecimal()
        tall.setScale(0, RoundingMode.HALF_UP).intValueExact().also { avrundet ->
            if (tall.stripTrailingZeros().scale() > 0) {
                avvik += "${fakta.name} hadde desimalverdien $verdi, rundet av til $avrundet"
            }
        }
    }
