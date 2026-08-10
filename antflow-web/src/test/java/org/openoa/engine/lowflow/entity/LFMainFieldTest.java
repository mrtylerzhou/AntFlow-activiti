package org.openoa.engine.lowflow.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.MockBaseTest;
import org.openoa.base.constant.enums.LFControlTypeEnum;
import org.openoa.base.constant.enums.LFFieldTypeEnum;
import org.openoa.base.entity.BpmnConfLfFormdataField;
import org.openoa.base.exception.AFBizException;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class LFMainFieldTest extends MockBaseTest {

    private BpmnConfLfFormdataField createFieldConfig(String fieldId, String fieldName, Integer fieldType) {
        BpmnConfLfFormdataField config = new BpmnConfLfFormdataField();
        config.setFieldId(fieldId);
        config.setFieldName(fieldName);
        config.setFieldType(fieldType);
        return config;
    }

    @Nested
    @DisplayName("parseFromMap")
    class ParseFromMapTest {

        @Test
        @DisplayName("should throw AFBizException when fieldMap is empty")
        void shouldThrowWhenFieldMapIsEmpty() {
            Map<String, Object> fieldMap = Collections.emptyMap();
            Map<String, BpmnConfLfFormdataField> fieldConfigMap = new HashMap<>();
            fieldConfigMap.put("f1", createFieldConfig("f1", "name", LFFieldTypeEnum.STRING.getType()));

            assertThatThrownBy(() -> LFMainField.parseFromMap(fieldMap, fieldConfigMap, 1L, "formCode"))
                    .isInstanceOf(AFBizException.class)
                    .hasMessage("form data has no value");
        }

        @Test
        @DisplayName("should throw AFBizException when fieldMap is null")
        void shouldThrowWhenFieldMapIsNull() {
            Map<String, BpmnConfLfFormdataField> fieldConfigMap = new HashMap<>();
            fieldConfigMap.put("f1", createFieldConfig("f1", "name", LFFieldTypeEnum.STRING.getType()));

            assertThatThrownBy(() -> LFMainField.parseFromMap(null, fieldConfigMap, 1L, "formCode"))
                    .isInstanceOf(AFBizException.class)
                    .hasMessage("form data has no value");
        }

        @Test
        @DisplayName("should throw AFBizException when fieldConfigMap is empty")
        void shouldThrowWhenFieldConfigMapIsEmpty() {
            Map<String, Object> fieldMap = new HashMap<>();
            fieldMap.put("f1", "value");

            assertThatThrownBy(() -> LFMainField.parseFromMap(fieldMap, Collections.emptyMap(), 1L, "formCode"))
                    .isInstanceOf(AFBizException.class)
                    .hasMessage("field configs are empty,please check your logic");
        }

        @Test
        @DisplayName("should throw AFBizException when fieldConfigMap is null")
        void shouldThrowWhenFieldConfigMapIsNull() {
            Map<String, Object> fieldMap = new HashMap<>();
            fieldMap.put("f1", "value");

            assertThatThrownBy(() -> LFMainField.parseFromMap(fieldMap, null, 1L, "formCode"))
                    .isInstanceOf(AFBizException.class)
                    .hasMessage("field configs are empty,please check your logic");
        }

        @Test
        @DisplayName("should skip entries where fieldConfig is null for the fieldId")
        void shouldSkipEntriesWithNullFieldConfig() {
            Map<String, Object> fieldMap = new HashMap<>();
            fieldMap.put("unknownField", "value");
            fieldMap.put("knownField", "hello");
            Map<String, BpmnConfLfFormdataField> fieldConfigMap = new HashMap<>();
            fieldConfigMap.put("knownField", createFieldConfig("knownField", "Known", LFFieldTypeEnum.STRING.getType()));

            List<LFMainField> result = LFMainField.parseFromMap(fieldMap, fieldConfigMap, 1L, "formCode");

            assertEquals(1, result.size());
            assertEquals("knownField", result.get(0).getFieldId());
        }

        @Test
        @DisplayName("should build LFMainField for each entry and set formCode")
        void shouldBuildFieldsAndSetFormCode() {
            Map<String, Object> fieldMap = new HashMap<>();
            fieldMap.put("f1", "hello");
            fieldMap.put("f2", "world");
            Map<String, BpmnConfLfFormdataField> fieldConfigMap = new HashMap<>();
            fieldConfigMap.put("f1", createFieldConfig("f1", "Field1", LFFieldTypeEnum.STRING.getType()));
            fieldConfigMap.put("f2", createFieldConfig("f2", "Field2", LFFieldTypeEnum.STRING.getType()));

            List<LFMainField> result = LFMainField.parseFromMap(fieldMap, fieldConfigMap, 100L, "myForm");

            assertEquals(2, result.size());
            for (LFMainField field : result) {
                assertEquals("myForm", field.getFormCode());
            }
        }

        @Test
        @DisplayName("should set mainId on all returned fields")
        void shouldSetMainIdOnAllFields() {
            Map<String, Object> fieldMap = new HashMap<>();
            fieldMap.put("f1", "val1");
            fieldMap.put("f2", "val2");
            Map<String, BpmnConfLfFormdataField> fieldConfigMap = new HashMap<>();
            fieldConfigMap.put("f1", createFieldConfig("f1", "Field1", LFFieldTypeEnum.STRING.getType()));
            fieldConfigMap.put("f2", createFieldConfig("f2", "Field2", LFFieldTypeEnum.STRING.getType()));

            List<LFMainField> result = LFMainField.parseFromMap(fieldMap, fieldConfigMap, 999L, "formCode");

            for (LFMainField field : result) {
                assertEquals(Long.valueOf(999L), field.getMainId());
            }
        }
    }

    @Nested
    @DisplayName("buildMainField")
    class BuildMainFieldTest {

        @Nested
        @DisplayName("STRING type")
        class StringTypeTest {

            @Test
            @DisplayName("should set fieldValue to string representation")
            void shouldSetFieldValueToString() {
                BpmnConfLfFormdataField config = createFieldConfig("f1", "Name", LFFieldTypeEnum.STRING.getType());

                LFMainField result = LFMainField.buildMainField("hello", 1L, 0, config);

                assertEquals("hello", result.getFieldValue());
            }

            @Test
            @DisplayName("should set fieldValue to null when fieldValue is null")
            void shouldSetFieldValueToNullWhenNull() {
                BpmnConfLfFormdataField config = createFieldConfig("f1", "Name", LFFieldTypeEnum.STRING.getType());

                LFMainField result = LFMainField.buildMainField(null, 1L, 0, config);

                assertNull(result.getFieldValue());
            }
        }

        @Nested
        @DisplayName("NUMBER type")
        class NumberTypeTest {

            @Test
            @DisplayName("should set fieldValueNumber to parsed Double")
            void shouldSetFieldValueNumberToParsedDouble() {
                BpmnConfLfFormdataField config = createFieldConfig("f1", "Amount", LFFieldTypeEnum.NUMBER.getType());

                LFMainField result = LFMainField.buildMainField("42.5", 1L, 0, config);

                assertEquals(Double.valueOf(42.5), result.getFieldValueNumber());
            }

            @Test
            @DisplayName("should set fieldValueNumber to null when fieldValueStr is empty")
            void shouldSetFieldValueNumberToNullWhenEmpty() {
                BpmnConfLfFormdataField config = createFieldConfig("f1", "Amount", LFFieldTypeEnum.NUMBER.getType());

                LFMainField result = LFMainField.buildMainField(null, 1L, 0, config);

                assertNull(result.getFieldValueNumber());
            }

            @Test
            @DisplayName("should set fieldValue instead of fieldValueNumber when fieldName is select")
            void shouldSetFieldValueWhenSelectFieldName() {
                BpmnConfLfFormdataField config = createFieldConfig("f1", LFControlTypeEnum.SELECT.getName(), LFFieldTypeEnum.NUMBER.getType());

                LFMainField result = LFMainField.buildMainField("3", 1L, 0, config);

                assertEquals("3", result.getFieldValue());
                assertNull(result.getFieldValueNumber());
            }
        }

        @Nested
        @DisplayName("DATE type")
        class DateTypeTest {

            @Test
            @DisplayName("should set fieldValueDt to parsed Date")
            void shouldSetFieldValueDtToParsedDate() {
                BpmnConfLfFormdataField config = createFieldConfig("f1", "BirthDate", LFFieldTypeEnum.DATE.getType());

                LFMainField result = LFMainField.buildMainField("2024-01-15 00:00:00", 1L, 0, config);

                assertNotNull(result.getFieldValueDt());
            }

            @Test
            @DisplayName("should set fieldValueDt to null when fieldValueStr is empty")
            void shouldSetFieldValueDtToNullWhenEmpty() {
                BpmnConfLfFormdataField config = createFieldConfig("f1", "BirthDate", LFFieldTypeEnum.DATE.getType());

                LFMainField result = LFMainField.buildMainField(null, 1L, 0, config);

                assertNull(result.getFieldValueDt());
            }
        }

        @Nested
        @DisplayName("DATE_TIME type")
        class DateTimeTypeTest {

            @Test
            @DisplayName("should set fieldValueDt to parsed Date")
            void shouldSetFieldValueDtToParsedDate() {
                BpmnConfLfFormdataField config = createFieldConfig("f1", "CreatedAt", LFFieldTypeEnum.DATE_TIME.getType());

                LFMainField result = LFMainField.buildMainField("2024-01-15 10:30:00", 1L, 0, config);

                assertNotNull(result.getFieldValueDt());
            }
        }

        @Nested
        @DisplayName("TEXT type")
        class TextTypeTest {

            @Test
            @DisplayName("should set fieldValueText")
            void shouldSetFieldValueText() {
                BpmnConfLfFormdataField config = createFieldConfig("f1", "Description", LFFieldTypeEnum.TEXT.getType());

                LFMainField result = LFMainField.buildMainField("long text content", 1L, 0, config);

                assertEquals("long text content", result.getFieldValueText());
            }
        }

        @Nested
        @DisplayName("BOOLEAN type")
        class BooleanTypeTest {

            @Test
            @DisplayName("should set fieldValue to string representation")
            void shouldSetFieldValueToString() {
                BpmnConfLfFormdataField config = createFieldConfig("f1", "Active", LFFieldTypeEnum.BOOLEAN.getType());

                LFMainField result = LFMainField.buildMainField("true", 1L, 0, config);

                assertEquals("true", result.getFieldValue());
            }
        }

        @Nested
        @DisplayName("fieldType validation")
        class FieldTypeValidationTest {

            @Test
            @DisplayName("should throw AFBizException when fieldType is null")
            void shouldThrowWhenFieldTypeIsNull() {
                BpmnConfLfFormdataField config = createFieldConfig("f1", "Name", null);

                assertThatThrownBy(() -> LFMainField.buildMainField("value", 1L, 0, config))
                        .isInstanceOf(AFBizException.class)
                        .hasMessage(String.format("field type can not be empty,%s", config));
            }

            @Test
            @DisplayName("should throw AFBizException when fieldType is unrecognized")
            void shouldThrowWhenFieldTypeIsUnrecognized() {
                BpmnConfLfFormdataField config = createFieldConfig("f1", "Name", 999);

                assertThatThrownBy(() -> LFMainField.buildMainField("value", 1L, 0, config))
                        .isInstanceOf(AFBizException.class);
            }
        }

        @Nested
        @DisplayName("common field assignments")
        class CommonFieldAssignmentsTest {

            @Test
            @DisplayName("should set sort on returned field")
            void shouldSetSort() {
                BpmnConfLfFormdataField config = createFieldConfig("f1", "Name", LFFieldTypeEnum.STRING.getType());

                LFMainField result = LFMainField.buildMainField("val", 1L, 5, config);

                assertEquals(5, result.getSort());
            }

            @Test
            @DisplayName("should set mainId on returned field")
            void shouldSetMainId() {
                BpmnConfLfFormdataField config = createFieldConfig("f1", "Name", LFFieldTypeEnum.STRING.getType());

                LFMainField result = LFMainField.buildMainField("val", 42L, 0, config);

                assertEquals(Long.valueOf(42L), result.getMainId());
            }

            @Test
            @DisplayName("should set fieldId and fieldName from fieldConfig")
            void shouldSetFieldIdAndFieldName() {
                BpmnConfLfFormdataField config = createFieldConfig("myFieldId", "myFieldName", LFFieldTypeEnum.STRING.getType());

                LFMainField result = LFMainField.buildMainField("val", 1L, 0, config);

                assertEquals("myFieldId", result.getFieldId());
                assertEquals("myFieldName", result.getFieldName());
            }
        }
    }
}
