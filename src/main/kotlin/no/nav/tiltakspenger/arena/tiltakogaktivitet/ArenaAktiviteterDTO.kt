@file:Suppress("ktlint:standard:no-semi", "ktlint:standard:trailing-comma-on-declaration-site")

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

        enum class Tiltaksnavn {
            AMBF1,
            KURS,
            ANNUTDANN,
            ABOPPF,
            ABUOPPF,
            ABIST,
            ABTBOPPF,
            LONNTILAAP,
            ARBFORB,
            AMO,
            AMOE,
            AMOB,
            AMOY,
            PRAKSORD,
            PRAKSKJERM,
            ARBRRHBAG,
            ARBRRHBSM,
            ARBRRHDAG,
            ARBRDAGSM,
            ARBRRDOGN,
            ARBDOGNSM,
            ASV,
            ARBTREN,
            ATG,
            AVKLARAG,
            AVKLARUS,
            AVKLARSP,
            AVKLARKV,
            AVKLARSV,
            BIA,
            BIO,
            BREVKURS,
            DIGIOPPARB,
            DIVTILT,
            ETAB,
            EKSPEBIST,
            ENKELAMO,
            ENKFAGYRKE,
            FLEKSJOBB,
            TILRTILSK,
            KAT,
            VALS,
            FORSAMOENK,
            FORSAMOGRU,
            FORSFAGENK,
            FORSFAGGRU,
            FORSHOYUTD,
            FORSOPPLEV,
            FUNKSJASS,
            GRUNNSKOLE,
            GRUPPEAMO,
            GRUFAGYRKE,
            HOYEREUTD,
            HOYSKOLE,
            INDJOBSTOT,
            IPSUNG,
            INDOPPFOLG,
            INKLUTILS,
            ITGRTILS,
            JOBBKLUBB,
            JOBBFOKUS,
            JOBBK,
            JOBBBONUS,
            JOBBSKAP,
            AMBF2,
            TESTING,
            STATLAERL,
            LONNTILS,
            REAKTUFOR,
            LONNTILL,
            MENTOR,
            MIDLONTIL,
            NETTAMO,
            NETTKURS,
            INST_S,
            NYTEST,
            INDOPPFAG,
            INDOPPFSP,
            PV,
            INDOPPRF,
            REFINO,
            SPA,
            SUPPEMP,
            SYSSLANG,
            YHEMMOFF,
            SYSSOFF,
            LONNTIL,
            TIDSUBLONN,
            AMBF3,
            TILRETTEL,
            TILPERBED,
            TILSJOBB,
            UFØREPENLØ,
            UTDYRK,
            UTDPERMVIK,
            VIKARBLED,
            UTBHLETTPS,
            UTBHPSLD,
            UTBHSAMLI,
            UTVAOONAV,
            UTVOPPFOPL,
            VARLONTIL,
            VATIAROR,
            VASV,
            VV,
            VIDRSKOLE,
            OPPLT2AAR;

            companion object {
                fun fromTekst(tekst: String): Tiltaksnavn {
                    return when (tekst) {
                        "AMB Avklaring (fase 1)" -> AMBF1
                        "Andre kurs" -> KURS
                        "Annen utdanning" -> ANNUTDANN
                        "Arbeid med bistand A oppfølging" -> ABOPPF
                        "Arbeid med bistand A utvidet oppfølging" -> ABUOPPF
                        "Arbeid med Bistand (AB)" -> ABIST
                        "Arbeid med bistand B" -> ABTBOPPF
                        "Arbeidsavklaringspenger som lønnstilskudd" -> LONNTILAAP
                        "Arbeidsforberedende trening (AFT)" -> ARBFORB
                        "Arbeidsmarkedsopplæring (AMO)" -> AMO
                        "Arbeidsmarkedsopplæring (AMO) enkeltplass" -> AMOE
                        "Arbeidsmarkedsopplæring (AMO) i bedrift" -> AMOB
                        "Arbeidsmarkedsopplæring (AMO) yrkeshemmede" -> AMOY
                        "Arbeidspraksis i ordinær virksomhet" -> PRAKSORD
                        "Arbeidspraksis i skjermet virksomhet" -> PRAKSKJERM
                        "Arbeidsrettet rehabilitering" -> ARBRRHBAG
                        "Arbeidsrettet rehabilitering - sykmeldt arbeidstaker" -> ARBRRHBSM
                        "Arbeidsrettet rehabilitering (dag)" -> ARBRRHDAG
                        "Arbeidsrettet rehabilitering (dag) - sykmeldt arbeidstaker" -> ARBRDAGSM
                        "Arbeidsrettet rehabilitering (døgn)" -> ARBRRDOGN
                        "Arbeidsrettet rehabilitering (døgn) - sykmeldt arbeidstaker" -> ARBDOGNSM
                        "Arbeidssamvirke (ASV)" -> ASV
                        "Arbeidstrening" -> ARBTREN
                        "Arbeidstreningsgrupper" -> ATG
                        "Avklaring" -> AVKLARAG
                        "Avklaring" -> AVKLARUS
                        "Avklaring - sykmeldt arbeidstaker" -> AVKLARSP
                        "Avklaring av kortere varighet" -> AVKLARKV
                        "Avklaring i skjermet virksomhet" -> AVKLARSV
                        "Bedriftsintern attføring" -> BIA
                        "Bedriftsintern opplæring (BIO)" -> BIO
                        "Brevkurs" -> BREVKURS
                        "Digitalt oppfølgingstiltak for arbeidsledige (jobbklubb)" -> DIGIOPPARB
                        "Diverse tiltak" -> DIVTILT
                        "Egenetablering" -> ETAB
                        "Ekspertbistand" -> EKSPEBIST
                        "Enkeltplass AMO" -> ENKELAMO
                        "Enkeltplass Fag- og yrkesopplæring VGS og høyere yrkesfaglig utdanning" -> ENKFAGYRKE
                        "Fleksibel jobb - lønnstilskudd av lengre varighet" -> FLEKSJOBB
                        "Forebyggings- og tilretteleggingstilskudd IA virksomheter og BHT-honorar" -> TILRTILSK
                        "Formidlingstjenester" -> KAT
                        "Formidlingstjenester - Ventelønn" -> VALS
                        "Forsøk AMO enkeltplass" -> FORSAMOENK
                        "Forsøk AMO gruppe" -> FORSAMOGRU
                        "Forsøk fag- og yrkesopplæring enkeltplass" -> FORSFAGENK
                        "Forsøk fag- og yrkesopplæring gruppe" -> FORSFAGGRU
                        "Forsøk høyere utdanning" -> FORSHOYUTD
                        "Forsøk opplæringstiltak av lengre varighet" -> FORSOPPLEV
                        "Funksjonsassistanse" -> FUNKSJASS
                        "Grunnskole" -> GRUNNSKOLE
                        "Gruppe AMO" -> GRUPPEAMO
                        "Gruppe Fag- og yrkesopplæring VGS og høyere yrkesfaglig utdanning" -> GRUFAGYRKE
                        "Høyere utdanning" -> HOYEREUTD
                        "Høyskole/Universitet" -> HOYSKOLE
                        "Individuell jobbstøtte (IPS)" -> INDJOBSTOT
                        "Individuell karrierestøtte (IPS Ung)" -> IPSUNG
                        "Individuelt oppfølgingstiltak" -> INDOPPFOLG
                        "Inkluderingstilskudd" -> INKLUTILS
                        "Integreringstilskudd" -> ITGRTILS
                        "Intern jobbklubb" -> JOBBKLUBB
                        "Jobbfokus/Utvidet formidlingsbistand" -> JOBBFOKUS
                        "Jobbklubb" -> JOBBK
                        "Jobbklubb med bonusordning" -> JOBBBONUS
                        "Jobbskapingsprosjekter" -> JOBBSKAP
                        "Kvalifisering i arbeidsmarkedsbedrift" -> AMBF2
                        "Lenes testtiltak" -> TESTING
                        "Lærlinger i statlige etater" -> STATLAERL
                        "Lønnstilskudd" -> LONNTILS
                        "Lønnstilskudd - reaktivisering av uførepensjonister" -> REAKTUFOR
                        "Lønnstilskudd av lengre varighet" -> LONNTILL
                        "Mentor" -> MENTOR
                        "Midlertidig lønnstilskudd" -> MIDLONTIL
                        "Nettbasert arbeidsmarkedsopplæring (AMO)" -> NETTAMO
                        "Nettkurs" -> NETTKURS
                        "Nye plasser institusjonelle tiltak" -> INST_S
                        "Nytt testtiltak" -> NYTEST
                        "Oppfølging" -> INDOPPFAG
                        "Oppfølging - sykmeldt arbeidstaker" -> INDOPPFSP
                        "Produksjonsverksted (PV)" -> PV
                        "Resultatbasert finansiering av formidlingsbistand" -> INDOPPRF
                        "Resultatbasert finansiering av oppfølging" -> REFINO
                        "Spa prosjekter" -> SPA
                        "Supported Employment" -> SUPPEMP
                        "Sysselsettingstiltak for langtidsledige" -> SYSSLANG
                        "Sysselsettingstiltak for yrkeshemmede" -> YHEMMOFF
                        "Sysselsettingstiltak i offentlig sektor for yrkeshemmede" -> SYSSOFF
                        "Tidsbegrenset lønnstilskudd" -> LONNTIL
                        "Tidsubestemt lønnstilskudd" -> TIDSUBLONN
                        "Tilrettelagt arbeid i arbeidsmarkedsbedrift" -> AMBF3
                        "Tilrettelegging for arbeidstaker" -> TILRETTEL
                        "Tilretteleggingstilskudd for arbeidssøker" -> TILPERBED
                        "Tilskudd til sommerjobb" -> TILSJOBB
                        "Uførepensjon som lønnstilskudd" -> UFØREPENLØ
                        "Utdanning" -> UTDYRK
                        "Utdanningspermisjoner" -> UTDPERMVIK
                        "Utdanningsvikariater" -> VIKARBLED
                        "Utredning/behandling lettere psykiske lidelser" -> UTBHLETTPS
                        "Utredning/behandling lettere psykiske og sammensatte lidelser" -> UTBHPSLD
                        "Utredning/behandling sammensatte lidelser" -> UTBHSAMLI
                        "Arbeid med støtte" -> UTVAOONAV
                        "Tilpasset jobbstøtte",
                        "Utvidet oppfølging i opplæring" -> UTVOPPFOPL
                        "Varig lønnstilskudd" -> VARLONTIL
                        "Varig tilrettelagt arbeid i ordinær virksomhet" -> VATIAROR
                        "Varig tilrettelagt arbeid i skjermet virksomhet" -> VASV
                        "Varig vernet arbeid (VVA)" -> VV
                        "Videregående skole" -> VIDRSKOLE
                        "2-årig opplæringstiltak" -> OPPLT2AAR
                        else -> throw IllegalArgumentException(
                            "Tiltaksnavn '$tekst' ikke funnet, sjekk lovlige verdier i Arena",
                        )
                    }
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
            // f.eks: 13:00:00
            val startKlokkeslett: String?,
            val sluttDato: LocalDate,
            // f.eks: 14:00:00
            val sluttKlokkeslett: String?,
            val sted: String?,
        )
    }
}
