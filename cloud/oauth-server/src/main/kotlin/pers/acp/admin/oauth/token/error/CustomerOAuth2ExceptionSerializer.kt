package pers.acp.admin.oauth.token.error

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import io.github.zhangbinhub.acp.boot.enums.ResponseCode
import java.io.IOException

class CustomerOAuth2ExceptionSerializer : StdSerializer<CustomerOAuth2Exception>(CustomerOAuth2Exception::class.java) {
    @Throws(IOException::class)
    override fun serialize(exception: CustomerOAuth2Exception, jsonGenerator: JsonGenerator, provider: SerializerProvider?) {
        jsonGenerator.writeStartObject()
        jsonGenerator.writeNumberField("code", ResponseCode.AuthError.value)
        jsonGenerator.writeStringField("error", ResponseCode.AuthError.description)
        jsonGenerator.writeStringField("errorDescription", exception.cause?.message ?: exception.message
        ?: ResponseCode.AuthError.description)
        if (exception.additionalInformation != null) {
            for ((key, add) in exception.additionalInformation.entries) {
                jsonGenerator.writeStringField(key, add)
            }
        }
        jsonGenerator.writeEndObject()
    }
}