package org.openoa.common.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 *
 */
@Component
public class JacksonMapper extends ObjectMapper {
    private static final long serialVersionUID = 7840849083907067915L;

    private static final String ASIA_SHANGHAI = "Asia/Shanghai";

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private static final String NORM_TIME_PATTERN = "HH:mm:ss";

    private static final String FULL_DATE_TIME_FORMAT ="yyyy-MM-dd HH:mm:ss";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public JacksonMapper() {
        super();
        this.setLocale(Locale.CHINA);
        this.setTimeZone(TimeZone.getTimeZone(ASIA_SHANGHAI));
        this.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
        this.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
        this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        this.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
//        this.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.setDateFormat(new SimpleDateFormat(FULL_DATE_TIME_FORMAT));
        SimpleModule simpleModule = new SimpleModule();
        this.setSerializerFactory(this.getSerializerFactory().withSerializerModifier(new BeanSerializerModifier() {
            @Override
            public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {

                for (BeanPropertyWriter writer : beanProperties) {

                    if (isArrayType(writer)) {
                        writer.assignNullSerializer(new JsonSerializer.None() {
                            @Override
                            public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                                gen.writeStartArray();
                                gen.writeEndArray();
                            }
                        });
                    } else if (isMapType(writer)) {
                        writer.assignNullSerializer(new JsonSerializer.None() {
                            @Override
                            public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                                gen.writeStartObject();
                                gen.writeEndObject();
                            }
                        });
                    } else if (isNumberType(writer)) {
                        writer.assignNullSerializer(new JsonSerializer.None() {
                            @Override
                            public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                                gen.writeNumber(0);
                            }
                        });
                    } else {
                        writer.assignNullSerializer(new JsonSerializer.None() {
                            @Override
                            public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                                gen.writeString("");
                            }
                        });
                    }
                }
                return super.changeProperties(config, beanDesc, beanProperties);
            }

            /**
             * 是否是数组
             *
             * @param writer
             * @return
             */
            private boolean isArrayType(BeanPropertyWriter writer) {
                Class<?> rawClass = writer.getType().getRawClass();
                return rawClass.isArray() || Collection.class.isAssignableFrom(rawClass);

            }

            /**
             * 是否是map
             *
             * @param writer
             * @return
             */
            private boolean isMapType(BeanPropertyWriter writer) {
                Class<?> rawClass = writer.getType().getRawClass();
                return Map.class.isAssignableFrom(rawClass);

            }

            /**
             * 是否是String
             */
            private boolean isStringType(BeanPropertyWriter writer) {
                Class<?> clazz = writer.getType().getRawClass();
                return CharSequence.class.isAssignableFrom(clazz) || Character.class.isAssignableFrom(clazz);
            }

            /**
             * 是否是数值类型
             */
            private boolean isNumberType(BeanPropertyWriter writer) {
                Class<?> clazz = writer.getType().getRawClass();
                return Number.class.isAssignableFrom(clazz);
            }

            /**
             * 是否是boolean
             */
            private boolean isBooleanType(BeanPropertyWriter writer) {
                Class<?> clazz = writer.getType().getRawClass();
                return clazz.equals(Boolean.class);
            }

        }));
        simpleModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(FULL_DATE_TIME_FORMAT)));
        simpleModule.addSerializer(LocalDate.class, new LocalDateSerializer(DATE_FORMATTER));
        simpleModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(FULL_DATE_TIME_FORMAT)));

        // ======================= 时间反序列化规则 ==============================
        // yyyy-MM-dd HH:mm:ss
        simpleModule.addDeserializer(LocalDateTime.class,
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(FULL_DATE_TIME_FORMAT)));
        // yyyy-MM-dd
        simpleModule.addDeserializer(LocalDate.class,
                new LocalDateDeserializer(DATE_FORMATTER));
        // HH:mm:ss
        simpleModule.addDeserializer(LocalTime.class,
                new LocalTimeDeserializer(DateTimeFormatter.ofPattern(NORM_TIME_PATTERN)));

        registerModule(simpleModule);
    }
}
