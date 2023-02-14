package no.nav.tiltakspenger.arena.felles

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.testing.testApplication
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.xmlunit.matchers.CompareMatcher.isIdenticalTo
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicReference

// from https://github.com/manhhavu/ktor-jackson-xml
internal class JacksonXmlConverterTest {
    private val sampleXml = """<?xml version="1.0"?>
        <book id="bk101">
          <author>Gambardella, Matthew ÆØÅ</author>
          <title>XML Developer's Guide</title>
          <genre>Computer</genre>
          <price currency="EUR">44.95</price>
          <publish_date>2000-10-01</publish_date>
          <description>An in-depth look at creating applications with XML.</description>
       </book>
    """.trimIndent()

    private val book = Book(
        id = "bk101",
        author = "Gambardella, Matthew ÆØÅ",
        title = "XML Developer's Guide",
        genre = "Computer",
        price = Price(
            currency = "EUR",
            amount = BigDecimal("44.95"),
        ),
        publishDate = "2000-10-01",
        description = "An in-depth look at creating applications with XML.",
    )

    @Test
    fun deserialize() = testApplication {
        val holder = AtomicReference<Book>()
        install(ContentNegotiation) {
            register(ContentType.Application.Xml, JacksonXmlConverter())
        }
        routing {
            post("/") {
                val book = call.receive<Book>()
                holder.set(book)
                call.respond("OK")
            }
        }
        val response = client.post("/") {
            header(HttpHeaders.ContentType, ContentType.Application.Xml)
            setBody(sampleXml)
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(book, holder.get())
    }

    @Test
    fun serialize() = testApplication {
        install(ContentNegotiation) {
            register(ContentType.Application.Xml, JacksonXmlConverter())
        }
        routing {
            get("/") {
                call.respond(book)
            }
        }
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertThat(response.bodyAsText(), isIdenticalTo(sampleXml).ignoreWhitespace())
    }
}

@JacksonXmlRootElement(localName = "book")
data class Book(
    @JacksonXmlProperty(localName = "id", isAttribute = true)
    val id: String,
    @JsonProperty("author")
    val author: String,
    @JsonProperty("title")
    val title: String,
    @JsonProperty("genre")
    val genre: String,
    @JsonProperty("price")
    val price: Price,
    @JsonProperty("publish_date")
    val publishDate: String,
    @JsonProperty("description")
    val description: String,
)

data class Price(
    @JacksonXmlProperty(localName = "currency", isAttribute = true)
    val currency: String,
    @JsonProperty(XML_TEXT_ELEMENT_NAME)
    @JacksonXmlText
    val amount: BigDecimal,
)
