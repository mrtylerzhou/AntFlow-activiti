package org.openoa.engine.lowflow.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.openoa.MockBaseTest;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.entity.BpmnConfLfFormdata;
import org.openoa.base.entity.BpmnConfLfFormdataField;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.FormConfigWrapper;
import org.openoa.engine.bpmnconf.service.impl.BpmnConfLfFormdataFieldServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmnConfLfFormdataServiceImpl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class LFFormDataPreProcessorTest extends MockBaseTest {

    @Spy
    @InjectMocks
    private LFFormDataPreProcessor preProcessor;

    @Mock
    private BpmnConfLfFormdataServiceImpl lfFormdataService;

    @Mock
    private BpmnConfLfFormdataFieldServiceImpl lfFormdataFieldService;

    private LFFormDataPreProcessor realInstance;

    @BeforeEach
    void setUp() {
        realInstance = new LFFormDataPreProcessor();
    }

    private FormConfigWrapper.LFWidget createWidget(String type, String category, String name, String label) {
        FormConfigWrapper.LFWidget widget = new FormConfigWrapper.LFWidget();
        widget.setType(type);
        widget.setCategory(category);
        FormConfigWrapper.LFWidget.LFOption option = new FormConfigWrapper.LFWidget.LFOption();
        option.setName(name);
        option.setLabel(label);
        widget.setOptions(option);
        return widget;
    }

    private FormConfigWrapper.LFWidget createContainerWidget(String type) {
        FormConfigWrapper.LFWidget widget = new FormConfigWrapper.LFWidget();
        widget.setType(type);
        widget.setCategory(StringConstants.LOWFLOW_FORM_CONTAINER_TYPE);
        return widget;
    }

    private BpmnConfVo createConfVo(Long id, String formCode, Integer isLowCodeFlow, String lfFormData) {
        BpmnConfVo confVo = new BpmnConfVo();
        confVo.setId(id);
        confVo.setFormCode(formCode);
        confVo.setIsLowCodeFlow(isLowCodeFlow);
        confVo.setLfFormData(lfFormData);
        return confVo;
    }

    private void invokeParseWidgetListRecursively(Object target, List<FormConfigWrapper.LFWidget> widgetList,
                                                  Long confId, Long formDataId, List<BpmnConfLfFormdataField> result) throws Exception {
        Method method = LFFormDataPreProcessor.class.getDeclaredMethod("parseWidgetListRecursively",
                List.class, Long.class, Long.class, List.class);
        method.setAccessible(true);
        method.invoke(target, widgetList, confId, formDataId, result);
    }

    private int invokeGetFieldTypeByTypeString(Object target, String typeString) throws Exception {
        Method method = LFFormDataPreProcessor.class.getDeclaredMethod("getFieldTypeByTypeString", String.class);
        method.setAccessible(true);
        return (int) method.invoke(target, typeString);
    }

    @Nested
    @DisplayName("getFieldTypeByTypeString")
    class GetFieldTypeByTypeStringTest {

        @Test
        @DisplayName("should return 2 for number")
        void shouldReturn2ForNumber() throws Exception {
            assertEquals(2, invokeGetFieldTypeByTypeString(realInstance, "number"));
        }

        @Test
        @DisplayName("should return 4 for date")
        void shouldReturn4ForDate() throws Exception {
            assertEquals(4, invokeGetFieldTypeByTypeString(realInstance, "date"));
        }

        @Test
        @DisplayName("should return 6 for switch")
        void shouldReturn6ForSwitch() throws Exception {
            assertEquals(6, invokeGetFieldTypeByTypeString(realInstance, "switch"));
        }

        @Test
        @DisplayName("should return 1 for select")
        void shouldReturn1ForSelect() throws Exception {
            assertEquals(1, invokeGetFieldTypeByTypeString(realInstance, "select"));
        }

        @Test
        @DisplayName("should return 1 for input")
        void shouldReturn1ForInput() throws Exception {
            assertEquals(1, invokeGetFieldTypeByTypeString(realInstance, "input"));
        }

        @Test
        @DisplayName("should return 1 for checkbox")
        void shouldReturn1ForCheckbox() throws Exception {
            assertEquals(1, invokeGetFieldTypeByTypeString(realInstance, "checkbox"));
        }

        @Test
        @DisplayName("should return 1 for time")
        void shouldReturn1ForTime() throws Exception {
            assertEquals(1, invokeGetFieldTypeByTypeString(realInstance, "time"));
        }

        @Test
        @DisplayName("should return 1 for unknown type string")
        void shouldReturn1ForUnknownType() throws Exception {
            assertEquals(1, invokeGetFieldTypeByTypeString(realInstance, "unknown"));
        }
    }

    @Nested
    @DisplayName("parseWidgetListRecursively")
    class ParseWidgetListRecursivelyTest {

        @Test
        @DisplayName("should parse simple non-container widget into field")
        void shouldParseSimpleNonContainerWidget() throws Exception {
            FormConfigWrapper.LFWidget widget = createWidget("input", "form-item", "field1", "Label1");
            List<FormConfigWrapper.LFWidget> widgetList = Arrays.asList(widget);
            List<BpmnConfLfFormdataField> result = new ArrayList<>();

            invokeParseWidgetListRecursively(realInstance, widgetList, 100L, 200L, result);

            assertEquals(1, result.size());
            assertEquals("field1", result.get(0).getFieldId());
            assertEquals("Label1", result.get(0).getFieldName());
        }

        @Test
        @DisplayName("should skip container widgets and recurse into CARD container")
        void shouldRecurseIntoCardContainer() throws Exception {
            FormConfigWrapper.LFWidget innerWidget = createWidget("input", "form-item", "cardField", "CardLabel");
            FormConfigWrapper.LFWidget cardWidget = createContainerWidget("card");
            cardWidget.setWidgetList(Arrays.asList(innerWidget));
            List<FormConfigWrapper.LFWidget> widgetList = Arrays.asList(cardWidget);
            List<BpmnConfLfFormdataField> result = new ArrayList<>();

            invokeParseWidgetListRecursively(realInstance, widgetList, 10L, 20L, result);

            assertEquals(1, result.size());
            assertEquals("cardField", result.get(0).getFieldId());
        }

        @Test
        @DisplayName("should recurse into TAB container via tabs list")
        void shouldRecurseIntoTabContainer() throws Exception {
            FormConfigWrapper.LFWidget innerWidget = createWidget("number", "form-item", "tabField", "TabLabel");
            FormConfigWrapper.LFWidget tabPane = new FormConfigWrapper.LFWidget();
            tabPane.setWidgetList(Arrays.asList(innerWidget));
            FormConfigWrapper.LFWidget tabWidget = createContainerWidget("tab");
            tabWidget.setTabs(Arrays.asList(tabPane));
            List<FormConfigWrapper.LFWidget> widgetList = Arrays.asList(tabWidget);
            List<BpmnConfLfFormdataField> result = new ArrayList<>();

            invokeParseWidgetListRecursively(realInstance, widgetList, 10L, 20L, result);

            assertEquals(1, result.size());
            assertEquals("tabField", result.get(0).getFieldId());
            assertEquals(2, result.get(0).getFieldType().intValue());
        }

        @Test
        @DisplayName("should recurse into TABLE container via rows/cols")
        void shouldRecurseIntoTableContainer() throws Exception {
            FormConfigWrapper.LFWidget innerWidget = createWidget("date", "form-item", "tableField", "TableLabel");
            FormConfigWrapper.LFWidget col = new FormConfigWrapper.LFWidget();
            col.setWidgetList(Arrays.asList(innerWidget));
            FormConfigWrapper.TableRow row = new FormConfigWrapper.TableRow();
            row.setCols(Arrays.asList(col));
            FormConfigWrapper.LFWidget tableWidget = createContainerWidget("table");
            tableWidget.setRows(Arrays.asList(row));
            List<FormConfigWrapper.LFWidget> widgetList = Arrays.asList(tableWidget);
            List<BpmnConfLfFormdataField> result = new ArrayList<>();

            invokeParseWidgetListRecursively(realInstance, widgetList, 10L, 20L, result);

            assertEquals(1, result.size());
            assertEquals("tableField", result.get(0).getFieldId());
            assertEquals(4, result.get(0).getFieldType().intValue());
        }

        @Test
        @DisplayName("should recurse into GRID container via cols when rows is empty")
        void shouldRecurseIntoGridContainer() throws Exception {
            FormConfigWrapper.LFWidget innerWidget = createWidget("switch", "form-item", "gridField", "GridLabel");
            FormConfigWrapper.LFWidget col = new FormConfigWrapper.LFWidget();
            col.setWidgetList(Arrays.asList(innerWidget));
            FormConfigWrapper.LFWidget gridWidget = createContainerWidget("grid");
            gridWidget.setRows(null);
            gridWidget.setCols(Arrays.asList(col));
            List<FormConfigWrapper.LFWidget> widgetList = Arrays.asList(gridWidget);
            List<BpmnConfLfFormdataField> result = new ArrayList<>();

            invokeParseWidgetListRecursively(realInstance, widgetList, 10L, 20L, result);

            assertEquals(1, result.size());
            assertEquals("gridField", result.get(0).getFieldId());
            assertEquals(6, result.get(0).getFieldType().intValue());
        }

        @Test
        @DisplayName("should skip unknown container type")
        void shouldSkipUnknownContainerType() throws Exception {
            FormConfigWrapper.LFWidget unknownWidget = createContainerWidget("accordion");
            List<FormConfigWrapper.LFWidget> widgetList = Arrays.asList(unknownWidget);
            List<BpmnConfLfFormdataField> result = new ArrayList<>();

            invokeParseWidgetListRecursively(realInstance, widgetList, 10L, 20L, result);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("should skip widget with null sub-widgetList")
        void shouldSkipWidgetWithNullSubWidgetList() throws Exception {
            FormConfigWrapper.LFWidget col = new FormConfigWrapper.LFWidget();
            col.setWidgetList(null);
            FormConfigWrapper.LFWidget gridWidget = createContainerWidget("grid");
            gridWidget.setRows(null);
            gridWidget.setCols(Arrays.asList(col));
            List<FormConfigWrapper.LFWidget> widgetList = Arrays.asList(gridWidget);
            List<BpmnConfLfFormdataField> result = new ArrayList<>();

            invokeParseWidgetListRecursively(realInstance, widgetList, 10L, 20L, result);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("should set correct confId, formDataId, fieldType, fieldId, fieldName on parsed fields")
        void shouldSetCorrectFieldProperties() throws Exception {
            FormConfigWrapper.LFWidget widget = createWidget("number", "form-item", "amount", "Amount");
            List<FormConfigWrapper.LFWidget> widgetList = Arrays.asList(widget);
            List<BpmnConfLfFormdataField> result = new ArrayList<>();

            invokeParseWidgetListRecursively(realInstance, widgetList, 555L, 666L, result);

            assertEquals(1, result.size());
            BpmnConfLfFormdataField field = result.get(0);
            assertEquals(Long.valueOf(555L), field.getBpmnConfId());
            assertEquals(Long.valueOf(666L), field.getFormDataId());
            assertEquals(2, field.getFieldType().intValue());
            assertEquals("amount", field.getFieldId());
            assertEquals("Amount", field.getFieldName());
        }

        @Test
        @DisplayName("should handle nested containers CARD inside TAB")
        void shouldHandleNestedContainers() throws Exception {
            FormConfigWrapper.LFWidget innerWidget = createWidget("input", "form-item", "nestedField", "NestedLabel");
            FormConfigWrapper.LFWidget cardWidget = createContainerWidget("card");
            cardWidget.setWidgetList(Arrays.asList(innerWidget));
            FormConfigWrapper.LFWidget tabPane = new FormConfigWrapper.LFWidget();
            tabPane.setWidgetList(Arrays.asList(cardWidget));
            FormConfigWrapper.LFWidget tabWidget = createContainerWidget("tab");
            tabWidget.setTabs(Arrays.asList(tabPane));
            List<FormConfigWrapper.LFWidget> widgetList = Arrays.asList(tabWidget);
            List<BpmnConfLfFormdataField> result = new ArrayList<>();

            invokeParseWidgetListRecursively(realInstance, widgetList, 10L, 20L, result);

            assertEquals(1, result.size());
            assertEquals("nestedField", result.get(0).getFieldId());
        }
    }

    @Nested
    @DisplayName("preWriteProcess")
    class PreWriteProcessTest {

        @Test
        @DisplayName("should return early when confVo is null")
        void shouldReturnEarlyWhenConfVoIsNull() {
            preProcessor.preWriteProcess(null);

            verify(lfFormdataService, never()).save(any());
            verify(lfFormdataFieldService, never()).saveBatch(anyList());
        }

        @Test
        @DisplayName("should return early when isLowCodeFlow is null")
        void shouldReturnEarlyWhenIsLowCodeFlowIsNull() {
            BpmnConfVo confVo = createConfVo(1L, "form1", null, "{}");

            preProcessor.preWriteProcess(confVo);

            verify(lfFormdataService, never()).save(any());
        }

        @Test
        @DisplayName("should return early when isLowCodeFlow is 0")
        void shouldReturnEarlyWhenIsLowCodeFlowIsZero() {
            BpmnConfVo confVo = createConfVo(1L, "form1", 0, "{}");

            preProcessor.preWriteProcess(confVo);

            verify(lfFormdataService, never()).save(any());
        }

        @Test
        @DisplayName("should throw AFBizException when widgetList is empty")
        void shouldThrowWhenWidgetListIsEmpty() {
            String lfFormData = "{\"widgetList\":[]}";
            BpmnConfVo confVo = createConfVo(1L, "form1", 1, lfFormData);
            when(lfFormdataService.save(any(BpmnConfLfFormdata.class))).thenAnswer(invocation -> {
                BpmnConfLfFormdata entity = invocation.getArgument(0);
                entity.setId(100L);
                return true;
            });

            try (MockedStatic<SecurityUtils> securityUtilsMock = mockStatic(SecurityUtils.class)) {
                securityUtilsMock.when(SecurityUtils::getLogInEmpName).thenReturn("testUser");

                AFBizException ex = assertThrows(AFBizException.class, () -> preProcessor.preWriteProcess(confVo));
                assertTrue(ex.getMessage().contains("lowcode form has no widget"));
            }
        }

        @Test
        @DisplayName("should throw AFBizException when parsed formdataFields is empty")
        void shouldThrowWhenParsedFormdataFieldsIsEmpty() {
            String lfFormData = String.format("{\"widgetList\":[{\"type\":\"accordion\",\"category\":\"%s\"}]}",
                    StringConstants.LOWFLOW_FORM_CONTAINER_TYPE);
            BpmnConfVo confVo = createConfVo(1L, "form1", 1, lfFormData);
            when(lfFormdataService.save(any(BpmnConfLfFormdata.class))).thenAnswer(invocation -> {
                BpmnConfLfFormdata entity = invocation.getArgument(0);
                entity.setId(100L);
                return true;
            });

            try (MockedStatic<SecurityUtils> securityUtilsMock = mockStatic(SecurityUtils.class)) {
                securityUtilsMock.when(SecurityUtils::getLogInEmpName).thenReturn("testUser");

                AFBizException ex = assertThrows(AFBizException.class, () -> preProcessor.preWriteProcess(confVo));
                assertTrue(ex.getMessage().contains("lowcode form fields can not be empty"));
            }
        }

        @Test
        @DisplayName("should save lfFormdata and formdataFields for valid low-code flow")
        void shouldSaveLfFormdataAndFormdataFields() {
            String lfFormData = "{\"widgetList\":[{\"type\":\"input\",\"category\":\"form-item\",\"options\":{\"name\":\"field1\",\"label\":\"Field1\"}}]}";
            BpmnConfVo confVo = createConfVo(1L, "form1", 1, lfFormData);
            when(lfFormdataService.save(any(BpmnConfLfFormdata.class))).thenAnswer(invocation -> {
                BpmnConfLfFormdata entity = invocation.getArgument(0);
                entity.setId(100L);
                return true;
            });

            try (MockedStatic<SecurityUtils> securityUtilsMock = mockStatic(SecurityUtils.class)) {
                securityUtilsMock.when(SecurityUtils::getLogInEmpName).thenReturn("testUser");

                preProcessor.preWriteProcess(confVo);

                ArgumentCaptor<BpmnConfLfFormdata> formdataCaptor = ArgumentCaptor.forClass(BpmnConfLfFormdata.class);
                verify(lfFormdataService).save(formdataCaptor.capture());
                BpmnConfLfFormdata capturedFormdata = formdataCaptor.getValue();
                assertEquals(Long.valueOf(1L), capturedFormdata.getBpmnConfId());
                assertEquals(lfFormData, capturedFormdata.getFormdata());
                assertEquals("testUser", capturedFormdata.getCreateUser());

                ArgumentCaptor<List<BpmnConfLfFormdataField>> fieldsCaptor = ArgumentCaptor.forClass(List.class);
                verify(lfFormdataFieldService).saveBatch(fieldsCaptor.capture());
                List<BpmnConfLfFormdataField> capturedFields = fieldsCaptor.getValue();
                assertEquals(1, capturedFields.size());
                assertEquals("field1", capturedFields.get(0).getFieldId());
                assertEquals("Field1", capturedFields.get(0).getFieldName());
            }
        }

        @Test
        @DisplayName("should set lfFormDataId on confVo after save")
        void shouldSetLfFormDataIdOnConfVoAfterSave() {
            String lfFormData = "{\"widgetList\":[{\"type\":\"input\",\"category\":\"form-item\",\"options\":{\"name\":\"f1\",\"label\":\"F1\"}}]}";
            BpmnConfVo confVo = createConfVo(1L, "form1", 1, lfFormData);
            when(lfFormdataService.save(any(BpmnConfLfFormdata.class))).thenAnswer(invocation -> {
                BpmnConfLfFormdata entity = invocation.getArgument(0);
                entity.setId(999L);
                return true;
            });

            try (MockedStatic<SecurityUtils> securityUtilsMock = mockStatic(SecurityUtils.class)) {
                securityUtilsMock.when(SecurityUtils::getLogInEmpName).thenReturn("testUser");

                preProcessor.preWriteProcess(confVo);

                assertEquals(Long.valueOf(999L), confVo.getLfFormDataId());
            }
        }
    }

    @Nested
    @DisplayName("preReadProcess")
    class PreReadProcessTest {

        @Test
        @DisplayName("should return early when confVo is null")
        void shouldReturnEarlyWhenConfVoIsNull() {
            preProcessor.preReadProcess(null);

            verify(lfFormdataService, never()).list(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("should return early when isLowCodeFlow is 0")
        void shouldReturnEarlyWhenIsLowCodeFlowIsZero() {
            BpmnConfVo confVo = createConfVo(1L, "form1", 0, null);

            preProcessor.preReadProcess(confVo);

            verify(lfFormdataService, never()).list(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("should throw AFBizException when no formdata found")
        void shouldThrowWhenNoFormdataFound() {
            BpmnConfVo confVo = createConfVo(1L, "form1", 1, null);
            when(lfFormdataService.list(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

            AFBizException ex = assertThrows(AFBizException.class, () -> preProcessor.preReadProcess(confVo));
            assertTrue(ex.getMessage().contains("can not get lowcode flow formdata by confId"));
        }

        @Test
        @DisplayName("should set lfFormData and lfFormDataId on confVo")
        void shouldSetLfFormDataAndLfFormDataIdOnConfVo() {
            BpmnConfVo confVo = createConfVo(1L, "form1", 1, null);
            BpmnConfLfFormdata lfFormdata = new BpmnConfLfFormdata();
            lfFormdata.setId(500L);
            lfFormdata.setFormdata("{\"widgetList\":[]}");
            when(lfFormdataService.list(any(LambdaQueryWrapper.class))).thenReturn(Arrays.asList(lfFormdata));

            preProcessor.preReadProcess(confVo);

            assertEquals("{\"widgetList\":[]}", confVo.getLfFormData());
            assertEquals(Long.valueOf(500L), confVo.getLfFormDataId());
        }
    }
}
