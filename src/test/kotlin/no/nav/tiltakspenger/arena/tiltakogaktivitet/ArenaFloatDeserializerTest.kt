package no.nav.tiltakspenger.arena.tiltakogaktivitet

import io.ktor.serialization.kotlinx.xml.DefaultXml
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class ArenaFloatDeserializerTest {
    @Serializable
    data class ClassForTest(@Serializable(with = ArenaFloatDeserializer::class) val tall: Float)

    @Test
    @Disabled
    fun deserialize() {
        // given
        val xml = """<tall>1,5</tall>"""

        // when
        val deserialized = DefaultXml.decodeFromString<ClassForTest>(xml)

        // then
        assertEquals(1.5F, deserialized.tall)
    }
}
