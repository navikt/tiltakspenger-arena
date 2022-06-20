package no.nav.tiltakspenger.arena.tiltakogaktivitet

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer

object ArenaFloatDeserializer : JsonDeserializer<Float>() {
    override fun deserialize(parser: JsonParser, context: DeserializationContext): Float {
        var floatString = parser.text
        floatString = floatString.replace(",", ".")
        return java.lang.Float.valueOf(floatString)
    }
}
