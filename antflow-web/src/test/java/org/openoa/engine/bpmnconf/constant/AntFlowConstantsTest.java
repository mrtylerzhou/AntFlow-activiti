package org.openoa.engine.bpmnconf.constant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class AntFlowConstantsTest extends BaseTest {

    @Nested
    @DisplayName("task and node keys")
    class TaskNodeKeysTest {
        @Test
        @DisplayName("END_KEY should equal END_EVENT")
        void endKeyEqualsEndEvent() {
            assertEquals(AntFlowConstants.END_KEY, AntFlowConstants.END_EVENT);
        }

        @Test
        @DisplayName("END_KEY should be 'end'")
        void endKeyValue() {
            assertEquals("end", AntFlowConstants.END_KEY);
        }

        @Test
        @DisplayName("START_NODE should be specific value")
        void startNodeValue() {
            assertEquals("task1418018332271", AntFlowConstants.START_NODE);
        }

        @Test
        @DisplayName("TASK_KEY should be specific value")
        void taskKeyValue() {
            assertEquals("task1418018332279", AntFlowConstants.TASK_KEY);
        }

        @Test
        @DisplayName("START_NODE and TASK_KEY should differ")
        void startNodeDiffersFromTaskKey() {
            assertNotEquals(AntFlowConstants.START_NODE, AntFlowConstants.TASK_KEY);
        }
    }

    @Nested
    @DisplayName("gateway types")
    class GatewayTypesTest {
        @Test
        @DisplayName("PARALLEL_GATEWAY value")
        void parallelGateway() {
            assertEquals("parallelGateway", AntFlowConstants.PARALLEL_GATEWAY);
        }

        @Test
        @DisplayName("EXCLUSIVE_GATEWAY value")
        void exclusiveGateway() {
            assertEquals("exclusiveGateway", AntFlowConstants.EXCLUSIVE_GATEWAY);
        }

        @Test
        @DisplayName("INCLUSIVE_GATEWAY value")
        void inclusiveGateway() {
            assertEquals("inclusiveGateway", AntFlowConstants.INCLUSIVE_GATEWAY);
        }
    }

    @Nested
    @DisplayName("process variables")
    class ProcessVariablesTest {
        @Test
        @DisplayName("UNAPPROVED_TASK value")
        void unapprovedTask() {
            assertEquals("unapprovedTask", AntFlowConstants.UNAPPROVED_TASK);
        }

        @Test
        @DisplayName("SYSTEM_PROCESS_TASK_USER value")
        void systemProcessTaskUser() {
            assertEquals("systemProcessTaskUser", AntFlowConstants.SYSTEM_PROCESS_TASK_USER);
        }

        @Test
        @DisplayName("ASSIGNEE_JUMP value")
        void assigneeJump() {
            assertEquals("assigneeJump", AntFlowConstants.ASSIGNEE_JUMP);
        }

        @Test
        @DisplayName("HASSTART_NUM value")
        void hasStartNum() {
            assertEquals("hasStartNum", AntFlowConstants.HASSTART_NUM);
        }

        @Test
        @DisplayName("APPROVAL_USER_CODE value")
        void approvalUserCode() {
            assertEquals("approvalUserCode", AntFlowConstants.APPROVAL_USER_CODE);
        }

        @Test
        @DisplayName("IS_ROLLBACK value")
        void isRollback() {
            assertEquals("rollback", AntFlowConstants.IS_ROLLBACK);
        }

        @Test
        @DisplayName("START_USER value")
        void startUser() {
            assertEquals("startUser", AntFlowConstants.START_USER);
        }
    }

    @Nested
    @DisplayName("personnel roles")
    class PersonnelRolesTest {
        @Test
        @DisplayName("HRBP value")
        void hrbp() {
            assertEquals("HRBP", AntFlowConstants.HRBP);
        }

        @Test
        @DisplayName("HR_LEADER value")
        void hrLeader() {
            assertEquals("HRLeader", AntFlowConstants.HR_LEADER);
        }

        @Test
        @DisplayName("STAFF_ID value")
        void staffId() {
            assertEquals("staff", AntFlowConstants.STAFF_ID);
        }

        @Test
        @DisplayName("STAFF_HANDOVER value")
        void staffHandover() {
            assertEquals("handover", AntFlowConstants.STAFF_HANDOVER);
        }

        @Test
        @DisplayName("LEADER value")
        void leader() {
            assertEquals("leader", AntFlowConstants.LEADER);
        }
    }

    @Nested
    @DisplayName("element types")
    class ElementTypesTest {
        @Test
        @DisplayName("USER_TASK value")
        void userTask() {
            assertEquals("userTask", AntFlowConstants.USER_TASK);
        }

        @Test
        @DisplayName("SERVICE_TASK value")
        void serviceTask() {
            assertEquals("serviceTask", AntFlowConstants.SERVICE_TASK);
        }

        @Test
        @DisplayName("MAIL_TASK value")
        void mailTask() {
            assertEquals("mailTask", AntFlowConstants.MAIL_TASK);
        }

        @Test
        @DisplayName("END_PROCESS value")
        void endProcess() {
            assertEquals("endevent1", AntFlowConstants.END_PROCESS);
        }
    }

    @Nested
    @DisplayName("other constants")
    class OtherConstantsTest {
        @Test
        @DisplayName("TURN_AROUND value")
        void turnAround() {
            assertEquals("turnround", AntFlowConstants.TURN_AROUND);
        }

        @Test
        @DisplayName("candidateUsers value")
        void candidateUsers() {
            assertEquals("candidateUsers", AntFlowConstants.candidateUsers);
        }

        @Test
        @DisplayName("NUM_OPERATOR value")
        void numOperator() {
            assertEquals("numberOperator", AntFlowConstants.NUM_OPERATOR);
        }

        @Test
        @DisplayName("DRAWING_FINT value")
        void drawingFint() {
            assertEquals("宋体", AntFlowConstants.DRAWING_FINT);
        }

        @Test
        @DisplayName("SCRIPT_CONTEXT value")
        void scriptContext() {
            assertEquals("it", AntFlowConstants.SCRIPT_CONTEXT);
        }
    }
}
