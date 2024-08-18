package org.openoa.base.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class JimuJsonUtil {
    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    public static <T> String toJsonString(T value) {

        StringWriter sw = new StringWriter();

        try {
            JsonGenerator gen = (new JsonFactory()).createGenerator(sw);
            mapper.writeValue(gen, value);
            gen.close();
        } catch (IOException var5) {
            log.error(var5.getMessage(), "value=[" + value + "]");
        }

        return sw.toString();
    }

    public static <T> T readAsEntity(String jsonString, Class<T> cls) throws Exception {
        T t = mapper.readValue(jsonString, cls);
        return t;
    }

    public static <T> List<T> readAsList(String json, Class<T> cls) throws Exception {
        List<T> o = (List<T>) readAsYourType(json,List.class,cls);
        return o;
    }

    public static Map<String,Long>readAsMapSL(String json)throws Exception{
        return readAsMap(json, String.class, Long.class);
    }
    public static Map<String,Integer>readAsMapSI(String json)throws Exception{
        return readAsMap(json, String.class, Integer.class);
    }
    public static Map<String,String>readAsMapSS(String json) throws Exception {
        return readAsMap(json, String.class, String.class);
    }
    public static <Tvalue> Map<String, Tvalue> readAsMapS(String json, Class<String> valueClass) throws Exception {
        return (Map<String, Tvalue>) readAsMap(json, String.class, valueClass);
    }

    public static <TKey, Tvalue> Map<TKey, Tvalue> readAsMap(String json, Class<TKey> keyClass, Class<Tvalue> valueClass) throws Exception {
        HashMap<TKey, Tvalue> map = (HashMap<TKey, Tvalue>) readAsYourType(json, HashMap.class, keyClass, valueClass);
        return map;
    }

    private static Object readAsYourType(String json, Class<?> rawType, Class<?>... classes) throws Exception {
        JavaType javaType = mapper.getTypeFactory().constructParametricType(rawType, classes);

        Object o = mapper.readValue(json, javaType);
        return o;
    }

    public static <T> T readAsType(String json, TypeReference<T> typeReference) throws JsonProcessingException {
        return mapper.readValue(json, typeReference);
    }
}
