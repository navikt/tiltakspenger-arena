package no.nav.tiltakspenger.arena.tiltakogaktivitet

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText
import no.nav.tiltakspenger.arena.felles.XML_TEXT_ELEMENT_NAME
import java.time.LocalDate

data class ArenaAktiviteterDTO(
    val response: Response
) {
    // Denne klassen er sjekket opp mot
    // https://confluence.adeo.no/display/ARENA/Arena+-+Tjeneste+Webservice+-+TiltakOgAktivitet_v1#ArenaTjenesteWebserviceTiltakOgAktivitet_v1-HentTiltakOgAktiviteterForBrukerResponse
    // De variablene som er merket som mandatory der er satt til ikke å være nullable her
    data class Response(
        val tiltaksaktivitetListe: List<Tiltaksaktivitet> = emptyList(),
        val gruppeaktivitetListe: List<Gruppeaktivitet> = emptyList(),
        val utdanningsaktivitetListe: List<Utdanningsaktivitet> = emptyList(),
    )

    data class Tiltaksaktivitet(
        @JsonDeserialize(using = ArenaTiltaksnavnDeserializer::class)
        val tiltaksnavn: Tiltaksnavn,
        val aktivitetId: String,
        val tiltakLokaltNavn: String?,
        val arrangoer: String?,
        val bedriftsnummer: String?,
        val deltakelsePeriode: DeltakelsesPeriode?,
        @JsonDeserialize(using = ArenaFloatDeserializer::class)
        val deltakelseProsent: Float?,
        val deltakerStatus: DeltakerStatus,
        val statusSistEndret: LocalDate?,
        val begrunnelseInnsoeking: String?,
        @JsonDeserialize(using = ArenaFloatDeserializer::class)
        val antallDagerPerUke: Float?,
    ) {

        data class DeltakelsesPeriode(
            val fom: LocalDate?,
            val tom: LocalDate?,
        )

        data class DeltakerStatus(
            @JacksonXmlProperty(localName = "termnavn", isAttribute = true)
            val statusNavn: String,
            @JsonProperty(XML_TEXT_ELEMENT_NAME)
            @JacksonXmlText
            val status: DeltakerStatusType
        )

        enum class Tiltaksnavn(val tekst: String) {

            MENTOR("Mentor"),
            MIDLONTIL("Midlertidig lønnstilskudd"),
            PV("Produksjonsverksted (PV)"),
            REFINO("Resultatbasert finansiering av oppfølging"),
            SUPPEMP("Supported Employment"),
            ETAB("Egenetablering"),
            FORSAMOENK("Forsøk AMO enkeltplass"),
            FORSAMOGRU("Forsøk AMO gruppe"),
            FORSFAGENK("Forsøk fag- og yrkesopplæring enkeltplass"),
            FORSFAGGRU("Forsøk fag- og yrkesopplæring gruppe"),
            FORSHOYUTD("Forsøk høyere utdanning"),
            FUNKSJASS("Funksjonsassistanse"),
            GRUFAGYRKE("Gruppe Fag- og yrkesopplæring VGS og høyere yrkesfaglig utdanning"),
            GRUPPEAMO("Gruppe AMO"),
            HOYEREUTD("Høyere utdanning"),
            INDJOBSTOT("Individuell jobbstøtte (IPS)"),
            INDOPPFAG("Oppfølging"),
            INDOPPRF("Resultatbasert finansiering av formidlingsbistand"),
            INKLUTILS("Inkluderingstilskudd"),
            IPSUNG("Individuell karrierestøtte (IPS Ung)"),
            JOBBK("Jobbklubb"),
            LONNTILAAP("Arbeidsavklaringspenger som lønnstilskudd"),
            AMBF2("Kvalifisering i arbeidsmarkedsbedrift"),
            ARBFORB("Arbeidsforberedende trening (AFT)"),
            ARBRRHDAG("Arbeidsrettet rehabilitering (dag)"),
            ARBTREN("Arbeidstrening"),
            AVKLARAG("Avklaring"),
            BIO("Bedriftsintern opplæring (BIO)"),
            DIGIOPPARB("Digitalt oppfølgingstiltak for arbeidsledige (jobbklubb)"),
            EKSPEBIST("Ekspertbistand"),
            ENKELAMO("Enkeltplass AMO"),
            ENKFAGYRKE("Enkeltplass Fag- og yrkesopplæring VGS og høyere yrkesfaglig utdanning"),
            TIDSUBLONN("Tidsubestemt lønnstilskudd"),
            TILPERBED("Tilretteleggingstilskudd for arbeidssøker"),
            TILRTILSK("Forebyggings- og tilretteleggingstilskudd IA virksomheter og BHT-honorar"),
            UTVAOONAV("Utvidet oppfølging i NAV"),
            UTVOPPFOPL("Utvidet oppfølging i opplæring"),
            VARLONTIL("Varig lønnstilskudd"),
            VASV("Varig tilrettelagt arbeid i skjermet virksomhet"),
            VATIAROR("Varig tilrettelagt arbeid i ordinær virksomhet"),
            VV("Varig vernet arbeid (VVA)"),
            AMO("AMO"),
            PRAKSKJERM("Avklaring i skjermet virksomhet");

            companion object {
                fun fromTekst(tekst: String): Tiltaksnavn {
                    return Tiltaksnavn.values().find { it.tekst == tekst }
                        ?: throw IllegalArgumentException(
                            "Tiltaksnavn $tekst ikke funnet, sjekk lovlige verdier i Arena"
                        )
                }
            }
        }

        enum class DeltakerStatusType(val tekst: String) {
            AKTUELL("Aktuell"),
            AVSLAG("Fått avslag"),
            DELAVB("Deltakelse avbrutt"),
            FULLF("Fullført"),
            GJENN("Gjennomføres"),
            GJENN_AVB("Gjennomføring avbrutt"),
            GJENN_AVL("Gjennomføring avlyst"),
            IKKAKTUELL("Ikke aktuell"),
            IKKEM("Ikke møtt"),
            INFOMOETE("Informasjonsmøte"),
            JATAKK("Takket ja til tilbud"),
            NEITAKK("Takket nei til tilbud"),
            TILBUD("Godkjent tiltaksplass"),
            VENTELISTE("Venteliste")
        }
    }

    data class Utdanningsaktivitet(
        val aktivitetstype: String,
        val aktivitetId: String,
        val beskrivelse: String?,
        val aktivitetPeriode: AktivitetPeriode?,
    ) {
        data class AktivitetPeriode(
            val fom: LocalDate?,
            val tom: LocalDate?,
        )
    }

    data class Gruppeaktivitet(
        val aktivitetstype: String,
        val aktivitetId: String,
        val beskrivelse: String?,
        val status: String?,
        val moeteplanListe: List<Moteplan>,
    ) {
        data class Moteplan(
            val startDato: LocalDate,
            val startKlokkeslett: String?, // f.eks: 13:00:00
            val sluttDato: LocalDate,
            val sluttKlokkeslett: String?, // f.eks: 14:00:00
            val sted: String?,
        )
    }
}
