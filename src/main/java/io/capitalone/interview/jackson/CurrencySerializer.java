package io.capitalone.interview.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Created by aseem80 on 4/5/17.
 */
public class CurrencySerializer extends JsonSerializer<BigDecimal> {

    @Override
    public void serialize(BigDecimal value, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException,
            JsonProcessingException {
        // put your desired money style here
        jsonGenerator.writeString("$" + value.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
    }

}
