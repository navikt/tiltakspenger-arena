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
        val tiltaksnavn: String,
        val aktivitetId: String,
        val tiltakLokaltNavn: String?,
        val arrangoer: String?,
        val bedriftsnummer: String?,
        val deltakelsePeriode: DeltakelsesPeriode?,
        @JsonDeserialize(using = ArenaFloatDeserializer::class)
        val deltakelseProsent: Float?,
        val deltakerStatus: DeltakerStatus,
        val statusSistEndret: LocalDate?,
        val begrunnelseInnsoeking: String,
        @JsonDeserialize(using = ArenaFloatDeserializer::class)
        val antallDagerPerUke: Float?,
    ) {

        data class DeltakelsesPeriode(
            val fom: LocalDate?,
            val tom: LocalDate?,
        )

        data class DeltakerStatus(
            @JacksonXmlProperty(localName = "termnavn", isAttribute = true)
            val termnavn: String,
            @JsonProperty(XML_TEXT_ELEMENT_NAME)
            @JacksonXmlText
            val status: String
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
