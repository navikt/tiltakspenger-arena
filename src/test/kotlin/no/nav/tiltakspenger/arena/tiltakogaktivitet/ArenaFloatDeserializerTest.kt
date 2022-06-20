package no.nav.tiltakspenger.arena.tiltakogaktivitet

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ArenaFloatDeserializerTest {
    data class ClassForTest(@JsonDeserialize(using = ArenaFloatDeserializer::class) val tall: Float)

    @Test
    fun deserialize() {
        // given
        val json = """{"tall": "1,5"}"""

        // when
        val deserialized: ClassForTest = ObjectMapper().registerKotlinModule().readValue(json, ClassForTest::class.java)

        // then
        assertEquals(1.5F, deserialized.tall)
    }
}
