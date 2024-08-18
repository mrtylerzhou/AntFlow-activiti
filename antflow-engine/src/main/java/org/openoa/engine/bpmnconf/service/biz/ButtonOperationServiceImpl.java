package org.openoa.engine.bpmnconf.service.biz;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.interf.ProcessOperationAdaptor;

import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.factory.IAdaptorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public BusinessDataVo buttonsOperationTransactional(BusinessDataVo vo) {

        //Do button operations
        ProcessOperationAdaptor processOperation = adaptorFactory.getProcessOperation(vo);
        processOperation.doProcessButton(vo);
        return vo;

    }
}
