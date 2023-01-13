package no.nav.tiltakspenger.arena.tiltakogaktivitet

import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaAktiviteterDTO.Tiltaksaktivitet.DeltakerStatusType
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaAktiviteterDTO.Tiltaksaktivitet.Tiltaksnavn
import no.nav.tiltakspenger.libs.arena.tiltak.ArenaTiltaksaktivitetResponsDTO

fun mapArenaTiltak(aktiviteter: List<ArenaAktiviteterDTO.Tiltaksaktivitet>): ArenaTiltaksaktivitetResponsDTO {
    return ArenaTiltaksaktivitetResponsDTO(
        tiltaksaktiviteter = aktiviteter.map { mapTiltaksaktivitet(it) },
        feil = null
    )
}


fun mapTiltaksaktivitet(tiltaksaktivitet: ArenaAktiviteterDTO.Tiltaksaktivitet):
        ArenaTiltaksaktivitetResponsDTO.TiltaksaktivitetDTO {
    return ArenaTiltaksaktivitetResponsDTO.TiltaksaktivitetDTO(
        tiltakType = mapTiltakType(tiltaksaktivitet.tiltaksnavn),
        aktivitetId = tiltaksaktivitet.aktivitetId,
        tiltakLokaltNavn = tiltaksaktivitet.tiltakLokaltNavn,
        arrangoer = tiltaksaktivitet.arrangoer,
        bedriftsnummer = tiltaksaktivitet.bedriftsnummer,
        deltakelsePeriode = mapDeltakelsePeriode(tiltaksaktivitet.deltakelsePeriode),
        deltakelseProsent = tiltaksaktivitet.deltakelseProsent,
        deltakerStatusType = mapDeltakerStatus(tiltaksaktivitet.deltakerStatus),
        statusSistEndret = tiltaksaktivitet.statusSistEndret,
        begrunnelseInnsoeking = tiltaksaktivitet.begrunnelseInnsoeking,
        antallDagerPerUke = tiltaksaktivitet.antallDagerPerUke,
    )
}

@Suppress("CyclomaticComplexMethod")
fun mapDeltakerStatus(deltakerStatus: ArenaAktiviteterDTO.Tiltaksaktivitet.DeltakerStatus):
        ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType =
    when (deltakerStatus.status) {
        DeltakerStatusType.AKTUELL -> ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType.AKTUELL
        DeltakerStatusType.AVSLAG -> ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType.AVSLAG
        DeltakerStatusType.DELAVB -> ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType.DELAVB
        DeltakerStatusType.FULLF -> ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType.FULLF
        DeltakerStatusType.GJENN -> ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType.GJENN
        DeltakerStatusType.GJENN_AVB -> ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType.GJENN_AVB
        DeltakerStatusType.GJENN_AVL -> ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType.GJENN_AVL
        DeltakerStatusType.IKKAKTUELL -> ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType.IKKAKTUELL
        DeltakerStatusType.IKKEM -> ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType.IKKEM
        DeltakerStatusType.INFOMOETE -> ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType.INFOMOETE
        DeltakerStatusType.JATAKK -> ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType.JATAKK
        DeltakerStatusType.NEITAKK -> ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType.NEITAKK
        DeltakerStatusType.TILBUD -> ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType.TILBUD
        DeltakerStatusType.VENTELISTE -> ArenaTiltaksaktivitetResponsDTO.DeltakerStatusType.VENTELISTE
    }


