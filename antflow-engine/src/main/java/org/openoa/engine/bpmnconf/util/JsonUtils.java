package org.openoa.engine.bpmnconf.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;

/**
 * @Author tylerzhou
 */
public class JsonUtils {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);
    private static final ObjectMapper mapper;

    static {
        mapper=new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }
    public static <T> String transfer2JsonString(T value) {

        StringWriter sw = new StringWriter();

        try {
            JsonGenerator gen = (new JsonFactory()).createGenerator(sw);
            mapper.writeValue(gen, value);
            gen.close();
        } catch (IOException var5) {
            logger.error(var5.getMessage(), "value=[" + value + "]");
        }

        return sw.toString();
    }
}
