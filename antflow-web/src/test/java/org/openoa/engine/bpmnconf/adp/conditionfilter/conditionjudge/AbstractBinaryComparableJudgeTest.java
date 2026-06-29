package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnStartConditionsVo;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AbstractBinaryComparableJudgeTest extends BaseTest {

    private TestableBinaryJudge judge;

    static class TestableBinaryJudge extends AbstractBinaryComparableJudge {
        private final String dbFieldName;
        private final String actualFieldName;

        TestableBinaryJudge(String dbFieldName, String actualFieldName) {
            this.dbFieldName = dbFieldName;
            this.actualFieldName = actualFieldName;
        }

        @Override
        protected String fieldNameInDb() {
            return dbFieldName;
        }

        @Override
        protected String fieldNameInStartConditions() {
            return actualFieldName;
        }
    }

    @BeforeEach
    void setUp() {
        judge = new TestableBinaryJudge("totalMoney", "totalMoney");
    }

    @Nested
    @DisplayName("single value comparison (non-binary operators)")
    class SingleValueComparisonTest {

        @Test
        @DisplayName("should match when actual >= conf (GTE, operator 1)")
        void shouldMatchGTE() {
            BpmnNodeConditionsConfBaseVo conf = createConf("100", 1);
            BpmnStartConditionsVo startConditions = createStartConditions("200");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should match when actual == conf (GTE, operator 1)")
        void shouldMatchGTEWhenEqual() {
            BpmnNodeConditionsConfBaseVo conf = createConf("100", 1);
            BpmnStartConditionsVo startConditions = createStartConditions("100");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should not match when actual < conf (GTE, operator 1)")
        void shouldNotMatchGTE() {
            BpmnNodeConditionsConfBaseVo conf = createConf("100", 1);
            BpmnStartConditionsVo startConditions = createStartConditions("50");

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should match when actual > conf (GT, operator 2)")
        void shouldMatchGT() {
            BpmnNodeConditionsConfBaseVo conf = createConf("100", 2);
            BpmnStartConditionsVo startConditions = createStartConditions("150");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should not match when actual == conf (GT, operator 2)")
        void shouldNotMatchGTWhenEqual() {
            BpmnNodeConditionsConfBaseVo conf = createConf("100", 2);
            BpmnStartConditionsVo startConditions = createStartConditions("100");

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should match when actual <= conf (LTE, operator 3)")
        void shouldMatchLTE() {
            BpmnNodeConditionsConfBaseVo conf = createConf("100", 3);
            BpmnStartConditionsVo startConditions = createStartConditions("50");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should match when actual == conf (EQ, operator 5)")
        void shouldMatchEQ() {
            BpmnNodeConditionsConfBaseVo conf = createConf("100", 5);
            BpmnStartConditionsVo startConditions = createStartConditions("100");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should not match when actual != conf (EQ, operator 5)")
        void shouldNotMatchEQ() {
            BpmnNodeConditionsConfBaseVo conf = createConf("100", 5);
            BpmnStartConditionsVo startConditions = createStartConditions("99");

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }
    }

    @Nested
    @DisplayName("binary range comparison (operators 6-9)")
    class BinaryRangeComparisonTest {

        @Test
        @DisplayName("should match when actual within open range (operator 6: conf1 < actual < conf2)")
        void shouldMatchOpenRange() {
            BpmnNodeConditionsConfBaseVo conf = createConf("100,200", 6);
            BpmnStartConditionsVo startConditions = createStartConditions("150");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should not match left boundary in open range (operator 6)")
        void shouldNotMatchLeftBoundaryOpenRange() {
            BpmnNodeConditionsConfBaseVo conf = createConf("100,200", 6);
            BpmnStartConditionsVo startConditions = createStartConditions("100");

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should not match right boundary in open range (operator 6)")
        void shouldNotMatchRightBoundaryOpenRange() {
            BpmnNodeConditionsConfBaseVo conf = createConf("100,200", 6);
            BpmnStartConditionsVo startConditions = createStartConditions("200");

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should match left boundary in left-closed right-open range (operator 7)")
        void shouldMatchLeftBoundaryClosedOpen() {
            BpmnNodeConditionsConfBaseVo conf = createConf("100,200", 7);
            BpmnStartConditionsVo startConditions = createStartConditions("100");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should not match right boundary in left-closed right-open range (operator 7)")
        void shouldNotMatchRightBoundaryClosedOpen() {
            BpmnNodeConditionsConfBaseVo conf = createConf("100,200", 7);
            BpmnStartConditionsVo startConditions = createStartConditions("200");

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should match right boundary in left-open right-closed range (operator 8)")
        void shouldMatchRightBoundaryOpenClosed() {
            BpmnNodeConditionsConfBaseVo conf = createConf("100,200", 8);
            BpmnStartConditionsVo startConditions = createStartConditions("200");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should match both boundaries in closed range (operator 9)")
        void shouldMatchBothBoundariesClosed() {
            BpmnNodeConditionsConfBaseVo conf = createConf("100,200", 9);

            BpmnStartConditionsVo leftBoundary = createStartConditions("100");
            BpmnStartConditionsVo rightBoundary = createStartConditions("200");

            assertTrue(judge.judge("node1", conf, leftBoundary, 0));
            assertTrue(judge.judge("node1", conf, rightBoundary, 0));
        }

        @Test
        @DisplayName("should not match value outside range")
        void shouldNotMatchOutsideRange() {
            BpmnNodeConditionsConfBaseVo conf = createConf("100,200", 9);
            BpmnStartConditionsVo startConditions = createStartConditions("300");

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }
    }

    @Nested
    @DisplayName("null and edge cases")
    class NullAndEdgeCasesTest {

        @Test
        @DisplayName("should return false when conf field is null")
        void shouldReturnFalseWhenConfFieldNull() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            conf.setNumberOperator(5);

            BpmnStartConditionsVo startConditions = createStartConditions("100");

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should return false when actual field is null")
        void shouldReturnFalseWhenActualFieldNull() {
            BpmnNodeConditionsConfBaseVo conf = createConf("100", 5);
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should handle decimal values correctly")
        void shouldHandleDecimalValues() {
            BpmnNodeConditionsConfBaseVo conf = createConf("100.50", 5);
            BpmnStartConditionsVo startConditions = createStartConditions("100.50");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should handle negative values correctly")
        void shouldHandleNegativeValues() {
            BpmnNodeConditionsConfBaseVo conf = createConf("-50", 1);
            BpmnStartConditionsVo startConditions = createStartConditions("-10");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should handle zero values correctly")
        void shouldHandleZeroValues() {
            BpmnNodeConditionsConfBaseVo conf = createConf("0", 5);
            BpmnStartConditionsVo startConditions = createStartConditions("0");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }
    }

    private BpmnNodeConditionsConfBaseVo createConf(String totalMoneyValue, int operatorCode) {
        BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
        conf.setTotalMoney(totalMoneyValue);
        conf.setNumberOperator(operatorCode);
        return conf;
    }

    private BpmnStartConditionsVo createStartConditions(String totalMoneyValue) {
        BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
        startConditions.setTotalMoney(totalMoneyValue);
        return startConditions;
    }
}
