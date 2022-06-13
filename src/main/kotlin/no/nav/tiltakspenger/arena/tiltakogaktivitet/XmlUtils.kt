package no.nav.tiltakspenger.arena.tiltakogaktivitet

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule

object XmlUtils {

    fun <T> fromXml(xml: String, clazz: Class<T>): T {
        val module = JacksonXmlModule()
        module.setDefaultUseWrapper(false)
        val xmlMapper: XmlMapper = XmlMapper(module)
        xmlMapper.registerModule(JavaTimeModule())
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        return xmlMapper.readValue(xml, clazz)
    }

    class ArenaFloatDeserializer : JsonDeserializer<Float>() {
        override fun deserialize(parser: JsonParser, context: DeserializationContext): Float {
            var floatString = parser.text
            floatString = floatString.replace(",", ".")
            return java.lang.Float.valueOf(floatString)
        }
    }
}
