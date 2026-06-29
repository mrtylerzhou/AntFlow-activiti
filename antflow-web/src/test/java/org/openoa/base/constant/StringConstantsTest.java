package org.openoa.base.constant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class StringConstantsTest extends BaseTest {

    @Nested
    @DisplayName("StringConstants values")
    class ValuesTest {
        @Test
        @DisplayName("BPMN_CODE_SPLITMARK should be hyphen")
        void bpmnCodeSplitmark() {
            assertEquals("-", StringConstants.BPMN_CODE_SPLITMARK);
        }

        @Test
        @DisplayName("FORM_CODE_LINKMARK should be underscore")
        void formCodeLinkmark() {
            assertEquals("_", StringConstants.FORM_CODE_LINKMARK);
        }

        @Test
        @DisplayName("LOWFLOW_FORM_CODE should be LF")
        void lowflowFormCode() {
            assertEquals("LF", StringConstants.LOWFLOW_FORM_CODE);
        }

        @Test
        @DisplayName("SCAN_BASE_PACKAGES should be org.openoa")
        void scanBasePackages() {
            assertEquals("org.openoa", StringConstants.SCAN_BASE_PACKAGES);
        }

        @Test
        @DisplayName("PROJECT_NAME should be antFlow")
        void projectName() {
            assertEquals("antFlow", StringConstants.PROJECT_NAME);
        }

        @Test
        @DisplayName("DEFAULT_TENANT should be default")
        void defaultTenant() {
            assertEquals("default", StringConstants.DEFAULT_TENANT);
        }

        @Test
        @DisplayName("HIDDEN_FIELD_PERMISSION should be H")
        void hiddenFieldPermission() {
            assertEquals("H", StringConstants.HIDDEN_FIELD_PERMISSION);
        }

        @Test
        @DisplayName("READ_ONLY_FIELD_PERMISSION should be R")
        void readOnlyFieldPermission() {
            assertEquals("R", StringConstants.READ_ONLY_FIELD_PERMISSION);
        }
    }

    @Nested
    @DisplayName("ActVarKeys")
    class ActVarKeysTest {
        @Test
        @DisplayName("PROCINSTID should be procInstId")
        void procInstId() {
            assertEquals("procInstId", StringConstants.ActVarKeys.PROCINSTID);
        }

        @Test
        @DisplayName("BUSINESS_ID should be businessId")
        void businessId() {
            assertEquals("businessId", StringConstants.ActVarKeys.BUSINESS_ID);
        }

        @Test
        @DisplayName("BPMNCODE should be bpmnCode")
        void bpmnCode() {
            assertEquals("bpmnCode", StringConstants.ActVarKeys.BPMNCODE);
        }

        @Test
        @DisplayName("FORMCODE should be formCode")
        void formCode() {
            assertEquals("formCode", StringConstants.ActVarKeys.FORMCODE);
        }

        @Test
        @DisplayName("PROCERSS_NUMBER should be processNumber")
        void processNumber() {
            assertEquals("processNumber", StringConstants.ActVarKeys.PROCERSS_NUMBER);
        }

        @Test
        @DisplayName("Is_OUTSIDEPROC should be isOutsideProc")
        void isOutsideProc() {
            assertEquals("isOutsideProc", StringConstants.ActVarKeys.Is_OUTSIDEPROC);
        }
    }
}
