package no.nav.tiltakspenger.arena.tiltakogaktivitet

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import java.time.LocalDate

data class ArenaAktiviteterDTO(
    val response: Response
) {
    // Denne klassen er sjekket opp mot
    // https://confluence.adeo.no/display/ARENA/Arena+-+Tjeneste+Webservice+-+TiltakOgAktivitet_v1#ArenaTjenesteWebserviceTiltakOgAktivitet_v1-HentTiltakOgAktiviteterForBrukerResponse
    // De variablene som er merket som mandatory der er satt til ikke å være nullable her
    data class Response(
        var tiltaksaktivitetListe: List<Tiltaksaktivitet> = emptyList(),
        var gruppeaktivitetListe: List<Gruppeaktivitet> = emptyList(),
        var utdanningsaktivitetListe: List<Utdanningsaktivitet> = emptyList(),
    )

    data class Tiltaksaktivitet(
        var tiltaksnavn: String,
        var aktivitetId: String,
        var tiltakLokaltNavn: String?,
        var arrangoer: String?,
        var bedriftsnummer: String?,
        var deltakelsePeriode: DeltakelsesPeriode?,
        @JsonDeserialize(using = ArenaFloatDeserializer::class)
        var deltakelseProsent: Float?,
        var deltakerStatus: String,
        var statusSistEndret: LocalDate?,
        var begrunnelseInnsoeking: String,
        @JsonDeserialize(using = ArenaFloatDeserializer::class)
        var antallDagerPerUke: Float?,
    ) {

        data class DeltakelsesPeriode(
            val fom: LocalDate?,
            val tom: LocalDate?,
        )
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
