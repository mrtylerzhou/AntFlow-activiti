package org.openoa.base.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.entity.jsonconf.BpmnConfConfigJson;

import static org.junit.jupiter.api.Assertions.*;

class BpmnConfVoTest extends BaseTest {

    @Nested
    @DisplayName("getOrParseConfConfigJson")
    class GetOrParseConfConfigJsonTest {
        @Test
        @DisplayName("should return null when both obj and json are null")
        void shouldReturnNullWhenBothNull() {
            BpmnConfVo vo = new BpmnConfVo();
            assertNull(vo.getOrParseConfConfigJson(null));
        }

        @Test
        @DisplayName("should parse JSON when obj is null and json is provided")
        void shouldParseWhenObjNull() {
            BpmnConfVo vo = new BpmnConfVo();
            String json = "{\"viewPageButtons\":[],\"noticeTemplateConfig\":{}}";
            BpmnConfConfigJson result = vo.getOrParseConfConfigJson(json);
            assertNotNull(result);
        }

        @Test
        @DisplayName("should return cached obj on second call")
        void shouldReturnCachedObj() {
            BpmnConfVo vo = new BpmnConfVo();
            String json = "{\"viewPageButtons\":[],\"noticeTemplateConfig\":{}}";
            BpmnConfConfigJson first = vo.getOrParseConfConfigJson(json);
            BpmnConfConfigJson second = vo.getOrParseConfConfigJson(json);
            assertSame(first, second);
        }

        @Test
        @DisplayName("should return existing obj without re-parsing")
        void shouldReturnExistingObj() {
            BpmnConfVo vo = new BpmnConfVo();
            BpmnConfConfigJson preSet = new BpmnConfConfigJson();
            vo.setConfConfigJsonObj(preSet);
            BpmnConfConfigJson result = vo.getOrParseConfConfigJson("{\"viewPageButtons\":[]}");
            assertSame(preSet, result);
        }
    }

    @Nested
    @DisplayName("builder")
    class BuilderTest {
        @Test
        @DisplayName("should build with all fields")
        void shouldBuildWithAllFields() {
            BpmnConfVo vo = BpmnConfVo.builder()
                    .id(1L)
                    .bpmnCode("CODE001")
                    .bpmnName("Test Flow")
                    .formCode("FORM001")
                    .effectiveStatus(1)
                    .build();
            assertEquals(1L, vo.getId());
            assertEquals("CODE001", vo.getBpmnCode());
            assertEquals("Test Flow", vo.getBpmnName());
            assertEquals("FORM001", vo.getFormCode());
        }
    }
}
