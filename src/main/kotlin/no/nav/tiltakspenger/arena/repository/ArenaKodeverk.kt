package no.nav.tiltakspenger.arena.repository

enum class ArenaSakStatus(val navn: String) {
    AKTIV("Aktiv"),
    AVSLU("Lukket"),
    INAKT("Inaktiv"),
}

enum class ArenaYtelse(val navn: String) {
    AA("Arbeidsavklaringspenger"),
    DAGP("Dagpenger"),
    INDIV("Individstønad"),
    ANNET("Alt annet"),
}

enum class ArenaVedtakType(val navn: String) {
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
    T("Tidsbegrenset bortfall"), // Gjelder ikke tiltakspenger
}

enum class ArenaRettighet(val navn: String, val ytelse: ArenaYtelse) {
    AAP("Arbeidsavklaringspenger", ArenaYtelse.AA),
    DAGO("Ordinære dagpenger", ArenaYtelse.DAGP),
    PERM("Dagpenger under permitteringer", ArenaYtelse.DAGP),
    FISK("Dagp. v/perm fra fiskeindustri", ArenaYtelse.DAGP),
    LONN("Lønnsgarantimidler - dagpenger", ArenaYtelse.DAGP),
    BASI("Tiltakspenger (basisytelse før 2014)", ArenaYtelse.INDIV),
    AATFOR("Tvungen forvaltning", ArenaYtelse.ANNET),
    AAUNGUFOR("Ung ufør", ArenaYtelse.ANNET),
    AA115("§11-5 nedsatt arbeidsevne", ArenaYtelse.ANNET),
    AA116("§11-6 behov for bistand", ArenaYtelse.ANNET),
    ABOUT("Boutgifter", ArenaYtelse.ANNET),
    ADAGR("Daglige reiseutgifter", ArenaYtelse.ANNET),
    AFLYT("Flytting", ArenaYtelse.ANNET),
    AHJMR("Hjemreise", ArenaYtelse.ANNET),
    ANKE("Anke", ArenaYtelse.ANNET),
    ARBT("Arbeidstreningplass", ArenaYtelse.ANNET),
    ATIF("Tilsyn - familiemedlemmer", ArenaYtelse.ANNET),
    ATIO("Tilsyn - barn over 10 år", ArenaYtelse.ANNET),
    ATIU("Tilsyn - barn under 10 år", ArenaYtelse.ANNET),
    ATTF("§11-6, nødvendig og hensiktsmessig tiltak", ArenaYtelse.ANNET),
    ATTK("§11-5, sykdom, skade eller lyte", ArenaYtelse.ANNET),
    ATTP("Attføringspenger", ArenaYtelse.ANNET),
    AUNDM("Bøker og undervisningsmatriell", ArenaYtelse.ANNET),
    BEHOV("Behovsvurdering", ArenaYtelse.ANNET),
    BIST14A("Bistandsbehov §14a", ArenaYtelse.ANNET),
    BORT("Borteboertillegg", ArenaYtelse.ANNET),
    BOUT("Boutgifter", ArenaYtelse.ANNET),
    BREI("MOB-Besøksreise", ArenaYtelse.ANNET),
    BTIF("Barnetilsyn - familiemedlemmer", ArenaYtelse.ANNET),
    BTIL("Barnetillegg", ArenaYtelse.ANNET),
    BTIO("Barnetilsyn - barn over 10 år", ArenaYtelse.ANNET),
    BTIU("Barnetilsyn - barn under 10 år", ArenaYtelse.ANNET),
    DAGR("Daglige reiseutgifter", ArenaYtelse.ANNET),
    DEKS("Eksport - dagpenger", ArenaYtelse.ANNET),
    DIMP("Import (E303 inn)", ArenaYtelse.ANNET),
    EKSG("Eksamensgebyr", ArenaYtelse.ANNET),
    FADD("Fadder", ArenaYtelse.ANNET),
    FLYT("Flytting", ArenaYtelse.ANNET),
    FREI("MOB-Fremreise", ArenaYtelse.ANNET),
    FRI_MK_AAP("Fritak fra å sende meldekort AAP", ArenaYtelse.ANNET),
    FRI_MK_IND("Fritak fra å sende meldekort individstønad", ArenaYtelse.ANNET),
    FSTO("MOB-Flyttestønad", ArenaYtelse.ANNET),
    HJMR("Hjemreise", ArenaYtelse.ANNET),
    HREI("MOB-Hjemreise", ArenaYtelse.ANNET),
    HUSH("Husholdsutgifter", ArenaYtelse.ANNET),
    IDAG("Reisetillegg", ArenaYtelse.ANNET),
    IEKS("Eksamensgebyr", ArenaYtelse.ANNET),
    IFLY("MOB-Flyttehjelp", ArenaYtelse.ANNET),
    INDIVFADD("Individstønad fadder", ArenaYtelse.ANNET),
    IREI("Hjemreise", ArenaYtelse.ANNET),
    ISEM("Semesteravgift", ArenaYtelse.ANNET),
    ISKO("Skolepenger", ArenaYtelse.ANNET),
    IUND("Bøker og undervisningsmatr.", ArenaYtelse.ANNET),
    KLAG1("Klage underinstans", ArenaYtelse.ANNET),
    KLAG2("Klage klageinstans", ArenaYtelse.ANNET),
    KOMP("Kompensasjon for ekstrautgifter", ArenaYtelse.ANNET),
    LREF("Refusjon av legeutgifter", ArenaYtelse.ANNET),
    MELD("Meldeplikt attføring", ArenaYtelse.ANNET),
    MITR("MOB-Midlertidig transporttilbud", ArenaYtelse.ANNET),
    NVURD("Næringsfaglig vurdering", ArenaYtelse.ANNET),
    REHAB("Rehabiliteringspenger", ArenaYtelse.ANNET),
    RSTO("MOB-Reisestønad", ArenaYtelse.ANNET),
    SANK_A("Sanksjon arbeidsgiver", ArenaYtelse.ANNET),
    SANK_B("Sanksjon behandler", ArenaYtelse.ANNET),
    SANK_S("Sanksjon sykmeldt", ArenaYtelse.ANNET),
    SEMA("Semesteravgift", ArenaYtelse.ANNET),
    SKOP("Skolepenger", ArenaYtelse.ANNET),
    SREI("MOB-Sjømenn", ArenaYtelse.ANNET),
    TFOR("Tvungen forvaltning", ArenaYtelse.ANNET),
    TILBBET("Tilbakebetaling", ArenaYtelse.ANNET),
    TILO("Tilsyn øvrige familiemedlemmer", ArenaYtelse.ANNET),
    TILTAK("Tiltaksplass", ArenaYtelse.ANNET),
    TILU("Tilsyn barn under 10 år", ArenaYtelse.ANNET),
    UFOREYT("Uføreytelser", ArenaYtelse.ANNET),
    UNDM("Bøker og undervisningsmatr.", ArenaYtelse.ANNET),
    UTESTENG("Utestengning", ArenaYtelse.ANNET),
    VENT("Ventestønad", ArenaYtelse.ANNET),
}

enum class ArenaVedtakStatus(val navn: String) {
    AVSLU("Avsluttet"),
    GODKJ("Godkjent"),
    INNST("Innstilt"),
    IVERK("Iverksatt"),
    MOTAT("Mottatt"),
    OPPRE("Opprettet"),
    REGIS("Registrert"),
}

enum class ArenaUtfall(val navn: String) {
    JA("Ja"),
    NEI("Nei"),
    AVBRUTT("Avbrutt"),
}

enum class ArenaAktivitetFase(val navn: String) {
    AU("Arbeidsutprøving"),
    EOS("EØS opphold"),
    FA("Ferdig avklart"),
    IKKE("Ikke spesif. aktivitetsfase"),
    SPE("Sykepengeerstatning"),
    UA("Under arbeidsavklaring"),
    UETO("Oppstartsfase, etablering"),
    UETU("Utviklingsfase, etablering"),
    UGJEN("Under gjennomføring av tiltak"),
    UVUP("Vurdering for uføre"),
}
