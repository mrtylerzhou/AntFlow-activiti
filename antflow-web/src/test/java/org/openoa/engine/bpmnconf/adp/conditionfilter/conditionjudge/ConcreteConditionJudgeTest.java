package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnStartConditionsVo;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class ConcreteConditionJudgeTest extends BaseTest {

    @Nested
    @DisplayName("TotalMoneyJudge")
    class TotalMoneyJudgeTest {

        private final TotalMoneyJudge judge = new TotalMoneyJudge();

        @Test
        @DisplayName("should match when totalMoney equals conf (operator 5/EQ)")
        void shouldMatchWhenTotalMoneyEqualsConf() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            conf.setTotalMoney("100");
            conf.setNumberOperator(5);
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setTotalMoney("100");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should match when totalMoney >= conf (operator 1/GTE)")
        void shouldMatchWhenTotalMoneyGteConf() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            conf.setTotalMoney("100");
            conf.setNumberOperator(1);
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setTotalMoney("200");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should match when totalMoney in range (operator 9/GTE1LTE2)")
        void shouldMatchWhenTotalMoneyInRange() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            conf.setTotalMoney("100,200");
            conf.setNumberOperator(9);
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setTotalMoney("150");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should return false when totalMoney is null in startConditions")
        void shouldReturnFalseWhenTotalMoneyNullInStartConditions() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            conf.setTotalMoney("100");
            conf.setNumberOperator(5);
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }
    }

    @Nested
    @DisplayName("AskLeaveJudge")
    class AskLeaveJudgeTest {

        private final AskLeaveJudge judge = new AskLeaveJudge();

        @Test
        @DisplayName("should match when leaveHour equals conf (operator 5/EQ)")
        void shouldMatchWhenLeaveHourEqualsConf() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            conf.setLeaveHour("8");
            conf.setNumberOperator(5);
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setLeaveHour(8.0);

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should match when leaveHour > conf (operator 2/GT)")
        void shouldMatchWhenLeaveHourGtConf() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            conf.setLeaveHour("8");
            conf.setNumberOperator(2);
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setLeaveHour(16.0);

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should return false when leaveHour is null")
        void shouldReturnFalseWhenLeaveHourNull() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            conf.setLeaveHour("8");
            conf.setNumberOperator(5);
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }
    }

    @Nested
    @DisplayName("PurchaseTotalMoneyJudge")
    class PurchaseTotalMoneyJudgeTest {

        private final PurchaseTotalMoneyJudge judge = new PurchaseTotalMoneyJudge();

        @Test
        @DisplayName("should match when planProcurementTotalMoney equals conf (operator 5/EQ)")
        void shouldMatchWhenPlanProcurementTotalMoneyEqualsConf() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            conf.setPlanProcurementTotalMoney(100.0);
            conf.setNumberOperator(5);
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setPlanProcurementTotalMoney(100.0);

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should return false when planProcurementTotalMoney is null")
        void shouldReturnFalseWhenPlanProcurementTotalMoneyNull() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            conf.setPlanProcurementTotalMoney(100.0);
            conf.setNumberOperator(5);
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }
    }

    @Nested
    @DisplayName("ParkAreaJudge")
    class ParkAreaJudgeTest {

        private final ParkAreaJudge judge = new ParkAreaJudge();

        @Test
        @DisplayName("should match when totalMoney equals parkArea conf (operator 5/EQ)")
        void shouldMatchWhenTotalMoneyEqualsParkAreaConf() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            conf.setParkArea(100.0);
            conf.setNumberOperator(5);
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setTotalMoney("100");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should return false when parkArea is null")
        void shouldReturnFalseWhenParkAreaNull() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            conf.setNumberOperator(5);
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setTotalMoney("100");

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }
    }

    @Nested
    @DisplayName("OutTotalMoneyJudge")
    class OutTotalMoneyJudgeTest {

        private final OutTotalMoneyJudge judge = new OutTotalMoneyJudge();

        @Test
        @DisplayName("should match when outTotalMoney equals conf (operator 5/EQ)")
        void shouldMatchWhenOutTotalMoneyEqualsConf() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            conf.setOutTotalMoney("100");
            conf.setNumberOperator(5);
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setOutTotalMoney("100");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should match when outTotalMoney > conf (operator 2/GT)")
        void shouldMatchWhenOutTotalMoneyGtConf() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            conf.setOutTotalMoney("100");
            conf.setNumberOperator(2);
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setOutTotalMoney("200");

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should throw AFBizException when conf outTotalMoney is null")
        void shouldThrowWhenConfOutTotalMoneyNull() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            conf.setNumberOperator(5);
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setOutTotalMoney("100");

            assertThrows(AFBizException.class, () -> judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should throw AFBizException when startConditions outTotalMoney is null")
        void shouldThrowWhenStartConditionsOutTotalMoneyNull() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            conf.setOutTotalMoney("100");
            conf.setNumberOperator(5);
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            assertThrows(AFBizException.class, () -> judge.judge("node1", conf, startConditions, 0));
        }
    }

    @Nested
    @DisplayName("JobLevelJudge")
    class JobLevelJudgeTest {

        private final JobLevelJudge judge = new JobLevelJudge();

        @Test
        @DisplayName("should match when both id and name are equal")
        void shouldMatchWhenBothIdAndNameEqual() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            conf.setJobLevelVo(BaseIdTranStruVo.builder().id("1").name("Manager").build());
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setJobLevelVo(BaseIdTranStruVo.builder().id("1").name("Manager").build());

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should not match when id differs")
        void shouldNotMatchWhenIdDiffers() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            conf.setJobLevelVo(BaseIdTranStruVo.builder().id("1").name("Manager").build());
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setJobLevelVo(BaseIdTranStruVo.builder().id("2").name("Manager").build());

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should not match when name differs")
        void shouldNotMatchWhenNameDiffers() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            conf.setJobLevelVo(BaseIdTranStruVo.builder().id("1").name("Manager").build());
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setJobLevelVo(BaseIdTranStruVo.builder().id("1").name("Director").build());

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should throw AFBizException when conf jobLevelVo is null")
        void shouldThrowWhenConfJobLevelVoNull() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setJobLevelVo(BaseIdTranStruVo.builder().id("1").name("Manager").build());

            assertThrows(AFBizException.class, () -> judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should throw AFBizException when startConditions jobLevelVo is null")
        void shouldThrowWhenStartConditionsJobLevelVoNull() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            conf.setJobLevelVo(BaseIdTranStruVo.builder().id("1").name("Manager").build());
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            assertThrows(AFBizException.class, () -> judge.judge("node1", conf, startConditions, 0));
        }
    }

    @Nested
    @DisplayName("ThirdAccountJudge")
    class ThirdAccountJudgeTest {

        private final ThirdAccountJudge judge = new ThirdAccountJudge();

        @Test
        @DisplayName("should match when accountType contains the value")
        void shouldMatchWhenAccountTypeContainsValue() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            conf.setAccountType(Arrays.asList(1, 2, 3));
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setAccountType(2);

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should not match when accountType does not contain the value")
        void shouldNotMatchWhenAccountTypeDoesNotContainValue() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            conf.setAccountType(Arrays.asList(1, 2, 3));
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setAccountType(4);

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should throw AFBizException when conf accountType is empty")
        void shouldThrowWhenConfAccountTypeEmpty() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            conf.setAccountType(Collections.emptyList());
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setAccountType(1);

            assertThrows(AFBizException.class, () -> judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should return false when startConditions accountType is empty")
        void shouldReturnFalseWhenStartConditionsAccountTypeEmpty() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            conf.setAccountType(Arrays.asList(1, 2, 3));
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }
    }

    @Nested
    @DisplayName("PurchaseTypeJudge")
    class PurchaseTypeJudgeTest {

        private final PurchaseTypeJudge judge = new PurchaseTypeJudge();

        @Test
        @DisplayName("should not match when purchaseType types differ (List vs Integer)")
        void shouldNotMatchWhenPurchaseTypeTypesDiffer() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            conf.setPurchaseType(Arrays.asList(1, 2));
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setPurchaseType(1);

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should not match when purchaseType values differ")
        void shouldNotMatchWhenPurchaseTypeValuesDiffer() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            conf.setPurchaseType(Arrays.asList(1, 2));
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setPurchaseType(3);

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should throw AFBizException when conf purchaseType is empty")
        void shouldThrowWhenConfPurchaseTypeEmpty() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            conf.setPurchaseType(Collections.emptyList());
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setPurchaseType(1);

            assertThrows(AFBizException.class, () -> judge.judge("node1", conf, startConditions, 0));
        }
    }

    @Nested
    @DisplayName("MoneyOperatorJudge")
    class MoneyOperatorJudgeTest {

        private final MoneyOperatorJudge judge = new MoneyOperatorJudge();

        @Test
        @DisplayName("should always return true")
        void shouldAlwaysReturnTrue() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }
    }

    @Nested
    @DisplayName("BpmnTemplateMarkJudge")
    class BpmnTemplateMarkJudgeTest {

        private final BpmnTemplateMarkJudge judge = new BpmnTemplateMarkJudge();

        @Test
        @DisplayName("should match when templateMarks contains a templateMarkId")
        void shouldMatchWhenTemplateMarksContainsTemplateMarkId() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            conf.setTemplateMarks(Arrays.asList(1, 2, 3));
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setTemplateMarkIds(Arrays.asList(2, 4));

            assertTrue(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should not match when templateMarks does not contain any templateMarkId")
        void shouldNotMatchWhenTemplateMarksDoesNotContainAnyTemplateMarkId() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            conf.setTemplateMarks(Arrays.asList(1, 2, 3));
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setTemplateMarkIds(Arrays.asList(4, 5));

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should return false when templateMarks is empty")
        void shouldReturnFalseWhenTemplateMarksEmpty() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            conf.setTemplateMarks(Collections.emptyList());
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setTemplateMarkIds(Arrays.asList(1, 2));

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }

        @Test
        @DisplayName("should return false when templateMarkIds is empty")
        void shouldReturnFalseWhenTemplateMarkIdsEmpty() {
            BpmnNodeConditionsConfBaseVo conf = new BpmnNodeConditionsConfBaseVo();
            conf.setTemplateMarks(Arrays.asList(1, 2, 3));
            BpmnStartConditionsVo startConditions = new BpmnStartConditionsVo();
            startConditions.setTemplateMarkIds(Collections.emptyList());

            assertFalse(judge.judge("node1", conf, startConditions, 0));
        }
    }
}
