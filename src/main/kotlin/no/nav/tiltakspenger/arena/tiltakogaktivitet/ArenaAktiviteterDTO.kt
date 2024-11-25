@file:Suppress("ktlint:no-semi", "ktlint:trailing-comma-on-declaration-site")

package no.nav.tiltakspenger.arena.tiltakogaktivitet

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText
import no.nav.tiltakspenger.arena.felles.XML_TEXT_ELEMENT_NAME
import java.time.LocalDate

data class ArenaAktiviteterDTO(
    val response: Response,
) {
    // Lenke til dokumentasjon i confluence
    // Aktiviteter: https://confluence.adeo.no/pages/viewpage.action?pageId=470748287
    // De variablene som er merket som mandatory der er satt til ikke å være nullable her
    data class Response(
        val tiltaksaktivitetListe: List<Tiltaksaktivitet> = emptyList(),
        val gruppeaktivitetListe: List<Gruppeaktivitet> = emptyList(),
        val utdanningsaktivitetListe: List<Utdanningsaktivitet> = emptyList(),
    )

    data class Tiltaksaktivitet(
        @JsonDeserialize(using = ArenaTiltaksnavnDeserializer::class)
        val tiltaksnavn: Tiltaksnavn,
        /**
         * 'TA' + TILTAKDELTAKER.TILTAKDELTAKER_ID
         * Det er en unik id for en tiltakdeltaker, dvs én person på ett tiltak (tiltaksgjennomføring).
         * */
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
            val status: DeltakerStatusType,
        )

        enum class Tiltaksnavn(val tekst: String) {
            AMBF1("AMB Avklaring (fase 1)"),
            KURS("Andre kurs"),
            ANNUTDANN("Annen utdanning"),
            ABOPPF("Arbeid med bistand A oppfølging"),
            ABUOPPF("Arbeid med bistand A utvidet oppfølging"),
            ABIST("Arbeid med Bistand (AB)"),
            ABTBOPPF("Arbeid med bistand B"),
            LONNTILAAP("Arbeidsavklaringspenger som lønnstilskudd"),
            ARBFORB("Arbeidsforberedende trening (AFT)"),
            AMO("Arbeidsmarkedsopplæring (AMO)"),
            AMOE("Arbeidsmarkedsopplæring (AMO) enkeltplass"),
            AMOB("Arbeidsmarkedsopplæring (AMO) i bedrift"),
            AMOY("Arbeidsmarkedsopplæring (AMO) yrkeshemmede"),
            PRAKSORD("Arbeidspraksis i ordinær virksomhet"),
            PRAKSKJERM("Arbeidspraksis i skjermet virksomhet"),
            ARBRRHBAG("Arbeidsrettet rehabilitering"),
            ARBRRHBSM("Arbeidsrettet rehabilitering - sykmeldt arbeidstaker"),
            ARBRRHDAG("Arbeidsrettet rehabilitering (dag)"),
            ARBRDAGSM("Arbeidsrettet rehabilitering (dag) - sykmeldt arbeidstaker"),
            ARBRRDOGN("Arbeidsrettet rehabilitering (døgn)"),
            ARBDOGNSM("Arbeidsrettet rehabilitering (døgn) - sykmeldt arbeidstaker"),
            ASV("Arbeidssamvirke (ASV)"),
            ARBTREN("Arbeidstrening"),
            ATG("Arbeidstreningsgrupper"),
            AVKLARAG("Avklaring"),
            AVKLARUS("Avklaring"),
            AVKLARSP("Avklaring - sykmeldt arbeidstaker"),
            AVKLARKV("Avklaring av kortere varighet"),
            AVKLARSV("Avklaring i skjermet virksomhet"),
            BIA("Bedriftsintern attføring"),
            BIO("Bedriftsintern opplæring (BIO)"),
            BREVKURS("Brevkurs"),
            DIGIOPPARB("Digitalt jobbsøkerkurs"),
            DIVTILT("Diverse tiltak"),
            ETAB("Egenetablering"),
            EKSPEBIST("Ekspertbistand"),
            ENKELAMO("Enkeltplass AMO"),
            ENKFAGYRKE("Enkeltplass Fag- og yrkesopplæring VGS og høyere yrkesfaglig utdanning"),
            FLEKSJOBB("Fleksibel jobb - lønnstilskudd av lengre varighet"),
            TILRTILSK("Forebyggings- og tilretteleggingstilskudd IA virksomheter og BHT-honorar"),
            KAT("Formidlingstjenester"),
            VALS("Formidlingstjenester - Ventelønn"),
            FORSAMOENK("Forsøk AMO enkeltplass"),
            FORSAMOGRU("Forsøk AMO gruppe"),
            FORSFAGENK("Forsøk fag- og yrkesopplæring enkeltplass"),
            FORSFAGGRU("Forsøk fag- og yrkesopplæring gruppe"),
            FORSHOYUTD("Forsøk høyere utdanning"),
            FORSOPPLEV("Forsøk opplæringstiltak av lengre varighet"),
            FUNKSJASS("Funksjonsassistanse"),
            GRUNNSKOLE("Grunnskole"),
            GRUPPEAMO("Gruppe AMO"),
            GRUFAGYRKE("Gruppe Fag- og yrkesopplæring VGS og høyere yrkesfaglig utdanning"),
            HOYEREUTD("Høyere utdanning"),
            HOYSKOLE("Høyskole/Universitet"),
            INDJOBSTOT("Individuell jobbstøtte (IPS)"),
            IPSUNG("Individuell karrierestøtte (IPS Ung)"),
            INDOPPFOLG("Individuelt oppfølgingstiltak"),
            INKLUTILS("Inkluderingstilskudd"),
            ITGRTILS("Integreringstilskudd"),
            JOBBKLUBB("Intern jobbklubb"),
            JOBBFOKUS("Jobbfokus/Utvidet formidlingsbistand"),
            JOBBK("Jobbklubb"),
            JOBBBONUS("Jobbklubb med bonusordning"),
            JOBBSKAP("Jobbskapingsprosjekter"),
            AMBF2("Kvalifisering i arbeidsmarkedsbedrift"),
            TESTING("Lenes testtiltak"),
            STATLAERL("Lærlinger i statlige etater"),
            LONNTILS("Lønnstilskudd"),
            REAKTUFOR("Lønnstilskudd - reaktivisering av uførepensjonister"),
            LONNTILL("Lønnstilskudd av lengre varighet"),
            MENTOR("Mentor"),
            MIDLONTIL("Midlertidig lønnstilskudd"),
            NETTAMO("Nettbasert arbeidsmarkedsopplæring (AMO)"),
            NETTKURS("Nettkurs"),
            INST_S("Nye plasser institusjonelle tiltak"),
            NYTEST("Nytt testtiltak"),
            INDOPPFAG("Oppfølging"),
            INDOPPFSP("Oppfølging - sykmeldt arbeidstaker"),
            PV("Produksjonsverksted (PV)"),
            INDOPPRF("Resultatbasert finansiering av formidlingsbistand"),
            REFINO("Resultatbasert finansiering av oppfølging"),
            SPA("Spa prosjekter"),
            SUPPEMP("Supported Employment"),
            SYSSLANG("Sysselsettingstiltak for langtidsledige"),
            YHEMMOFF("Sysselsettingstiltak for yrkeshemmede"),
            SYSSOFF("Sysselsettingstiltak i offentlig sektor for yrkeshemmede"),
            LONNTIL("Tidsbegrenset lønnstilskudd"),
            TIDSUBLONN("Tidsubestemt lønnstilskudd"),
            AMBF3("Tilrettelagt arbeid i arbeidsmarkedsbedrift"),
            TILRETTEL("Tilrettelegging for arbeidstaker"),
            TILPERBED("Tilretteleggingstilskudd for arbeidssøker"),
            TILSJOBB("Tilskudd til sommerjobb"),
            UFØREPENLØ("Uførepensjon som lønnstilskudd"),
            UTDYRK("Utdanning"),
            UTDPERMVIK("Utdanningspermisjoner"),
            VIKARBLED("Utdanningsvikariater"),
            UTBHLETTPS("Utredning/behandling lettere psykiske lidelser"),
            UTBHPSLD("Utredning/behandling lettere psykiske og sammensatte lidelser"),
            UTBHSAMLI("Utredning/behandling sammensatte lidelser"),
            UTVAOONAV("Arbeid med støtte"),
            UTVOPPFOPL("Utvidet oppfølging i opplæring"),
            VARLONTIL("Varig lønnstilskudd"),
            VATIAROR("Varig tilrettelagt arbeid i ordinær virksomhet"),
            VASV("Varig tilrettelagt arbeid i skjermet virksomhet"),
            VV("Varig vernet arbeid (VVA)"),
            VIDRSKOLE("Videregående skole"),
            OPPLT2AAR("2-årig opplæringstiltak");

            companion object {
                fun fromTekst(tekst: String): Tiltaksnavn {
                    return Tiltaksnavn.values().find { it.tekst == tekst }
                        ?: throw IllegalArgumentException(
                            "Tiltaksnavn '$tekst' ikke funnet, sjekk lovlige verdier i Arena",
                        )
                }
            }
        }

        enum class DeltakerStatusType(
            val tekst: String,
        ) {
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
            VENTELISTE("Venteliste"),
            FEILREG("Feilregistrert");
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
