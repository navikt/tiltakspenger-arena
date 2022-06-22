package no.nav.tiltakspenger.arena.tiltakogaktivitet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ArenaFloatDeserializer : KSerializer<Float> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ArenaFloat", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Float {
        var floatString = decoder.decodeString()
        floatString = floatString.replace(",", ".")
        return java.lang.Float.valueOf(floatString)
    }

    override fun serialize(encoder: Encoder, value: Float) {
        TODO("Not yet implemented")
    }
}
