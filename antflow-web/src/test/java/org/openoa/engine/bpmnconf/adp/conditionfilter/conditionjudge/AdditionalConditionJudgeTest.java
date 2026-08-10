package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.BaseTest;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class AdditionalConditionJudgeTest extends BaseTest {

    private OutTotalMoneyJudge outTotalMoneyJudge;
    private ThirdAccountJudge thirdAccountJudge;
    private PurchaseTypeJudge purchaseTypeJudge;
    private BpmnTemplateMarkJudge bpmnTemplateMarkJudge;
    private TotalMoneyJudge totalMoneyJudge;
    private PurchaseTotalMoneyJudge purchaseTotalMoneyJudge;
    private AskLeaveJudge askLeaveJudge;

    @BeforeEach
    void setUp() {
        outTotalMoneyJudge = new OutTotalMoneyJudge();
        thirdAccountJudge = new ThirdAccountJudge();
        purchaseTypeJudge = new PurchaseTypeJudge();
        bpmnTemplateMarkJudge = new BpmnTemplateMarkJudge();
        totalMoneyJudge = new TotalMoneyJudge();
        purchaseTotalMoneyJudge = new PurchaseTotalMoneyJudge();
        askLeaveJudge = new AskLeaveJudge();
    }

    @Nested
    @DisplayName("OutTotalMoneyJudge")
    class OutTotalMoneyJudgeTest {

        @Test
        @DisplayName("should return true when amounts are equal with operator 5")
        void shouldReturnTrueWhenEqualAmounts() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            conditionsConf.setOutTotalMoney("500");
            conditionsConf.setNumberOperator(5);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setOutTotalMoney("500");

            assertTrue(outTotalMoneyJudge.judge("node1", conditionsConf, startConditions, 0));
        }

        @Test
        @DisplayName("should return true when user amount is greater than db amount with operator 2")
        void shouldReturnTrueWhenUserAmountGreaterThanDb() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            conditionsConf.setOutTotalMoney("100");
            conditionsConf.setNumberOperator(2);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setOutTotalMoney("200");

            assertTrue(outTotalMoneyJudge.judge("node1", conditionsConf, startConditions, 0));
        }

        @Test
        @DisplayName("should return true when user amount is less than db amount with operator 4")
        void shouldReturnTrueWhenUserAmountLessThanDb() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            conditionsConf.setOutTotalMoney("500");
            conditionsConf.setNumberOperator(4);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setOutTotalMoney("300");

            assertTrue(outTotalMoneyJudge.judge("node1", conditionsConf, startConditions, 0));
        }

        @Test
        @DisplayName("should throw AFBizException when conf outTotalMoney is null")
        void shouldThrowWhenConfOutTotalMoneyNull() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            conditionsConf.setNumberOperator(5);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setOutTotalMoney("500");

            assertThrows(AFBizException.class, () ->
                    outTotalMoneyJudge.judge("node1", conditionsConf, startConditions, 0));
        }

        @Test
        @DisplayName("should throw AFBizException when start conditions outTotalMoney is null")
        void shouldThrowWhenStartOutTotalMoneyNull() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            conditionsConf.setOutTotalMoney("500");
            conditionsConf.setNumberOperator(5);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            assertThrows(AFBizException.class, () ->
                    outTotalMoneyJudge.judge("node1", conditionsConf, startConditions, 0));
        }
    }

    @Nested
    @DisplayName("ThirdAccountJudge")
    class ThirdAccountJudgeTest {

        @Test
        @DisplayName("should return true when account type is contained in conf list")
        void shouldReturnTrueWhenAccountTypeContained() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            conditionsConf.setAccountType(Arrays.asList(1, 2, 3));

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setAccountType(2);

            assertTrue(thirdAccountJudge.judge("node1", conditionsConf, startConditions, 0));
        }

        @Test
        @DisplayName("should return false when account type is not in conf list")
        void shouldReturnFalseWhenAccountTypeNotInList() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            conditionsConf.setAccountType(Arrays.asList(1, 2, 3));

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setAccountType(5);

            assertFalse(thirdAccountJudge.judge("node1", conditionsConf, startConditions, 0));
        }

        @Test
        @DisplayName("should throw AFBizException when conf accountType is empty")
        void shouldThrowWhenConfAccountTypeEmpty() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setAccountType(1);

            assertThrows(AFBizException.class, () ->
                    thirdAccountJudge.judge("node1", conditionsConf, startConditions, 0));
        }

        @Test
        @DisplayName("should return false when user accountType is null")
        void shouldReturnFalseWhenUserAccountTypeNull() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            conditionsConf.setAccountType(Arrays.asList(1, 2, 3));

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            assertFalse(thirdAccountJudge.judge("node1", conditionsConf, startConditions, 0));
        }
    }

    @Nested
    @DisplayName("PurchaseTypeJudge")
    class PurchaseTypeJudgeTest {

        @Test
        @DisplayName("should return false when purchase type is List vs Integer comparison (source code bug)")
        void shouldReturnFalseWhenPurchaseTypeMatchesDueToListEqualsIntegerBug() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            conditionsConf.setPurchaseType(Collections.singletonList(1));

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setPurchaseType(1);

            assertFalse(purchaseTypeJudge.judge("node1", conditionsConf, startConditions, 0));
        }

        @Test
        @DisplayName("should return false when purchase type does not match")
        void shouldReturnFalseWhenPurchaseTypeNotMatch() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            conditionsConf.setPurchaseType(Collections.singletonList(1));

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setPurchaseType(2);

            assertFalse(purchaseTypeJudge.judge("node1", conditionsConf, startConditions, 0));
        }

        @Test
        @DisplayName("should throw AFBizException when conf purchaseType is empty")
        void shouldThrowWhenConfPurchaseTypeEmpty() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setPurchaseType(1);

            assertThrows(AFBizException.class, () ->
                    purchaseTypeJudge.judge("node1", conditionsConf, startConditions, 0));
        }

        @Test
        @DisplayName("should throw AFBizException when user purchaseType is null")
        void shouldThrowWhenUserPurchaseTypeNull() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            conditionsConf.setPurchaseType(Collections.singletonList(1));

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            assertThrows(AFBizException.class, () ->
                    purchaseTypeJudge.judge("node1", conditionsConf, startConditions, 0));
        }
    }

    @Nested
    @DisplayName("BpmnTemplateMarkJudge")
    class BpmnTemplateMarkJudgeTest {

        @Test
        @DisplayName("should return true when template mark matches")
        void shouldReturnTrueWhenTemplateMarkMatches() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            conditionsConf.setTemplateMarks(Arrays.asList(1, 2, 3));

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setTemplateMarkIds(Arrays.asList(3, 4, 5));

            assertTrue(bpmnTemplateMarkJudge.judge("node1", conditionsConf, startConditions, 0));
        }

        @Test
        @DisplayName("should return false when no matching template mark")
        void shouldReturnFalseWhenNoMatchingTemplateMark() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            conditionsConf.setTemplateMarks(Arrays.asList(1, 2, 3));

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setTemplateMarkIds(Arrays.asList(4, 5, 6));

            assertFalse(bpmnTemplateMarkJudge.judge("node1", conditionsConf, startConditions, 0));
        }

        @Test
        @DisplayName("should return false when template marks in conf is empty")
        void shouldReturnFalseWhenConfTemplateMarksEmpty() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setTemplateMarkIds(Arrays.asList(1, 2));

            assertFalse(bpmnTemplateMarkJudge.judge("node1", conditionsConf, startConditions, 0));
        }

        @Test
        @DisplayName("should return false when template mark ids from user is null")
        void shouldReturnFalseWhenUserTemplateMarkIdsNull() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            conditionsConf.setTemplateMarks(Arrays.asList(1, 2, 3));

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            assertFalse(bpmnTemplateMarkJudge.judge("node1", conditionsConf, startConditions, 0));
        }
    }

    @Nested
    @DisplayName("TotalMoneyJudge")
    class TotalMoneyJudgeTest {

        @Test
        @DisplayName("should return true when total money is equal with operator 5")
        void shouldReturnTrueWhenEqualAmounts() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            conditionsConf.setTotalMoney("500");
            conditionsConf.setNumberOperator(5);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setTotalMoney("500");

            assertTrue(totalMoneyJudge.judge("node1", conditionsConf, startConditions, 0));
        }

        @Test
        @DisplayName("should return true when total money is within range with operator 6")
        void shouldReturnTrueWhenWithinRange() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            conditionsConf.setTotalMoney("100,500");
            conditionsConf.setNumberOperator(6);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setTotalMoney("300");

            assertTrue(totalMoneyJudge.judge("node1", conditionsConf, startConditions, 0));
        }

        @Test
        @DisplayName("should return false when totalMoney in conf is null")
        void shouldReturnFalseWhenConfTotalMoneyNull() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            conditionsConf.setNumberOperator(5);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setTotalMoney("500");

            assertFalse(totalMoneyJudge.judge("node1", conditionsConf, startConditions, 0));
        }
    }

    @Nested
    @DisplayName("PurchaseTotalMoneyJudge")
    class PurchaseTotalMoneyJudgeTest {

        @Test
        @DisplayName("should return true when purchase total money is equal with operator 5")
        void shouldReturnTrueWhenEqualAmounts() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            conditionsConf.setPlanProcurementTotalMoney(300.0);
            conditionsConf.setNumberOperator(5);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setPlanProcurementTotalMoney(300.0);

            assertTrue(purchaseTotalMoneyJudge.judge("node1", conditionsConf, startConditions, 0));
        }

        @Test
        @DisplayName("should throw when binary operator 9 used with Double field that has no range")
        void shouldThrowWhenBinaryOperatorWithDoubleField() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            conditionsConf.setPlanProcurementTotalMoney(100.0);
            conditionsConf.setNumberOperator(9);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setPlanProcurementTotalMoney(300.0);

            assertThrows(ArrayIndexOutOfBoundsException.class, () ->
                    purchaseTotalMoneyJudge.judge("node1", conditionsConf, startConditions, 0));
        }
    }

    @Nested
    @DisplayName("AskLeaveJudge")
    class AskLeaveJudgeTest {

        @Test
        @DisplayName("should return true when user leave hours greater than db threshold with operator 2")
        void shouldReturnTrueWhenUserLeaveHoursGreaterThanThreshold() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            conditionsConf.setLeaveHour("8");
            conditionsConf.setNumberOperator(2);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setLeaveHour(16.0);

            assertTrue(askLeaveJudge.judge("node1", conditionsConf, startConditions, 0));
        }

        @Test
        @DisplayName("should return true when leave hours is within range with operator 6")
        void shouldReturnTrueWhenWithinRange() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            conditionsConf.setLeaveHour("8,24");
            conditionsConf.setNumberOperator(6);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setLeaveHour(16.0);

            assertTrue(askLeaveJudge.judge("node1", conditionsConf, startConditions, 0));
        }
    }

    @Nested
    @DisplayName("OutTotalMoneyJudge boundary operators")
    class OutTotalMoneyRangeTest {
        @Test
        @DisplayName("should return true when outTotalMoney equals at boundary with operator 1 (GTE)")
        void shouldReturnTrueAtGTEBoundary() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            conditionsConf.setOutTotalMoney("100");
            conditionsConf.setNumberOperator(1);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setOutTotalMoney("100");

            assertTrue(outTotalMoneyJudge.judge("node1", conditionsConf, startConditions, 0));
        }

        @Test
        @DisplayName("should return true when outTotalMoney equals at boundary with operator 3 (LTE)")
        void shouldReturnTrueAtLTEBoundary() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            conditionsConf.setOutTotalMoney("500");
            conditionsConf.setNumberOperator(3);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setOutTotalMoney("500");

            assertTrue(outTotalMoneyJudge.judge("node1", conditionsConf, startConditions, 0));
        }

        @Test
        @DisplayName("should return false when outTotalMoney is less with operator 1 (GTE)")
        void shouldReturnFalseWhenLessWithGTE() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            conditionsConf.setOutTotalMoney("500");
            conditionsConf.setNumberOperator(1);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setOutTotalMoney("300");

            assertFalse(outTotalMoneyJudge.judge("node1", conditionsConf, startConditions, 0));
        }

        @Test
        @DisplayName("should return false when outTotalMoney is greater with operator 3 (LTE)")
        void shouldReturnFalseWhenGreaterWithLTE() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            conditionsConf.setOutTotalMoney("100");
            conditionsConf.setNumberOperator(3);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setOutTotalMoney("300");

            assertFalse(outTotalMoneyJudge.judge("node1", conditionsConf, startConditions, 0));
        }
    }

    @Nested
    @DisplayName("TotalMoneyJudge additional scenarios")
    class TotalMoneyAdditionalTest {
        @Test
        @DisplayName("should return true when total money is greater with operator 2")
        void shouldReturnTrueWhenGreater() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            conditionsConf.setTotalMoney("100");
            conditionsConf.setNumberOperator(2);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setTotalMoney("200");

            assertTrue(totalMoneyJudge.judge("node1", conditionsConf, startConditions, 0));
        }

        @Test
        @DisplayName("should return true when total money is less with operator 4")
        void shouldReturnTrueWhenLess() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            conditionsConf.setTotalMoney("500");
            conditionsConf.setNumberOperator(4);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setTotalMoney("300");

            assertTrue(totalMoneyJudge.judge("node1", conditionsConf, startConditions, 0));
        }

        @Test
        @DisplayName("should return false when user totalMoney is null")
        void shouldReturnFalseWhenUserTotalMoneyNull() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            conditionsConf.setTotalMoney("500");
            conditionsConf.setNumberOperator(5);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            assertFalse(totalMoneyJudge.judge("node1", conditionsConf, startConditions, 0));
        }
    }

    @Nested
    @DisplayName("AskLeaveJudge additional scenarios")
    class AskLeaveAdditionalTest {
        @Test
        @DisplayName("should return true when leave hours equal with operator 5")
        void shouldReturnTrueWhenEqual() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            conditionsConf.setLeaveHour("8");
            conditionsConf.setNumberOperator(5);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setLeaveHour(8.0);

            assertTrue(askLeaveJudge.judge("node1", conditionsConf, startConditions, 0));
        }

        @Test
        @DisplayName("should return true when leave hours within closed interval with operator 9")
        void shouldReturnTrueWithinClosedInterval() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            conditionsConf.setLeaveHour("4,24");
            conditionsConf.setNumberOperator(9);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setLeaveHour(8.0);

            assertTrue(askLeaveJudge.judge("node1", conditionsConf, startConditions, 0));
        }

        @Test
        @DisplayName("should return false when leave hours outside closed interval")
        void shouldReturnFalseOutsideClosedInterval() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            conditionsConf.setLeaveHour("4,8");
            conditionsConf.setNumberOperator(9);

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setLeaveHour(16.0);

            assertFalse(askLeaveJudge.judge("node1", conditionsConf, startConditions, 0));
        }
    }

    @Nested
    @DisplayName("BpmnTemplateMarkJudge additional scenarios")
    class BpmnTemplateMarkAdditionalTest {
        @Test
        @DisplayName("should return true when multiple template marks match")
        void shouldReturnTrueWhenMultipleMatches() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            conditionsConf.setTemplateMarks(Arrays.asList(1, 2, 3));

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setTemplateMarkIds(Arrays.asList(1, 2));

            assertTrue(bpmnTemplateMarkJudge.judge("node1", conditionsConf, startConditions, 0));
        }

        @Test
        @DisplayName("should return false when both lists are empty")
        void shouldReturnFalseWhenBothEmpty() {
            BpmnNodeConditionsConfBaseVo conditionsConf = new BpmnNodeConditionsConfBaseVo();
            conditionsConf.setTemplateMarks(Collections.emptyList());

            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setTemplateMarkIds(Collections.emptyList());

            assertFalse(bpmnTemplateMarkJudge.judge("node1", conditionsConf, startConditions, 0));
        }
    }
}
