package org.openoa.engine.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.engine.vo.CallbackReqVo;
import org.openoa.engine.vo.CallbackRespVo;

import static org.junit.jupiter.api.Assertions.*;

class CallbackAdaptorTest extends BaseTest {

    @Nested
    @DisplayName("getNewRespObj")
    class GetNewRespObjTest {
        @Test
        @DisplayName("should create new instance of resp type via reflection")
        void shouldCreateNewInstanceOfRespType() throws Exception {
            CallbackAdaptor<CallbackReqVo, CallbackRespVo> adaptor = new TestCallbackAdaptor();

            CallbackRespVo result = adaptor.getNewRespObj();

            assertNotNull(result);
        }

        @Test
        @DisplayName("should create different instances on each call")
        void shouldCreateDifferentInstancesOnEachCall() throws Exception {
            CallbackAdaptor<CallbackReqVo, CallbackRespVo> adaptor = new TestCallbackAdaptor();

            CallbackRespVo instance1 = adaptor.getNewRespObj();
            CallbackRespVo instance2 = adaptor.getNewRespObj();

            assertNotSame(instance1, instance2);
        }

        @Test
        @DisplayName("created instance should be assignable to resp type")
        void createdInstanceShouldBeAssignableToRespType() throws Exception {
            CallbackAdaptor<CallbackReqVo, CallbackRespVo> adaptor = new TestCallbackAdaptor();

            CallbackRespVo result = adaptor.getNewRespObj();

            assertTrue(result instanceof CallbackRespVo);
        }

        @Test
        @DisplayName("created instance fields should be default values")
        void createdInstanceFieldsShouldBeDefaultValues() throws Exception {
            CallbackAdaptor<CallbackReqVo, CallbackRespVo> adaptor = new TestCallbackAdaptor();

            CallbackRespVo result = adaptor.getNewRespObj();

            assertNull(result.getStatus());
            assertNull(result.getBusinessId());
            assertNull(result.getBusinessPartyMark());
            assertNull(result.getExtend());
        }
    }

    private static class TestCallbackAdaptor implements CallbackAdaptor<CallbackReqVo, CallbackRespVo> {
        @Override
        public CallbackReqVo formatRequest(org.openoa.base.vo.BpmnConfVo bpmnConfVo) {
            return new CallbackReqVo();
        }

        @Override
        public CallbackRespVo formatResponce(String resultJson) {
            return new CallbackRespVo();
        }
    }
}
