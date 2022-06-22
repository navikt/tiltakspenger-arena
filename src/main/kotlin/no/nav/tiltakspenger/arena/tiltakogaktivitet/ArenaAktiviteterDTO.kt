package no.nav.tiltakspenger.arena.tiltakogaktivitet

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class ArenaAktiviteterDTO(
    val response: Response
) {
    // Denne klassen er sjekket opp mot
    // https://confluence.adeo.no/display/ARENA/Arena+-+Tjeneste+Webservice+-+TiltakOgAktivitet_v1#ArenaTjenesteWebserviceTiltakOgAktivitet_v1-HentTiltakOgAktiviteterForBrukerResponse
    // De variablene som er merket som mandatory der er satt til ikke å være nullable her
    @Serializable
    data class Response(
        val tiltaksaktivitetListe: List<Tiltaksaktivitet> = emptyList(),
        val gruppeaktivitetListe: List<Gruppeaktivitet> = emptyList(),
        val utdanningsaktivitetListe: List<Utdanningsaktivitet> = emptyList(),
    )

    @Serializable
    data class Tiltaksaktivitet(
        val tiltaksnavn: String,
        val aktivitetId: String,
        val tiltakLokaltNavn: String?,
        val arrangoer: String?,
        val bedriftsnummer: String?,
        val deltakelsePeriode: DeltakelsesPeriode?,
        @Serializable(with = ArenaFloatDeserializer::class)
        val deltakelseProsent: Float?,
        val deltakerStatus: String,
        @Serializable(with = LocalDateSerializer::class)
        val statusSistEndret: LocalDate?,
        val begrunnelseInnsoeking: String,
        @Serializable(with = ArenaFloatDeserializer::class)
        val antallDagerPerUke: Float?,
    ) {

        @Serializable
        data class DeltakelsesPeriode(
            @Serializable(with = LocalDateSerializer::class)
            val fom: LocalDate?,
            @Serializable(with = LocalDateSerializer::class)
            val tom: LocalDate?,
        )
    }

    @Serializable
    data class Utdanningsaktivitet(
        val aktivitetstype: String,
        val aktivitetId: String,
        val beskrivelse: String?,
        val aktivitetPeriode: AktivitetPeriode?,
    ) {
        @Serializable
        data class AktivitetPeriode(
            @Serializable(with = LocalDateSerializer::class)
            val fom: LocalDate?,
            @Serializable(with = LocalDateSerializer::class)
            val tom: LocalDate?,
        )
    }

    @Serializable
    data class Gruppeaktivitet(
        val aktivitetstype: String,
        val aktivitetId: String,
        val beskrivelse: String?,
        val status: String?,
        val moeteplanListe: List<Moteplan>,
    ) {
        @Serializable
        data class Moteplan(
            @Serializable(with = LocalDateSerializer::class)
            val startDato: LocalDate,
            val startKlokkeslett: String?, // f.eks: 13:00:00
            @Serializable(with = LocalDateSerializer::class)
            val sluttDato: LocalDate,
            val sluttKlokkeslett: String?, // f.eks: 14:00:00
            val sted: String?,
        )
    }
}
