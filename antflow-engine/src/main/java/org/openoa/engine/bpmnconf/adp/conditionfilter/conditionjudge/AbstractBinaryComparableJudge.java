package org.openoa.engine.bpmnconf.adp.conditionfilter.conditionjudge;

import com.alibaba.fastjson2.JSON;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.openoa.base.constant.enums.JudgeOperatorEnum;
import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * 为支持1<a<2这种类型的比较设计的,如果是普通的单值比较,请使用AbstractComparableJudge,第二个参数值为null即可
 */
public abstract class AbstractBinaryComparableJudge extends AbstractComparableJudge{
    private static final Logger log = LoggerFactory.getLogger(AbstractBinaryComparableJudge.class);

    @Override
    public boolean judge(String nodeId, BpmnNodeConditionsConfBaseVo conditionsConf, BpmnStartConditionsVo bpmnStartConditionsVo) {
        String fieldNameInDb = fieldNameInDb();
        String fieldNameActual=fieldNameInStartConditions();
        String fieldValueInDb = null;
        String fieldValueActual=null;
        BigDecimal fieldValueActualbig=null;
        String fieldValue1InDb=null;
        String fieldValue2InDb=null;
        BigDecimal fieldValue1InDbbig=null;
        BigDecimal fieldValue2InDbBig=null;
        //运算符类型
        Integer theOperatorType = conditionsConf.getNumberOperator();
        try {
            fieldValueInDb =  FieldUtils.readField(conditionsConf, fieldNameInDb, true).toString();
            fieldValueActual = FieldUtils.readField(bpmnStartConditionsVo, fieldNameActual, true).toString();
            fieldValueActualbig = new BigDecimal(fieldValueActual);

            if(JudgeOperatorEnum.binaryOperator().contains(theOperatorType)){
                String[] split = fieldValueInDb.split(",");
                fieldValue1InDb = split[0];
                fieldValue2InDb = split[1];
                fieldValue1InDbbig = new BigDecimal(fieldValue1InDb);
                fieldValue2InDbBig = new BigDecimal(fieldValue2InDb);
            }else{
                fieldValue1InDb = fieldValueInDb;
                fieldValue1InDbbig = new BigDecimal(fieldValue1InDb);
            }

        } catch (IllegalAccessException e) {
            log.error("获取配置条件字段名失败!配置值:{},实际条件值:{}", JSON.toJSON(conditionsConf),JSON.toJSONString(bpmnStartConditionsVo));
            throw new RuntimeException(e);
        }


        return super.compareJudge(fieldValue1InDbbig,fieldValue2InDbBig,fieldValueActualbig,theOperatorType);
    }
    //数据库中配置的条件名称,即配置条件模板时使用的,一般情况下如果是单值,建议使用一样的名字
    //但是可能模板配置的是集合类型,实际中提供的是单值,比如模板中是省份集合,实际条件是某一个省份,这时候再使用同样的字段名就不便于理解
    protected abstract String fieldNameInDb();
    protected  abstract String fieldNameInStartConditions();
}
