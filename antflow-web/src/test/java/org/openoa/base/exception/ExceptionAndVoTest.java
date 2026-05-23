package org.openoa.base.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnTemplateVo;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionAndVoTest extends BaseTest {

    @Nested
    @DisplayName("BusinessErrorEnum")
    class BusinessErrorEnumTest {
        @Test
        @DisplayName("PARAMS_IS_NULL should have code 1418300271")
        void paramsIsNullCode() {
            assertEquals(1418300271, BusinessErrorEnum.PARAMS_IS_NULL.getCode());
            assertEquals("参数为空错误", BusinessErrorEnum.PARAMS_IS_NULL.getMsg());
        }

        @Test
        @DisplayName("getCodeStr should return string representation of code")
        void getCodeStr() {
            assertEquals("1418300271", BusinessErrorEnum.PARAMS_IS_NULL.getCodeStr());
        }

        @Test
        @DisplayName("toString should contain code and msg")
        void toStringFormat() {
            String str = BusinessErrorEnum.PARAMS_IS_NULL.toString();
            assertTrue(str.contains("1418300271"));
            assertTrue(str.contains("参数为空错误"));
        }

        @Test
        @DisplayName("USER_NOT_EXIST should have correct msg")
        void userNotExistMsg() {
            assertEquals("用户不存在", BusinessErrorEnum.USER_NOT_EXIST.getMsg());
        }

        @Test
        @DisplayName("DATA_NOT_FOUND should have correct code and msg")
        void dataNotFoundCode() {
            assertEquals(1418300374, BusinessErrorEnum.DATA_NOT_FOUND.getCode());
            assertEquals("数据未找到", BusinessErrorEnum.DATA_NOT_FOUND.getMsg());
        }
    }

    @Nested
    @DisplayName("AFBizException")
    class AFBizExceptionTest {
        @Test
        @DisplayName("constructor with message and cause")
        void constructorWithMessageAndCause() {
            Throwable cause = new RuntimeException("root cause");
            AFBizException ex = new AFBizException("test message", cause);
            assertEquals("test message", ex.getMessage());
            assertEquals("1", ex.getCode());
            assertEquals(cause, ex.getCause());
        }

        @Test
        @DisplayName("constructor with code and message")
        void constructorWithCodeAndMessage() {
            AFBizException ex = new AFBizException("ERR001", "error message");
            assertEquals("ERR001", ex.getCode());
            assertEquals("error message", ex.getMessage());
            assertNull(ex.getErrT());
        }

        @Test
        @DisplayName("constructor with code, message and errT")
        void constructorWithCodeMessageErrT() {
            Object errT = new Object();
            AFBizException ex = new AFBizException("ERR002", "msg", errT);
            assertEquals("ERR002", ex.getCode());
            assertEquals(errT, ex.getErrT());
        }

        @Test
        @DisplayName("constructor with BusinessErrorEnum")
        void constructorWithBusinessErrorEnum() {
            AFBizException ex = new AFBizException(BusinessErrorEnum.PARAMS_IS_NULL);
            assertEquals("1418300271", ex.getCode());
            assertEquals("参数为空错误", ex.getMessage());
        }

        @Test
        @DisplayName("constructor with BusinessErrorEnum and custom message")
        void constructorWithBusinessErrorEnumAndCustomMsg() {
            AFBizException ex = new AFBizException(BusinessErrorEnum.PARAMS_IS_NULL, "custom msg");
            assertEquals("1418300271", ex.getCode());
            assertEquals("custom msg", ex.getMessage());
        }

        @Test
        @DisplayName("constructor with message only should use code=1")
        void constructorWithMessageOnly() {
            AFBizException ex = new AFBizException("simple message");
            assertEquals("1", ex.getCode());
            assertEquals("simple message", ex.getMessage());
        }

        @Test
        @DisplayName("constructor with type, code, message, subMessage")
        void constructorWithTypeCodeMessageSub() {
            AFBizException ex = new AFBizException(1, "CODE", "main msg", "sub msg");
            assertEquals(1, ex.getType());
            assertEquals("CODE", ex.getCode());
            assertEquals("main msg", ex.getMessage());
            assertEquals("sub msg", ex.getSubMessage());
        }

        @Test
        @DisplayName("constructor with type, errLevel, message, subMessage")
        void constructorWithTypeErrLevelMsgSub() {
            AFBizException ex = new AFBizException(2, 3, "msg", "sub");
            assertEquals(2, ex.getType());
            assertEquals(3, ex.getErrLevel());
            assertEquals("sub", ex.getSubMessage());
        }

        @Test
        @DisplayName("isLog should default to true and be settable")
        void isLogDefaultTrue() {
            AFBizException ex = new AFBizException("test");
            assertTrue(ex.getIsLog());
            ex.setIsLog(false);
            assertFalse(ex.getIsLog());
        }
    }

    @Nested
    @DisplayName("BpmnTemplateVo")
    class BpmnTemplateVoTest {
        @Test
        @DisplayName("setEmpList should auto-extract empIdList")
        void setEmpListShouldExtractIds() {
            BpmnTemplateVo vo = new BpmnTemplateVo();
            vo.setEmpList(Arrays.asList(
                    BaseIdTranStruVo.builder().id("emp1").name("Alice").build(),
                    BaseIdTranStruVo.builder().id("emp2").name("Bob").build()
            ));
            assertNotNull(vo.getEmpIdList());
            assertEquals(2, vo.getEmpIdList().size());
            assertEquals("emp1", vo.getEmpIdList().get(0));
            assertEquals("emp2", vo.getEmpIdList().get(1));
        }

        @Test
        @DisplayName("setRoleList should auto-extract roleIdList")
        void setRoleListShouldExtractIds() {
            BpmnTemplateVo vo = new BpmnTemplateVo();
            vo.setRoleList(Arrays.asList(
                    BaseIdTranStruVo.builder().id("role1").name("Admin").build()
            ));
            assertNotNull(vo.getRoleIdList());
            assertEquals(1, vo.getRoleIdList().size());
            assertEquals("role1", vo.getRoleIdList().get(0));
        }

        @Test
        @DisplayName("setEmpList with null should not set empIdList")
        void setEmpListNullShouldNotSetIds() {
            BpmnTemplateVo vo = new BpmnTemplateVo();
            vo.setEmpList(null);
            assertNull(vo.getEmpIdList());
        }

        @Test
        @DisplayName("setEmpList with empty list should not set empIdList")
        void setEmpListEmptyShouldNotSetIds() {
            BpmnTemplateVo vo = new BpmnTemplateVo();
            vo.setEmpList(Arrays.asList());
            assertNull(vo.getEmpIdList());
        }
    }
}
