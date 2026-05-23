package org.openoa.engine.bpmnconf.adp.processcallback;

import com.alibaba.fastjson2.JSON;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.engine.vo.CallbackRespVo;
import org.openoa.engine.vo.ProcBaseCallBackVo;

import static org.junit.jupiter.api.Assertions.*;

class ProcBaseCallBackAdpTest extends BaseTest {

    private final ProcBaseCallBackAdp adaptor = new ProcBaseCallBackAdp();

    @Nested
    @DisplayName("formatRequest")
    class FormatRequestTest {
        @Test
        @DisplayName("should extract formData from BpmnConfVo")
        void shouldExtractFormData() {
            BpmnConfVo vo = new BpmnConfVo();
            vo.setFormData("{\"key\":\"value\"}");
            ProcBaseCallBackVo result = adaptor.formatRequest(vo);
            assertNotNull(result);
            assertEquals("{\"key\":\"value\"}", result.getFormData());
        }

        @Test
        @DisplayName("should handle null formData")
        void shouldHandleNullFormData() {
            BpmnConfVo vo = new BpmnConfVo();
            ProcBaseCallBackVo result = adaptor.formatRequest(vo);
            assertNotNull(result);
            assertNull(result.getFormData());
        }

        @Test
        @DisplayName("should handle empty formData")
        void shouldHandleEmptyFormData() {
            BpmnConfVo vo = new BpmnConfVo();
            vo.setFormData("");
            ProcBaseCallBackVo result = adaptor.formatRequest(vo);
            assertEquals("", result.getFormData());
        }
    }

    @Nested
    @DisplayName("formatResponce")
    class FormatResponceTest {
        @Test
        @DisplayName("should parse JSON to CallbackRespVo")
        void shouldParseJson() {
            String json = "{\"status\":\"success\",\"businessId\":\"biz001\",\"businessPartyMark\":\"PARTY_A\",\"extend\":\"extra\"}";
            CallbackRespVo result = adaptor.formatResponce(json);
            assertNotNull(result);
            assertEquals("success", result.getStatus());
            assertEquals("biz001", result.getBusinessId());
            assertEquals("PARTY_A", result.getBusinessPartyMark());
            assertEquals("extra", result.getExtend());
        }

        @Test
        @DisplayName("should handle minimal JSON")
        void shouldHandleMinimalJson() {
            String json = "{}";
            CallbackRespVo result = adaptor.formatResponce(json);
            assertNotNull(result);
            assertNull(result.getStatus());
            assertNull(result.getBusinessId());
        }

        @Test
        @DisplayName("should handle partial JSON")
        void shouldHandlePartialJson() {
            String json = "{\"status\":\"error\"}";
            CallbackRespVo result = adaptor.formatResponce(json);
            assertEquals("error", result.getStatus());
            assertNull(result.getBusinessId());
        }
    }
}
