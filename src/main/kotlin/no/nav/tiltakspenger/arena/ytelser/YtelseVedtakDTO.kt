package no.nav.tiltakspenger.arena.ytelser

import no.nav.tiltakspenger.arena.ytelser.YtelseSakDTO.YtelseSakYtelsetype
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.Vedtak
import java.time.LocalDate

data class YtelseVedtakDTO(
    val beslutningsDato: LocalDate? = null,
    val periodetypeForYtelse: YtelseVedtakPeriodeTypeForYtelse? = null,
    val vedtaksperiodeFom: LocalDate? = null,
    val vedtaksperiodeTom: LocalDate? = null,
    val vedtaksType: YtelseVedtakVedtakstype? = null,
    val status: YtelseVedtakStatus? = null,
) {
    companion object {
        fun of(vedtakListe: List<Vedtak>): List<YtelseVedtakDTO> =
            vedtakListe.map { vedtak ->
                YtelseVedtakDTO(
                    beslutningsDato = vedtak.beslutningsdato,
                    periodetypeForYtelse = vedtak.periodetypeForYtelse?.let {
                        YtelseVedtakPeriodeTypeForYtelse.fromNavn(it)
                    },
                    vedtaksperiodeFom = vedtak.vedtaksperiode.fom,
                    vedtaksperiodeTom = vedtak.vedtaksperiode.tom,
                    vedtaksType = vedtak.vedtakstype?.let { YtelseVedtakVedtakstype.fromNavn(it) },
                    status = vedtak.status?.let { YtelseVedtakStatus.fromNavn(it) }
                )
            }
    }

    enum class YtelseVedtakPeriodeTypeForYtelse(val navn: String) {
        E("Endring"),
        F("Forlenget ventetid"), // Gjelder ikke tiltakspenger
        G("Gjenopptak"),
        N("Annuller sanksjon"), // Gjelder ikke tiltakspenger
        O("Ny rettighet"),
        S("Stans"),
        A("Reaksjon"), // Står ikke listet opp i dokumentasjonen..
        K("Kontroll"), // Står ikke listet opp i dokumentasjonen..
        M("Omgjør reaksjon"), // Står ikke listet opp i dokumentasjonen..
        R("Revurdering"), // Står ikke listet opp i dokumentasjonen..
        T("Tidsbegrenset bortfall"); // Gjelder ikke tiltakspenger

        companion object {
            fun fromNavn(n: String): YtelseVedtakPeriodeTypeForYtelse =
                YtelseVedtakPeriodeTypeForYtelse.values().firstOrNull { it.navn == n }
                    ?: throw IllegalArgumentException("Ukjent YtelseVedtakPeriodeTypeForYtelse $n")
        }
    }

    enum class YtelseVedtakVedtakstype(val navn: String, val ytelseSakDTOYtelsetype: YtelseSakYtelsetype) {
        AAP("Arbeidsavklaringspenger", YtelseSakYtelsetype.AA),
        DAGO("Ordinære dagpenger", YtelseSakYtelsetype.DAGP),
        PERM("Dagpenger under permitteringer", YtelseSakYtelsetype.DAGP),
        FISK("Dagp. v/perm fra fiskeindustri", YtelseSakYtelsetype.DAGP),
        LONN("Lønnsgarantimidler - dagpenger", YtelseSakYtelsetype.DAGP),
        BASI("Tiltakspenger (basisytelse før 2014)", YtelseSakYtelsetype.INDIV),
        AATFOR("Tvungen forvaltning", YtelseSakYtelsetype.ANNET),
        AAUNGUFOR("Ung ufør", YtelseSakYtelsetype.ANNET),
        AA115("§11-5 nedsatt arbeidsevne", YtelseSakYtelsetype.ANNET),
        AA116("§11-6 behov for bistand", YtelseSakYtelsetype.ANNET),
        ABOUT("Boutgifter", YtelseSakYtelsetype.ANNET),
        ADAGR("Daglige reiseutgifter", YtelseSakYtelsetype.ANNET),
        AFLYT("Flytting", YtelseSakYtelsetype.ANNET),
        AHJMR("Hjemreise", YtelseSakYtelsetype.ANNET),
        ANKE("Anke", YtelseSakYtelsetype.ANNET),
        ARBT("Arbeidstreningplass", YtelseSakYtelsetype.ANNET),
        ATIF("Tilsyn - familiemedlemmer", YtelseSakYtelsetype.ANNET),
        ATIO("Tilsyn - barn over 10 år", YtelseSakYtelsetype.ANNET),
        ATIU("Tilsyn - barn under 10 år", YtelseSakYtelsetype.ANNET),
        ATTF("§11-6, nødvendig og hensiktsmessig tiltak", YtelseSakYtelsetype.ANNET),
        ATTK("§11-5, sykdom, skade eller lyte", YtelseSakYtelsetype.ANNET),
        ATTP("Attføringspenger", YtelseSakYtelsetype.ANNET),
        AUNDM("Bøker og undervisningsmatriell", YtelseSakYtelsetype.ANNET),
        BEHOV("Behovsvurdering", YtelseSakYtelsetype.ANNET),
        BIST14A("Bistandsbehov §14a", YtelseSakYtelsetype.ANNET),
        BORT("Borteboertillegg", YtelseSakYtelsetype.ANNET),
        BOUT("Boutgifter", YtelseSakYtelsetype.ANNET),
        BREI("MOB-Besøksreise", YtelseSakYtelsetype.ANNET),
        BTIF("Barnetilsyn - familiemedlemmer", YtelseSakYtelsetype.ANNET),
        BTIL("Barnetillegg", YtelseSakYtelsetype.ANNET),
        BTIO("Barnetilsyn - barn over 10 år", YtelseSakYtelsetype.ANNET),
        BTIU("Barnetilsyn - barn under 10 år", YtelseSakYtelsetype.ANNET),
        DAGR("Daglige reiseutgifter", YtelseSakYtelsetype.ANNET),
        DEKS("Eksport - dagpenger", YtelseSakYtelsetype.ANNET),
        DIMP("Import (E303 inn)", YtelseSakYtelsetype.ANNET),
        EKSG("Eksamensgebyr", YtelseSakYtelsetype.ANNET),
        FADD("Fadder", YtelseSakYtelsetype.ANNET),
        FLYT("Flytting", YtelseSakYtelsetype.ANNET),
        FREI("MOB-Fremreise", YtelseSakYtelsetype.ANNET),
        FRI_MK_AAP("Fritak fra å sende meldekort AAP", YtelseSakYtelsetype.ANNET),
        FRI_MK_IND("Fritak fra å sende meldekort individstønad", YtelseSakYtelsetype.ANNET),
        FSTO("MOB-Flyttestønad", YtelseSakYtelsetype.ANNET),
        HJMR("Hjemreise", YtelseSakYtelsetype.ANNET),
        HREI("MOB-Hjemreise", YtelseSakYtelsetype.ANNET),
        HUSH("Husholdsutgifter", YtelseSakYtelsetype.ANNET),
        IDAG("Reisetillegg", YtelseSakYtelsetype.ANNET),
        IEKS("Eksamensgebyr", YtelseSakYtelsetype.ANNET),
        IFLY("MOB-Flyttehjelp", YtelseSakYtelsetype.ANNET),
        INDIVFADD("Individstønad fadder", YtelseSakYtelsetype.ANNET),
        IREI("Hjemreise", YtelseSakYtelsetype.ANNET),
        ISEM("Semesteravgift", YtelseSakYtelsetype.ANNET),
        ISKO("Skolepenger", YtelseSakYtelsetype.ANNET),
        IUND("Bøker og undervisningsmatr.", YtelseSakYtelsetype.ANNET),
        KLAG1("Klage underinstans", YtelseSakYtelsetype.ANNET),
        KLAG2("Klage klageinstans", YtelseSakYtelsetype.ANNET),
        KOMP("Kompensasjon for ekstrautgifter", YtelseSakYtelsetype.ANNET),
        LREF("Refusjon av legeutgifter", YtelseSakYtelsetype.ANNET),
        MELD("Meldeplikt attføring", YtelseSakYtelsetype.ANNET),
        MITR("MOB-Midlertidig transporttilbud", YtelseSakYtelsetype.ANNET),
        NVURD("Næringsfaglig vurdering", YtelseSakYtelsetype.ANNET),
        REHAB("Rehabiliteringspenger", YtelseSakYtelsetype.ANNET),
        RSTO("MOB-Reisestønad", YtelseSakYtelsetype.ANNET),
        SANK_A("Sanksjon arbeidsgiver", YtelseSakYtelsetype.ANNET),
        SANK_B("Sanksjon behandler", YtelseSakYtelsetype.ANNET),
        SANK_S("Sanksjon sykmeldt", YtelseSakYtelsetype.ANNET),
        SEMA("Semesteravgift", YtelseSakYtelsetype.ANNET),
        SKOP("Skolepenger", YtelseSakYtelsetype.ANNET),
        SREI("MOB-Sjømenn", YtelseSakYtelsetype.ANNET),
        TFOR("Tvungen forvaltning", YtelseSakYtelsetype.ANNET),
        TILBBET("Tilbakebetaling", YtelseSakYtelsetype.ANNET),
        TILO("Tilsyn øvrige familiemedlemmer", YtelseSakYtelsetype.ANNET),
        TILTAK("Tiltaksplass", YtelseSakYtelsetype.ANNET),
        TILU("Tilsyn barn under 10 år", YtelseSakYtelsetype.ANNET),
        UFOREYT("Uføreytelser", YtelseSakYtelsetype.ANNET),
        UNDM("Bøker og undervisningsmatr.", YtelseSakYtelsetype.ANNET),
        UTESTENG("Utestengning", YtelseSakYtelsetype.ANNET),
        VENT("Ventestønad", YtelseSakYtelsetype.ANNET);

        companion object {
            fun fromNavn(n: String): YtelseVedtakVedtakstype {
                val faktiskNavn = n.substringBeforeLast('/').trim()
                return YtelseVedtakVedtakstype.values().firstOrNull { it.navn == faktiskNavn }
                    ?: throw IllegalArgumentException("Ukjent YtelseVedtakVedtakstype $n (trodde det var $faktiskNavn")
            }
        }
    }

    enum class YtelseVedtakStatus(val navn: String) {
        AVSLU("Avsluttet"),
        GODKJ("Godkjent"),
        INNST("Innstilt"),
        IVERK("Iverksatt"),
        MOTAT("Mottatt"),
        OPPRE("Opprettet"),
        REGIS("Registrert");

        companion object {
            fun fromNavn(n: String): YtelseVedtakStatus =
                YtelseVedtakStatus.values().firstOrNull { it.navn == n }
                    ?: throw IllegalArgumentException("Ukjent YtelseVedtakStatus $n")
        }
    }
}
