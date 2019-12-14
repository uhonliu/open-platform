package com.opencloud.common.exception;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 自定义oauth2异常提示
 *
 * @author liuyadu
 */
@Slf4j
public class OpenOAuth2ExceptionSerializer extends StdSerializer<OpenOAuth2Exception> {
    public OpenOAuth2ExceptionSerializer() {
        super(OpenOAuth2Exception.class);
    }

    @Override
    public void serialize(OpenOAuth2Exception ex, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("message", ex.getMessage());
        gen.writeStringField("data", "");
        gen.writeNumberField("timestamp", System.currentTimeMillis());
        if (ex.getAdditionalInformation() != null) {
            for (Map.Entry<String, String> entry : ex.getAdditionalInformation().entrySet()) {
                String key = entry.getKey();
                String add = entry.getValue();
                if ("code".equals(key)) {
                    gen.writeNumberField(key, new BigDecimal(add));
                } else {
                    gen.writeStringField(key, add);
                }
            }
        }
        gen.writeEndObject();
    }
}