package no.nav.tiltakspenger.arena.tiltakogaktivitet

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaAktiviteterDTO.Tiltaksaktivitet.Tiltaksnavn

class ArenaTiltaksnavnDeserializer : JsonDeserializer<Tiltaksnavn>() {
    override fun deserialize(parser: JsonParser, context: DeserializationContext) = Tiltaksnavn.fromTekst(parser.text)
}