@Suppress("CyclomaticComplexMethod", "LongMethod")
fun mapTiltakType(tiltaksnavn: Tiltaksnavn):
        ArenaTiltaksaktivitetResponsDTO.TiltakType =
    when (tiltaksnavn) {
        Tiltaksnavn.AMBF1 -> ArenaTiltaksaktivitetResponsDTO.TiltakType.AMBF1
        Tiltaksnavn.KURS -> ArenaTiltaksaktivitetResponsDTO.TiltakType.KURS
        Tiltaksnavn.ANNUTDANN -> ArenaTiltaksaktivitetResponsDTO.TiltakType.ANNUTDANN
        Tiltaksnavn.ABOPPF -> ArenaTiltaksaktivitetResponsDTO.TiltakType.ABOPPF
        Tiltaksnavn.ABUOPPF -> ArenaTiltaksaktivitetResponsDTO.TiltakType.ABUOPPF
        Tiltaksnavn.ABIST -> ArenaTiltaksaktivitetResponsDTO.TiltakType.ABIST
        Tiltaksnavn.ABTBOPPF -> ArenaTiltaksaktivitetResponsDTO.TiltakType.ABTBOPPF
        Tiltaksnavn.LONNTILAAP -> ArenaTiltaksaktivitetResponsDTO.TiltakType.LONNTILAAP
        Tiltaksnavn.ARBFORB -> ArenaTiltaksaktivitetResponsDTO.TiltakType.ARBFORB
        Tiltaksnavn.AMO -> ArenaTiltaksaktivitetResponsDTO.TiltakType.AMO
        Tiltaksnavn.AMOE -> ArenaTiltaksaktivitetResponsDTO.TiltakType.AMOE
        Tiltaksnavn.AMOB -> ArenaTiltaksaktivitetResponsDTO.TiltakType.AMOB
        Tiltaksnavn.AMOY -> ArenaTiltaksaktivitetResponsDTO.TiltakType.AMOY
        Tiltaksnavn.PRAKSORD -> ArenaTiltaksaktivitetResponsDTO.TiltakType.PRAKSORD
        Tiltaksnavn.PRAKSKJERM -> ArenaTiltaksaktivitetResponsDTO.TiltakType.PRAKSKJERM
        Tiltaksnavn.ARBRRHBAG -> ArenaTiltaksaktivitetResponsDTO.TiltakType.ARBRRHBAG
        Tiltaksnavn.ARBRRHBSM -> ArenaTiltaksaktivitetResponsDTO.TiltakType.ARBRRHBSM
        Tiltaksnavn.ARBRRHDAG -> ArenaTiltaksaktivitetResponsDTO.TiltakType.ARBRRHDAG
        Tiltaksnavn.ARBRDAGSM -> ArenaTiltaksaktivitetResponsDTO.TiltakType.ARBRDAGSM
        Tiltaksnavn.ARBRRDOGN -> ArenaTiltaksaktivitetResponsDTO.TiltakType.ARBRRDOGN
        Tiltaksnavn.ARBDOGNSM -> ArenaTiltaksaktivitetResponsDTO.TiltakType.ARBDOGNSM
        Tiltaksnavn.ASV -> ArenaTiltaksaktivitetResponsDTO.TiltakType.ASV
        Tiltaksnavn.ARBTREN -> ArenaTiltaksaktivitetResponsDTO.TiltakType.ARBTREN
        Tiltaksnavn.ATG -> ArenaTiltaksaktivitetResponsDTO.TiltakType.ATG
        Tiltaksnavn.AVKLARAG -> ArenaTiltaksaktivitetResponsDTO.TiltakType.AVKLARAG
        Tiltaksnavn.AVKLARUS -> ArenaTiltaksaktivitetResponsDTO.TiltakType.AVKLARUS
        Tiltaksnavn.AVKLARSP -> ArenaTiltaksaktivitetResponsDTO.TiltakType.AVKLARSP
        Tiltaksnavn.AVKLARKV -> ArenaTiltaksaktivitetResponsDTO.TiltakType.AVKLARKV
        Tiltaksnavn.AVKLARSV -> ArenaTiltaksaktivitetResponsDTO.TiltakType.AVKLARSV
        Tiltaksnavn.BIA -> ArenaTiltaksaktivitetResponsDTO.TiltakType.BIA
        Tiltaksnavn.BIO -> ArenaTiltaksaktivitetResponsDTO.TiltakType.BIO
        Tiltaksnavn.BREVKURS -> ArenaTiltaksaktivitetResponsDTO.TiltakType.BREVKURS
        Tiltaksnavn.DIGIOPPARB -> ArenaTiltaksaktivitetResponsDTO.TiltakType.DIGIOPPARB
        Tiltaksnavn.DIVTILT -> ArenaTiltaksaktivitetResponsDTO.TiltakType.DIVTILT
        Tiltaksnavn.ETAB -> ArenaTiltaksaktivitetResponsDTO.TiltakType.ETAB
        Tiltaksnavn.EKSPEBIST -> ArenaTiltaksaktivitetResponsDTO.TiltakType.EKSPEBIST
        Tiltaksnavn.ENKELAMO -> ArenaTiltaksaktivitetResponsDTO.TiltakType.ENKELAMO
        Tiltaksnavn.ENKFAGYRKE -> ArenaTiltaksaktivitetResponsDTO.TiltakType.ENKFAGYRKE
        Tiltaksnavn.FLEKSJOBB -> ArenaTiltaksaktivitetResponsDTO.TiltakType.FLEKSJOBB
        Tiltaksnavn.TILRTILSK -> ArenaTiltaksaktivitetResponsDTO.TiltakType.TILRTILSK
        Tiltaksnavn.KAT -> ArenaTiltaksaktivitetResponsDTO.TiltakType.KAT
        Tiltaksnavn.VALS -> ArenaTiltaksaktivitetResponsDTO.TiltakType.VALS
        Tiltaksnavn.FORSAMOENK -> ArenaTiltaksaktivitetResponsDTO.TiltakType.FORSAMOENK
        Tiltaksnavn.FORSAMOGRU -> ArenaTiltaksaktivitetResponsDTO.TiltakType.FORSAMOGRU
        Tiltaksnavn.FORSFAGENK -> ArenaTiltaksaktivitetResponsDTO.TiltakType.FORSFAGENK
        Tiltaksnavn.FORSFAGGRU -> ArenaTiltaksaktivitetResponsDTO.TiltakType.FORSFAGGRU
        Tiltaksnavn.FORSHOYUTD -> ArenaTiltaksaktivitetResponsDTO.TiltakType.FORSHOYUTD
        Tiltaksnavn.FUNKSJASS -> ArenaTiltaksaktivitetResponsDTO.TiltakType.FUNKSJASS
        Tiltaksnavn.GRUNNSKOLE -> ArenaTiltaksaktivitetResponsDTO.TiltakType.GRUNNSKOLE
        Tiltaksnavn.GRUPPEAMO -> ArenaTiltaksaktivitetResponsDTO.TiltakType.GRUPPEAMO
        Tiltaksnavn.GRUFAGYRKE -> ArenaTiltaksaktivitetResponsDTO.TiltakType.GRUFAGYRKE
        Tiltaksnavn.HOYEREUTD -> ArenaTiltaksaktivitetResponsDTO.TiltakType.HOYEREUTD
        Tiltaksnavn.HOYSKOLE -> ArenaTiltaksaktivitetResponsDTO.TiltakType.HOYSKOLE
        Tiltaksnavn.INDJOBSTOT -> ArenaTiltaksaktivitetResponsDTO.TiltakType.INDJOBSTOT
        Tiltaksnavn.IPSUNG -> ArenaTiltaksaktivitetResponsDTO.TiltakType.IPSUNG
        Tiltaksnavn.INDOPPFOLG -> ArenaTiltaksaktivitetResponsDTO.TiltakType.INDOPPFOLG
        Tiltaksnavn.INKLUTILS -> ArenaTiltaksaktivitetResponsDTO.TiltakType.INKLUTILS
        Tiltaksnavn.ITGRTILS -> ArenaTiltaksaktivitetResponsDTO.TiltakType.ITGRTILS
        Tiltaksnavn.JOBBKLUBB -> ArenaTiltaksaktivitetResponsDTO.TiltakType.JOBBKLUBB
        Tiltaksnavn.JOBBFOKUS -> ArenaTiltaksaktivitetResponsDTO.TiltakType.JOBBFOKUS
        Tiltaksnavn.JOBBK -> ArenaTiltaksaktivitetResponsDTO.TiltakType.JOBBK
        Tiltaksnavn.JOBBBONUS -> ArenaTiltaksaktivitetResponsDTO.TiltakType.JOBBBONUS
        Tiltaksnavn.JOBBSKAP -> ArenaTiltaksaktivitetResponsDTO.TiltakType.JOBBSKAP
        Tiltaksnavn.AMBF2 -> ArenaTiltaksaktivitetResponsDTO.TiltakType.AMBF2
        Tiltaksnavn.TESTING -> ArenaTiltaksaktivitetResponsDTO.TiltakType.TESTING
        Tiltaksnavn.STATLAERL -> ArenaTiltaksaktivitetResponsDTO.TiltakType.STATLAERL
        Tiltaksnavn.LONNTILS -> ArenaTiltaksaktivitetResponsDTO.TiltakType.LONNTILS
        Tiltaksnavn.REAKTUFOR -> ArenaTiltaksaktivitetResponsDTO.TiltakType.REAKTUFOR
        Tiltaksnavn.LONNTILL -> ArenaTiltaksaktivitetResponsDTO.TiltakType.LONNTILL
        Tiltaksnavn.MENTOR -> ArenaTiltaksaktivitetResponsDTO.TiltakType.MENTOR
        Tiltaksnavn.MIDLONTIL -> ArenaTiltaksaktivitetResponsDTO.TiltakType.MIDLONTIL
        Tiltaksnavn.NETTAMO -> ArenaTiltaksaktivitetResponsDTO.TiltakType.NETTAMO
        Tiltaksnavn.NETTKURS -> ArenaTiltaksaktivitetResponsDTO.TiltakType.NETTKURS
        Tiltaksnavn.INST_S -> ArenaTiltaksaktivitetResponsDTO.TiltakType.INST_S
        Tiltaksnavn.NYTEST -> ArenaTiltaksaktivitetResponsDTO.TiltakType.NYTEST
        Tiltaksnavn.INDOPPFAG -> ArenaTiltaksaktivitetResponsDTO.TiltakType.INDOPPFAG
        Tiltaksnavn.INDOPPFSP -> ArenaTiltaksaktivitetResponsDTO.TiltakType.INDOPPFSP
        Tiltaksnavn.PV -> ArenaTiltaksaktivitetResponsDTO.TiltakType.PV
        Tiltaksnavn.INDOPPRF -> ArenaTiltaksaktivitetResponsDTO.TiltakType.INDOPPRF
        Tiltaksnavn.REFINO -> ArenaTiltaksaktivitetResponsDTO.TiltakType.REFINO
        Tiltaksnavn.SPA -> ArenaTiltaksaktivitetResponsDTO.TiltakType.SPA
        Tiltaksnavn.SUPPEMP -> ArenaTiltaksaktivitetResponsDTO.TiltakType.SUPPEMP
        Tiltaksnavn.SYSSLANG -> ArenaTiltaksaktivitetResponsDTO.TiltakType.SYSSLANG
        Tiltaksnavn.YHEMMOFF -> ArenaTiltaksaktivitetResponsDTO.TiltakType.YHEMMOFF
        Tiltaksnavn.SYSSOFF -> ArenaTiltaksaktivitetResponsDTO.TiltakType.SYSSOFF
        Tiltaksnavn.LONNTIL -> ArenaTiltaksaktivitetResponsDTO.TiltakType.LONNTIL
        Tiltaksnavn.TIDSUBLONN -> ArenaTiltaksaktivitetResponsDTO.TiltakType.TIDSUBLONN
        Tiltaksnavn.AMBF3 -> ArenaTiltaksaktivitetResponsDTO.TiltakType.AMBF3
        Tiltaksnavn.TILRETTEL -> ArenaTiltaksaktivitetResponsDTO.TiltakType.TILRETTEL
        Tiltaksnavn.TILPERBED -> ArenaTiltaksaktivitetResponsDTO.TiltakType.TILPERBED
        Tiltaksnavn.TILSJOBB -> ArenaTiltaksaktivitetResponsDTO.TiltakType.TILSJOBB
        Tiltaksnavn.UFØREPENLØ -> ArenaTiltaksaktivitetResponsDTO.TiltakType.UFØREPENLØ
        Tiltaksnavn.UTDYRK -> ArenaTiltaksaktivitetResponsDTO.TiltakType.UTDYRK
        Tiltaksnavn.UTDPERMVIK -> ArenaTiltaksaktivitetResponsDTO.TiltakType.UTDPERMVIK
        Tiltaksnavn.VIKARBLED -> ArenaTiltaksaktivitetResponsDTO.TiltakType.VIKARBLED
        Tiltaksnavn.UTBHLETTPS -> ArenaTiltaksaktivitetResponsDTO.TiltakType.UTBHLETTPS
        Tiltaksnavn.UTBHPSLD -> ArenaTiltaksaktivitetResponsDTO.TiltakType.UTBHPSLD
        Tiltaksnavn.UTBHSAMLI -> ArenaTiltaksaktivitetResponsDTO.TiltakType.UTBHSAMLI
        Tiltaksnavn.UTVAOONAV -> ArenaTiltaksaktivitetResponsDTO.TiltakType.UTVAOONAV
        Tiltaksnavn.UTVOPPFOPL -> ArenaTiltaksaktivitetResponsDTO.TiltakType.UTVOPPFOPL
        Tiltaksnavn.VARLONTIL -> ArenaTiltaksaktivitetResponsDTO.TiltakType.VARLONTIL
        Tiltaksnavn.VATIAROR -> ArenaTiltaksaktivitetResponsDTO.TiltakType.VATIAROR
        Tiltaksnavn.VASV -> ArenaTiltaksaktivitetResponsDTO.TiltakType.VASV
        Tiltaksnavn.VV -> ArenaTiltaksaktivitetResponsDTO.TiltakType.VV
        Tiltaksnavn.VIDRSKOLE -> ArenaTiltaksaktivitetResponsDTO.TiltakType.VIDRSKOLE
        Tiltaksnavn.OPPLT2AAR -> ArenaTiltaksaktivitetResponsDTO.TiltakType.OPPLT2AAR
    }

fun mapDeltakelsePeriode(deltakelsePeriode: ArenaAktiviteterDTO.Tiltaksaktivitet.DeltakelsesPeriode?):
        ArenaTiltaksaktivitetResponsDTO.DeltakelsesPeriodeDTO? =
    deltakelsePeriode?.let { ArenaTiltaksaktivitetResponsDTO.DeltakelsesPeriodeDTO(fom = it.fom, tom = it.tom) }
