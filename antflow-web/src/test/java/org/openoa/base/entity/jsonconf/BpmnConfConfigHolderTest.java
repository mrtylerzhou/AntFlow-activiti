package org.openoa.base.entity.jsonconf;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.constant.enums.MsgNoticeTypeEnum;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BaseNumIdStruVo;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BpmnTemplateVo;
import org.openoa.base.vo.BpmnViewPageButtonBaseVo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BpmnConfConfigHolderTest extends BaseTest {

    private BpmnConfConfigHolder createInstance() throws Exception {
        Constructor<BpmnConfConfigHolder> ctor = BpmnConfConfigHolder.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        return ctor.newInstance();
    }

    private Object invokePrivate(String methodName, Class<?>[] paramTypes, Object... args) throws Exception {
        Method method = BpmnConfConfigHolder.class.getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true);
        return method.invoke(null, args);
    }

    private Object invokePrivate(String methodName, Object... args) throws Exception {
        for (Method m : BpmnConfConfigHolder.class.getDeclaredMethods()) {
            if (m.getName().equals(methodName) && m.getParameterCount() == args.length) {
                m.setAccessible(true);
                return m.invoke(null, args);
            }
        }
        throw new NoSuchMethodException(methodName);
    }

    @Nested
    @DisplayName("private constructor")
    class PrivateConstructorTest {
        @Test
        @DisplayName("should be instantiable via reflection")
        void shouldBeInstantiable() throws Exception {
            BpmnConfConfigHolder holder = createInstance();
            assertNotNull(holder);
        }
    }

    @Nested
    @DisplayName("joinList")
    class JoinListTest {
        @Test
        @DisplayName("should return null for null list")
        void nullList() throws Exception {
            assertNull(invokePrivate("joinList", new Class[]{List.class}, (Object) null));
        }

        @Test
        @DisplayName("should return null for empty list")
        void emptyList() throws Exception {
            assertNull(invokePrivate("joinList", Arrays.asList()));
        }

        @Test
        @DisplayName("should join list elements with comma")
        void joinWithComma() throws Exception {
            assertEquals("a,b,c", invokePrivate("joinList", Arrays.asList("a", "b", "c")));
        }

        @Test
        @DisplayName("should join single element")
        void singleElement() throws Exception {
            assertEquals("hello", invokePrivate("joinList", Arrays.asList("hello")));
        }

        @Test
        @DisplayName("should join integer list")
        void integerList() throws Exception {
            assertEquals("1,2,3", invokePrivate("joinList", Arrays.asList(1, 2, 3)));
        }
    }

    @Nested
    @DisplayName("convertMessageSendTypeList")
    class ConvertMessageSendTypeListTest {
        @Test
        @DisplayName("should return null for null list")
        void nullList() throws Exception {
            assertNull(invokePrivate("convertMessageSendTypeList", new Class[]{List.class}, (Object) null));
        }

        @Test
        @DisplayName("should return null for empty list")
        void emptyList() throws Exception {
            assertNull(invokePrivate("convertMessageSendTypeList", Arrays.asList()));
        }

        @Test
        @DisplayName("should convert BaseNumIdStruVo list to comma-separated toString")
        void convertList() throws Exception {
            BaseNumIdStruVo v1 = new BaseNumIdStruVo();
            v1.setId(1L);
            v1.setName("email");
            BaseNumIdStruVo v2 = new BaseNumIdStruVo();
            v2.setId(2L);
            v2.setName("sms");
            String result = (String) invokePrivate("convertMessageSendTypeList", Arrays.asList(v1, v2));
            assertNotNull(result);
            assertTrue(result.contains("email"));
            assertTrue(result.contains("sms"));
            assertTrue(result.contains(","));
        }
    }

    @Nested
    @DisplayName("buildViewPageButtons")
    class BuildViewPageButtonsTest {
        @Test
        @DisplayName("should return null for null input")
        void nullInput() throws Exception {
            assertNull(invokePrivate("buildViewPageButtons",
                    new Class[]{BpmnViewPageButtonBaseVo.class}, (Object) null));
        }

        @Test
        @DisplayName("should map viewPageStart to viewType 1")
        void viewPageStartMapsToType1() throws Exception {
            BpmnViewPageButtonBaseVo input = BpmnViewPageButtonBaseVo.builder()
                    .viewPageStart(Arrays.asList(1, 2))
                    .build();
            @SuppressWarnings("unchecked")
            List<BpmnConfConfigJson.ViewPageButton> result =
                    (List<BpmnConfConfigJson.ViewPageButton>) invokePrivate("buildViewPageButtons", input);
            assertEquals(2, result.size());
            assertEquals(1, result.get(0).getViewType());
            assertEquals(1, result.get(0).getButtonType());
            assertEquals(1, result.get(1).getViewType());
            assertEquals(2, result.get(1).getButtonType());
        }

        @Test
        @DisplayName("should map viewPageOther to viewType 2")
        void viewPageOtherMapsToType2() throws Exception {
            BpmnViewPageButtonBaseVo input = BpmnViewPageButtonBaseVo.builder()
                    .viewPageOther(Arrays.asList(3))
                    .build();
            @SuppressWarnings("unchecked")
            List<BpmnConfConfigJson.ViewPageButton> result =
                    (List<BpmnConfConfigJson.ViewPageButton>) invokePrivate("buildViewPageButtons", input);
            assertEquals(1, result.size());
            assertEquals(2, result.get(0).getViewType());
            assertEquals(3, result.get(0).getButtonType());
        }

        @Test
        @DisplayName("should combine both page types")
        void combineBothTypes() throws Exception {
            BpmnViewPageButtonBaseVo input = BpmnViewPageButtonBaseVo.builder()
                    .viewPageStart(Arrays.asList(1))
                    .viewPageOther(Arrays.asList(2))
                    .build();
            @SuppressWarnings("unchecked")
            List<BpmnConfConfigJson.ViewPageButton> result =
                    (List<BpmnConfConfigJson.ViewPageButton>) invokePrivate("buildViewPageButtons", input);
            assertEquals(2, result.size());
            assertEquals(1, result.get(0).getViewType());
            assertEquals(2, result.get(1).getViewType());
        }
    }

    @Nested
    @DisplayName("buildNoticeTemplateConfig")
    class BuildNoticeTemplateConfigTest {
        @Test
        @DisplayName("should create config with one detail per MsgNoticeTypeEnum")
        void oneDetailPerEnum() throws Exception {
            BpmnConfConfigJson.NoticeTemplateConfig config =
                    (BpmnConfConfigJson.NoticeTemplateConfig) invokePrivate("buildNoticeTemplateConfig");
            assertNotNull(config);
            assertNotNull(config.getDetails());
            assertEquals(MsgNoticeTypeEnum.values().length, config.getDetails().size());
        }

        @Test
        @DisplayName("should use enum code as noticeTemplateType")
        void enumCodeAsType() throws Exception {
            BpmnConfConfigJson.NoticeTemplateConfig config =
                    (BpmnConfConfigJson.NoticeTemplateConfig) invokePrivate("buildNoticeTemplateConfig");
            assertEquals(MsgNoticeTypeEnum.PROCESS_FLOW.getCode(),
                    config.getDetails().get(0).getNoticeTemplateType());
        }

        @Test
        @DisplayName("should use enum defaultValue as noticeTemplateDetail")
        void enumDefaultAsDetail() throws Exception {
            BpmnConfConfigJson.NoticeTemplateConfig config =
                    (BpmnConfConfigJson.NoticeTemplateConfig) invokePrivate("buildNoticeTemplateConfig");
            assertEquals(MsgNoticeTypeEnum.PROCESS_FLOW.getDefaultValue(),
                    config.getDetails().get(0).getNoticeTemplateDetail());
        }
    }

    @Nested
    @DisplayName("buildConfTemplates")
    class BuildConfTemplatesTest {
        @Test
        @DisplayName("should return null for null template list")
        void nullTemplates() throws Exception {
            assertNull(invokePrivate("buildConfTemplates",
                    new Class[]{List.class, String.class}, null, "FORM001"));
        }

        @Test
        @DisplayName("should return null for empty template list")
        void emptyTemplates() throws Exception {
            assertNull(invokePrivate("buildConfTemplates", Arrays.asList(), "FORM001"));
        }

        @Test
        @DisplayName("should map template fields correctly")
        void mapTemplateFields() throws Exception {
            BpmnTemplateVo t = BpmnTemplateVo.builder()
                    .event(1)
                    .informIdList(Arrays.asList("I1", "I2"))
                    .empIdList(Arrays.asList("E1"))
                    .roleIdList(Arrays.asList("R1"))
                    .funcIdList(Arrays.asList("F1"))
                    .templateId(100L)
                    .build();
            @SuppressWarnings("unchecked")
            List<BpmnConfConfigJson.ConfTemplateConf> result =
                    (List<BpmnConfConfigJson.ConfTemplateConf>) invokePrivate("buildConfTemplates",
                            Arrays.asList(t), "FORM001");
            assertEquals(1, result.size());
            BpmnConfConfigJson.ConfTemplateConf conf = result.get(0);
            assertEquals(1, conf.getEvent());
            assertEquals("I1,I2", conf.getInforms());
            assertEquals("E1", conf.getEmps());
            assertEquals("R1", conf.getRoles());
            assertEquals("F1", conf.getFuncs());
            assertEquals(100L, conf.getTemplateId());
            assertEquals("FORM001", conf.getFormCode());
        }
    }

    @Nested
    @DisplayName("buildConfConfig")
    class BuildConfConfigTest {
        @Test
        @DisplayName("should build config without low-code when isLowCodeFlow is null")
        void noLowCodeWhenNull() {
            BpmnConfVo confVo = new BpmnConfVo();
            BpmnConfConfigJson config = BpmnConfConfigHolder.buildConfConfig(confVo);
            assertNotNull(config);
            assertNull(config.getLowCodeFormConfig());
        }

        @Test
        @DisplayName("should build config without low-code when isLowCodeFlow is 0")
        void noLowCodeWhenZero() {
            BpmnConfVo confVo = new BpmnConfVo();
            confVo.setIsLowCodeFlow(0);
            BpmnConfConfigJson config = BpmnConfConfigHolder.buildConfConfig(confVo);
            assertNull(config.getLowCodeFormConfig());
        }

        @Test
        @DisplayName("should build config with low-code when isLowCodeFlow is 1")
        void withLowCode() {
            BpmnConfVo confVo = new BpmnConfVo();
            confVo.setIsLowCodeFlow(1);
            confVo.setLfFormData("{\"fields\":[]}");
            BpmnConfConfigJson config = BpmnConfConfigHolder.buildConfConfig(confVo);
            assertNotNull(config.getLowCodeFormConfig());
            assertEquals("{\"fields\":[]}", config.getLowCodeFormConfig().getFormdata());
        }

        @Test
        @DisplayName("should always include noticeTemplateConfig")
        void alwaysIncludeNoticeTemplate() {
            BpmnConfVo confVo = new BpmnConfVo();
            BpmnConfConfigJson config = BpmnConfConfigHolder.buildConfConfig(confVo);
            assertNotNull(config.getNoticeTemplateConfig());
            assertNotNull(config.getNoticeTemplateConfig().getDetails());
        }
    }
}
