package org.openoa.engine.bpmnconf.service.biz;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.base.interf.ProcessOperationAdaptor;

import org.openoa.base.service.ProcessorFactory;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.factory.IAdaptorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static org.openoa.base.constant.enums.CallbackTypeEnum.*;

/**
 *@Author JimuOffice
 * @Description button operation servicve
 * @Param
 * @return
 * @Version 0.5
 */
@Service
@Slf4j
public class ButtonOperationServiceImpl{
    @Autowired
    private IAdaptorFactory adaptorFactory;

    @Transactional(rollbackFor = Exception.class)
    public BusinessDataVo buttonsOperationTransactional(BusinessDataVo vo) {

        //Do button operations
        ProcessOperationAdaptor processOperation = adaptorFactory.getProcessOperation(vo);
        try {
            processOperation.doProcessButton(vo);
            ProcessorFactory.executePostProcessors(vo);
        } catch (Exception e){
            log.error("流程执行出错啦!",e);
            throw e;
        }
        return vo;

    }
}
