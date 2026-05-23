package org.openoa.base.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

class AFExecutionEntityTest extends BaseTest {

    @Nested
    @DisplayName("clone")
    class CloneTest {
        @Test
        @DisplayName("should clone all fields")
        void shouldCloneAllFields() {
            AFExecutionEntity original = new AFExecutionEntity();
            original.setId("exec001");
            original.setRevision(2);
            original.setProcessInstanceId("procInst001");
            original.setBusinessKey("biz001");
            original.setProcessDefinitionId("procDef:1:1");
            original.setActivityId("task1");
            original.setIsActive(true);
            original.setIsConcurrent(false);
            original.setIsScope(true);
            original.setIsEventScope(false);
            original.setParentId("parent001");
            original.setSuperExecutionId("super001");
            original.setSuspensionState(1);
            original.setCachedEntityState(3);
            original.setTenantId(100);
            original.setName("testExecution");

            AFExecutionEntity cloned = original.clone();

            assertNotSame(original, cloned);
            assertEquals("exec001", cloned.getId());
            assertEquals(2, cloned.getRevision());
            assertEquals("procInst001", cloned.getProcessInstanceId());
            assertEquals("biz001", cloned.getBusinessKey());
            assertEquals("procDef:1:1", cloned.getProcessDefinitionId());
            assertEquals("task1", cloned.getActivityId());
            assertTrue(cloned.getIsActive());
            assertFalse(cloned.getIsConcurrent());
            assertTrue(cloned.getIsScope());
            assertFalse(cloned.getIsEventScope());
            assertEquals("parent001", cloned.getParentId());
            assertEquals("super001", cloned.getSuperExecutionId());
            assertEquals(1, cloned.getSuspensionState());
            assertEquals(3, cloned.getCachedEntityState());
            assertEquals(100, cloned.getTenantId());
            assertEquals("testExecution", cloned.getName());
        }

        @Test
        @DisplayName("should not affect original when modifying clone")
        void shouldNotAffectOriginal() {
            AFExecutionEntity original = new AFExecutionEntity();
            original.setId("original");
            original.setIsActive(true);

            AFExecutionEntity cloned = original.clone();
            cloned.setId("cloned");
            cloned.setIsActive(false);

            assertEquals("original", original.getId());
            assertTrue(original.getIsActive());
        }

        @Test
        @DisplayName("should handle null fields in clone")
        void shouldHandleNullFields() {
            AFExecutionEntity original = new AFExecutionEntity();
            AFExecutionEntity cloned = original.clone();
            assertNull(cloned.getId());
            assertNull(cloned.getProcessInstanceId());
            assertNull(cloned.getBusinessKey());
        }
    }
}
