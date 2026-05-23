package org.openoa.engine.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openoa.MockBaseTest;
import org.openoa.base.constant.enums.WildcardCharacterEnum;
import org.openoa.base.entity.InformationTemplate;
import org.openoa.base.vo.InformationTemplateVo;
import org.openoa.engine.bpmnconf.mapper.InformationTemplateMapper;
import org.openoa.engine.bpmnconf.service.impl.InformationTemplateServiceImpl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InformationTemplateUtilsTest extends MockBaseTest {

    @InjectMocks
    private InformationTemplateUtils informationTemplateUtils;

    @Mock
    private InformationTemplateServiceImpl informationTemplateService;

    @Mock
    private InformationTemplateMapper informationTemplateMapper;

    @BeforeEach
    void setUp() {
        lenient().doReturn(informationTemplateMapper).when(informationTemplateService).getBaseMapper();
    }

    @Nested
    @DisplayName("translateInformationTemplate")
    class TranslateInformationTemplateTest {

        @Test
        @DisplayName("should replace all wildcard patterns with corresponding values from map")
        void shouldReplaceAllWildcardPatterns() {
            InformationTemplate template = InformationTemplate.builder()
                    .id(1L)
                    .systemTitle("{工作流名称}-{流程编号}")
                    .systemContent("{申请人}提交给{被审批人}")
                    .noteContent("{申请日期}(年月日) {申请时间}(年月日时分秒)")
                    .jumpUrl(1)
                    .build();
            when(informationTemplateMapper.selectById(1L)).thenReturn(template);

            Map<Integer, String> map = new HashMap<>();
            map.put(WildcardCharacterEnum.ONE_CHARACTER.getCode(), "请假流程");
            map.put(WildcardCharacterEnum.TWO_CHARACTER.getCode(), "PROC-001");
            map.put(WildcardCharacterEnum.THREE_CHARACTER.getCode(), "张三");
            map.put(WildcardCharacterEnum.FOUR_CHARACTER.getCode(), "李四");
            map.put(WildcardCharacterEnum.FIVE_CHARACTER.getCode(), "2024-01-15");
            map.put(WildcardCharacterEnum.SIX_CHARACTER.getCode(), "2024-01-15 10:30:00");

            InformationTemplateVo vo = InformationTemplateVo.builder()
                    .id(1L)
                    .wildcardCharacterMap(map)
                    .build();

            InformationTemplateVo result = informationTemplateUtils.translateInformationTemplate(vo);

            assertEquals("请假流程-PROC-001", result.getSystemTitle());
            assertEquals("张三提交给李四", result.getSystemContent());
            assertEquals("请假流程-PROC-001", result.getMailTitle());
            assertEquals("张三提交给李四", result.getMailContent());
            assertEquals("2024-01-15 2024-01-15 10:30:00", result.getNoteContent());
            assertEquals(Integer.valueOf(1), result.getJumpUrl());
        }

        @Test
        @DisplayName("should replace with empty string when map value is null")
        void shouldReplaceWithEmptyStringWhenMapValueIsNull() {
            InformationTemplate template = InformationTemplate.builder()
                    .id(1L)
                    .systemTitle("{工作流名称}-{流程编号}")
                    .systemContent("content")
                    .noteContent("note")
                    .jumpUrl(1)
                    .build();
            when(informationTemplateMapper.selectById(1L)).thenReturn(template);

            Map<Integer, String> map = new HashMap<>();
            map.put(WildcardCharacterEnum.ONE_CHARACTER.getCode(), "请假流程");
            map.put(WildcardCharacterEnum.TWO_CHARACTER.getCode(), null);

            InformationTemplateVo vo = InformationTemplateVo.builder()
                    .id(1L)
                    .wildcardCharacterMap(map)
                    .build();

            InformationTemplateVo result = informationTemplateUtils.translateInformationTemplate(vo);

            assertEquals("请假流程-", result.getSystemTitle());
        }

        @Test
        @DisplayName("should replace with empty string when map value is empty")
        void shouldReplaceWithEmptyStringWhenMapValueIsEmpty() {
            InformationTemplate template = InformationTemplate.builder()
                    .id(1L)
                    .systemTitle("{工作流名称}-{流程编号}")
                    .systemContent("content")
                    .noteContent("note")
                    .jumpUrl(1)
                    .build();
            when(informationTemplateMapper.selectById(1L)).thenReturn(template);

            Map<Integer, String> map = new HashMap<>();
            map.put(WildcardCharacterEnum.ONE_CHARACTER.getCode(), "请假流程");
            map.put(WildcardCharacterEnum.TWO_CHARACTER.getCode(), "");

            InformationTemplateVo vo = InformationTemplateVo.builder()
                    .id(1L)
                    .wildcardCharacterMap(map)
                    .build();

            InformationTemplateVo result = informationTemplateUtils.translateInformationTemplate(vo);

            assertEquals("请假流程-", result.getSystemTitle());
        }

        @Test
        @DisplayName("should return empty strings when template is not found")
        void shouldReturnEmptyStringsWhenTemplateNotFound() {
            when(informationTemplateMapper.selectById(999L)).thenReturn(null);

            Map<Integer, String> map = new HashMap<>();
            map.put(WildcardCharacterEnum.ONE_CHARACTER.getCode(), "value");

            InformationTemplateVo vo = InformationTemplateVo.builder()
                    .id(999L)
                    .wildcardCharacterMap(map)
                    .build();

            InformationTemplateVo result = informationTemplateUtils.translateInformationTemplate(vo);

            assertEquals("", result.getSystemTitle());
            assertEquals("", result.getSystemContent());
            assertEquals("", result.getMailTitle());
            assertEquals("", result.getMailContent());
            assertEquals("", result.getNoteContent());
        }

        @Test
        @DisplayName("should use new InformationTemplate defaults when selectById returns null")
        void shouldUseNewInformationTemplateDefaultsWhenSelectByIdReturnsNull() {
            when(informationTemplateMapper.selectById(999L)).thenReturn(null);

            Map<Integer, String> map = new HashMap<>();
            InformationTemplateVo vo = InformationTemplateVo.builder()
                    .id(999L)
                    .wildcardCharacterMap(map)
                    .build();

            InformationTemplateVo result = informationTemplateUtils.translateInformationTemplate(vo);

            assertNull(result.getJumpUrl());
            assertEquals("", result.getSystemTitle());
            assertEquals("", result.getSystemContent());
        }

        @Test
        @DisplayName("should pass jumpUrl through without translation")
        void shouldPassJumpUrlThroughWithoutTranslation() {
            InformationTemplate template = InformationTemplate.builder()
                    .id(1L)
                    .systemTitle("title")
                    .systemContent("content")
                    .noteContent("note")
                    .jumpUrl(3)
                    .build();
            when(informationTemplateMapper.selectById(1L)).thenReturn(template);

            Map<Integer, String> map = new HashMap<>();
            InformationTemplateVo vo = InformationTemplateVo.builder()
                    .id(1L)
                    .wildcardCharacterMap(map)
                    .build();

            InformationTemplateVo result = informationTemplateUtils.translateInformationTemplate(vo);

            assertEquals(Integer.valueOf(3), result.getJumpUrl());
        }

        @Test
        @DisplayName("should translate systemTitle, systemContent, noteContent independently")
        void shouldTranslateFieldsIndependently() {
            InformationTemplate template = InformationTemplate.builder()
                    .id(1L)
                    .systemTitle("{工作流名称}")
                    .systemContent("{流程编号}")
                    .noteContent("{申请人}")
                    .jumpUrl(1)
                    .build();
            when(informationTemplateMapper.selectById(1L)).thenReturn(template);

            Map<Integer, String> map = new HashMap<>();
            map.put(WildcardCharacterEnum.ONE_CHARACTER.getCode(), "流程A");
            map.put(WildcardCharacterEnum.TWO_CHARACTER.getCode(), "NUM-001");
            map.put(WildcardCharacterEnum.THREE_CHARACTER.getCode(), "王五");

            InformationTemplateVo vo = InformationTemplateVo.builder()
                    .id(1L)
                    .wildcardCharacterMap(map)
                    .build();

            InformationTemplateVo result = informationTemplateUtils.translateInformationTemplate(vo);

            assertEquals("流程A", result.getSystemTitle());
            assertEquals("NUM-001", result.getSystemContent());
            assertEquals("流程A", result.getMailTitle());
            assertEquals("NUM-001", result.getMailContent());
            assertEquals("王五", result.getNoteContent());
        }

        @Test
        @DisplayName("should handle template with no wildcards")
        void shouldHandleTemplateWithNoWildcards() {
            InformationTemplate template = InformationTemplate.builder()
                    .id(1L)
                    .systemTitle("固定标题")
                    .systemContent("固定内容")
                    .noteContent("固定通知")
                    .jumpUrl(2)
                    .build();
            when(informationTemplateMapper.selectById(1L)).thenReturn(template);

            Map<Integer, String> map = new HashMap<>();
            InformationTemplateVo vo = InformationTemplateVo.builder()
                    .id(1L)
                    .wildcardCharacterMap(map)
                    .build();

            InformationTemplateVo result = informationTemplateUtils.translateInformationTemplate(vo);

            assertEquals("固定标题", result.getSystemTitle());
            assertEquals("固定内容", result.getSystemContent());
            assertEquals("固定标题", result.getMailTitle());
            assertEquals("固定内容", result.getMailContent());
            assertEquals("固定通知", result.getNoteContent());
            assertEquals(Integer.valueOf(2), result.getJumpUrl());
        }
    }

    @Nested
    @DisplayName("translate")
    class TranslateTest {

        private Method translateMethod;

        @BeforeEach
        void setUpTranslateMethod() throws Exception {
            translateMethod = InformationTemplateUtils.class.getDeclaredMethod("translate", String.class, Map.class);
            translateMethod.setAccessible(true);
        }

        @Test
        @DisplayName("should return empty string when info is null")
        void shouldReturnEmptyStringWhenInfoIsNull() throws Exception {
            Map<Integer, String> map = new HashMap<>();

            String result = (String) translateMethod.invoke(informationTemplateUtils, null, map);

            assertEquals("", result);
        }

        @Test
        @DisplayName("should return empty string when info is empty string")
        void shouldReturnEmptyStringWhenInfoIsEmpty() throws Exception {
            Map<Integer, String> map = new HashMap<>();

            String result = (String) translateMethod.invoke(informationTemplateUtils, "", map);

            assertEquals("", result);
        }

        @Test
        @DisplayName("should replace single wildcard pattern")
        void shouldReplaceSingleWildcardPattern() throws Exception {
            Map<Integer, String> map = new HashMap<>();
            map.put(WildcardCharacterEnum.ONE_CHARACTER.getCode(), "请假流程");

            String result = (String) translateMethod.invoke(informationTemplateUtils, "通知：{工作流名称}已提交", map);

            assertEquals("通知：请假流程已提交", result);
        }

        @Test
        @DisplayName("should replace multiple wildcard patterns")
        void shouldReplaceMultipleWildcardPatterns() throws Exception {
            Map<Integer, String> map = new HashMap<>();
            map.put(WildcardCharacterEnum.ONE_CHARACTER.getCode(), "报销流程");
            map.put(WildcardCharacterEnum.THREE_CHARACTER.getCode(), "赵六");
            map.put(WildcardCharacterEnum.EIGHT_CHARACTER.getCode(), "钱七");

            String result = (String) translateMethod.invoke(informationTemplateUtils, "{工作流名称}-{申请人}等待{当前审批人}审批", map);

            assertEquals("报销流程-赵六等待钱七审批", result);
        }

        @Test
        @DisplayName("should replace all 9 wildcard patterns at once")
        void shouldReplaceAllNineWildcardPatterns() throws Exception {
            Map<Integer, String> map = new HashMap<>();
            map.put(WildcardCharacterEnum.ONE_CHARACTER.getCode(), "流程名");
            map.put(WildcardCharacterEnum.TWO_CHARACTER.getCode(), "编号1");
            map.put(WildcardCharacterEnum.THREE_CHARACTER.getCode(), "申请人A");
            map.put(WildcardCharacterEnum.FOUR_CHARACTER.getCode(), "审批人B");
            map.put(WildcardCharacterEnum.FIVE_CHARACTER.getCode(), "2024-03-01");
            map.put(WildcardCharacterEnum.SIX_CHARACTER.getCode(), "2024-03-01 08:00:00");
            map.put(WildcardCharacterEnum.SEVEN_CHARACTER.getCode(), "下一审批人C");
            map.put(WildcardCharacterEnum.EIGHT_CHARACTER.getCode(), "当前审批人D");
            map.put(WildcardCharacterEnum.NINE_CHARACTER.getCode(), "转发人E");

            String info = "{工作流名称}|{流程编号}|{申请人}|{被审批人}|{申请日期}(年月日)|{申请时间}(年月日时分秒)|{流转对象}(下一节点审批人)|{当前审批人}|{转发对象}";

            String result = (String) translateMethod.invoke(informationTemplateUtils, info, map);

            assertEquals("流程名|编号1|申请人A|审批人B|2024-03-01|2024-03-01 08:00:00|下一审批人C|当前审批人D|转发人E", result);
        }

        @Test
        @DisplayName("should not modify text without wildcards")
        void shouldNotModifyTextWithoutWildcards() throws Exception {
            Map<Integer, String> map = new HashMap<>();
            map.put(WildcardCharacterEnum.ONE_CHARACTER.getCode(), "流程名");

            String result = (String) translateMethod.invoke(informationTemplateUtils, "这是一段没有通配符的文本", map);

            assertEquals("这是一段没有通配符的文本", result);
        }

        @Test
        @DisplayName("should handle Chinese characters in wildcards correctly")
        void shouldHandleChineseCharactersInWildcardsCorrectly() throws Exception {
            Map<Integer, String> map = new HashMap<>();
            map.put(WildcardCharacterEnum.THREE_CHARACTER.getCode(), "测试用户");

            String result = (String) translateMethod.invoke(informationTemplateUtils, "您好{申请人}，欢迎", map);

            assertEquals("您好测试用户，欢迎", result);
        }

        @Test
        @DisplayName("should use empty string when map does not contain the code")
        void shouldUseEmptyStringWhenMapDoesNotContainCode() throws Exception {
            Map<Integer, String> map = new HashMap<>();

            String result = (String) translateMethod.invoke(informationTemplateUtils, "流程{工作流名称}已提交", map);

            assertEquals("流程已提交", result);
        }
    }
}
