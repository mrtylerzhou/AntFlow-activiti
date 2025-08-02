package org.openoa.engine.conf.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.util.ThreadLocalContainer;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.engine.bpmnconf.service.biz.ButtonOperationServiceImpl;
import org.openoa.engine.factory.FormFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import static org.openoa.base.constant.enums.ProcessOperationEnum.*;

/**
 * @Author TylerZhou
 * @Date 2024/7/7 9:19
 * @Version 1.0
 */
@Slf4j
@Aspect
@Component
public class DoButtonOperationAspect {
    @Autowired
    private FormFactory formFactory;
    @Autowired
    private ButtonOperationServiceImpl buttonOperationService;

    @Around("execution(* org.openoa.engine.factory.ButtonPreOperationService.buttonsPreOperation(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        if (args.length != 2) {
            throw new IllegalArgumentException("method must hava two parameters");
        }
        String params = (String) args[0];
        String formCode = (String) args[1];
        log.info("params:{},formCode:{}", params, formCode);
        //deserialize parameters that passed in
        BusinessDataVo vo = formFactory.dataFormConversion(params, formCode);
        //To determine the operation Type
        ProcessOperationEnum poEnum = ProcessOperationEnum.getEnumByCode(vo.getOperationType());
        if (ObjectUtils.isEmpty(poEnum)) {
            throw new AFBizException("unknown operation type,please Contact the Administrator");
        }
        formCode=vo.getFormCode();
        ThreadLocalContainer.set(StringConstants.FORM_CODE,formCode);
        //set the operation Flag
        if (poEnum.getCode().equals(BUTTON_TYPE_DIS_AGREE.getCode()) || poEnum.getCode().equals(BUTTON_TYPE_STOP.getCode())) {
            vo.setFlag(false);
        } else if (poEnum.getCode().equals(BUTTON_TYPE_ABANDON.getCode())) {
            vo.setFlag(true);
        }
        //set start user Info
        if (ObjectUtils.isEmpty(vo.getStartUserId())) {
            vo.setStartUserId(SecurityUtils.getLogInEmpIdStr());
            vo.setStartUserName(SecurityUtils.getLogInEmpName());
        }
       
        return buttonOperationService.buttonsOperationTransactional(vo);
    }
}
