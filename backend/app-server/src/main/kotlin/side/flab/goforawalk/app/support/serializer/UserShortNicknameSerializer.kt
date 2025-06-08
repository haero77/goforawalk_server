package side.flab.goforawalk.app.support.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

class UserShortNicknameSerializer : JsonSerializer<String>() {
    override fun serialize(
        value: String,
        gen: JsonGenerator,
        serializers: SerializerProvider,
    ) {
        gen.writeString(if (value.length > 8) value.substring(0, 8) else value)
    }
}
