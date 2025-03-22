package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

/**
 * it is a demo condition judge class,you can refer to it to write your only conditions judge logic
 * @Author TylerZhou
 * @Date 2024/6/22 20:47
 * @Version 0.5
 */
public class ParkAreaJudge extends AbstractBinaryComparableJudge {
    //这个方法没有实际意义，只是为了演示，可以删除,字段取名也比较随意
    @Override
    protected String fieldNameInDb() {
        return "parkArea";
    }

    @Override
    protected String fieldNameInStartConditions() {
        return "totalMoney";
    }
}
