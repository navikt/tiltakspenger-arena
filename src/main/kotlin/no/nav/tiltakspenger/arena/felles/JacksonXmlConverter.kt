package no.nav.tiltakspenger.arena.felles

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.http.ContentType
import io.ktor.http.content.OutgoingContent
import io.ktor.http.content.TextContent
import io.ktor.http.withCharset
import io.ktor.serialization.ContentConverter
import io.ktor.util.reflect.TypeInfo
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.charsets.Charset
import io.ktor.utils.io.jvm.javaio.toInputStream

// We need an explicit name for xml tag value
// due to this bug https://github.com/FasterXML/jackson-module-kotlin/issues/138
const val XML_TEXT_ELEMENT_NAME: String = "innerText"

/**
 * Inspired by https://github.com/manhhavu/ktor-jackson-xml
 *
 *    install(ContentNegotiation) {
 *       register(ContentType.Application.Xml, JacksonXmlConverter())
 *    }
 *
 *    to be able to modify the xmlMapper (eg. using specific modules and/or serializers and/or
 *    configuration options, you could use the following (as seen in the ktor-samples):
 *
 *    install(ContentNegotiation) {
 *        xml {
 *            configure(SerializationFeature.INDENT_OUTPUT, true)
 *            registerModule(JavaTimeModule())
 *        }
 *    }
 */
class JacksonXmlConverter(private val xmlMapper: XmlMapper = xmlMapper()) : ContentConverter {

    override suspend fun deserialize(charset: Charset, typeInfo: TypeInfo, content: ByteReadChannel): Any? {
        val reader = content.toInputStream().reader()
        return xmlMapper.readValue(reader, typeInfo.type.javaObjectType)
    }

    override suspend fun serialize(
        contentType: ContentType,
        charset: Charset,
        typeInfo: TypeInfo,
        value: Any?,
    ): OutgoingContent? {
        if (value == null) return null
        return TextContent(xmlMapper.writeValueAsString(value), contentType.withCharset(charset))
    }
}

private fun xmlMapper(xmlTextElementName: String = XML_TEXT_ELEMENT_NAME): XmlMapper {
    val module = JacksonXmlModule().apply {
        setXMLTextElementName(xmlTextElementName)
        setDefaultUseWrapper(false)
    }
    return XmlMapper(module).apply {
        registerModule(KotlinModule.Builder().build())
        registerModule(JavaTimeModule())
        setDefaultPrettyPrinter(
            DefaultPrettyPrinter().apply {
                indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
                indentObjectsWith(DefaultIndenter("  ", "\n"))
            },
        )
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }
}
