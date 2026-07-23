package org.openoa.engine.conf.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.openoa.base.constant.enums.BpmnConfFlagsEnum;
import org.openoa.base.entity.BpmnConf;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.lowflow.service.LFFormDataRuntimeHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * DIY辅助表单运行期增强切面: 包裹 AbstractFormOperationAdaptor+ 的 previewSetCondition/launchParameters,
 * 在原方法构建 BpmnStartConditionsVo 之后,对"启用辅助表单的DIY流程"做字段抽取与条件/取人增强。
 *
 * 仅当 extraFlags 命中 USE_AUXILIARY_FORM 且非低代码流程(isLowCodeFlow!=1)时介入;
 * LF流程(自身已调 LFFormDataRuntimeHelper)与普通DIY/Spy流程全部跳过,互不影响。
 *
 * 流程: 按 confId 收集"条件字段名 ∪ 表单取人字段名" → 用 MethodHandles.lookup 从 businessDataVo
 * 按字段名反射取值(取不到抛异常)组装 lfFields → 调 populateLfConditions + processFormRelatedUserConf。
 */
@Slf4j
@Aspect
@Component
public class AuxiliaryFormApprovalServiceAspect {

    @Autowired
    private LFFormDataRuntimeHelper lfFormDataRuntimeHelper;

    @Around("execution(* org.openoa.engine.bpmnconf.adp.processoperation.AbstractFormOperationAdaptor+.previewSetCondition(..))")
    public Object aroundPreviewSetCondition(ProceedingJoinPoint pjp) throws Throwable {
        return enrich(pjp);
    }

    @Around("execution(* org.openoa.engine.bpmnconf.adp.processoperation.AbstractFormOperationAdaptor+.launchParameters(..))")
    public Object aroundLaunchParameters(ProceedingJoinPoint pjp) throws Throwable {
        return enrich(pjp);
    }

    private Object enrich(ProceedingJoinPoint pjp) throws Throwable {
        BusinessDataVo businessDataVo = (BusinessDataVo) pjp.getArgs()[0];
        Object ret = pjp.proceed();
        if (!(ret instanceof BpmnStartConditionsVo)) {
            return ret;
        }
        BpmnStartConditionsVo startConditionsVo = (BpmnStartConditionsVo) ret;

        //解析 confId/extraFlags/isLowCodeFlow: 优先用 businessDataVo.bpmnConfVo, 否则按 processNumber/formCode 解析
        Long confId;
        Integer extraFlags;
        Integer isLowCodeFlow;
        BpmnConfVo bpmnConfVo = businessDataVo.getBpmnConfVo();
        if (bpmnConfVo != null) {
            confId = bpmnConfVo.getId();
            extraFlags = bpmnConfVo.getExtraFlags();
            isLowCodeFlow = bpmnConfVo.getIsLowCodeFlow();
        } else {
            BpmnConf conf = lfFormDataRuntimeHelper.resolveBpmnConf(businessDataVo);
            if (conf == null) {
                return ret;
            }
            confId = conf.getId();
            extraFlags = conf.getExtraFlags();
            isLowCodeFlow = conf.getIsLowCodeFlow();
        }

        //gate: LF流程自身已处理; 未启用辅助表单的流程不介入
        if (isLowCodeFlow != null && isLowCodeFlow == 1) {
            return ret;
        }
        if (extraFlags == null || !BpmnConfFlagsEnum.USE_AUXILIARY_FORM.flagsContainsCurrent(extraFlags)) {
            return ret;
        }

        //收集已引用的辅助表单字段名(条件 ∪ 表单取人);空则表示未配置,直接返回
        Set<String> fieldNames = lfFormDataRuntimeHelper.collectReferencedFieldNames(confId);
        if (fieldNames.isEmpty()) {
            return ret;
        }

        //按字段名从 businessDataVo 反射取值组装 lfFields;取不到抛异常强制用户解决
        Map<String, Object> lfFields = lfFormDataRuntimeHelper.extractFieldsByLookup(businessDataVo, fieldNames);
        businessDataVo.setLfFields(lfFields);
        lfFormDataRuntimeHelper.populateLfConditions(startConditionsVo, businessDataVo);
        lfFormDataRuntimeHelper.processFormRelatedUserConf(confId, extraFlags, businessDataVo);
        return ret;
    }
}
