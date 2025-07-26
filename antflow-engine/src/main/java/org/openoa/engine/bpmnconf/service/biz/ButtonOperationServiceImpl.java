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
            if (vo.getIsOutSideAccessProc()) {

                String verifyUserName = SecurityUtils.getLogInEmpNameSafe();

                String verifyUserId = SecurityUtils.getLogInEmpIdSafe();
//                Map<String, Object> objectMap = vo.getObjectMap();
//                if (!CollectionUtils.isEmpty(objectMap)) {
//                    verifyUserName = Optional.ofNullable(objectMap.get("employeeName")).map(String::valueOf).orElse(StringUtils.EMPTY);
//                    verifyUserId = Optional.ofNullable(objectMap.get("employeeId")).map(Object::toString).orElse("");
//                }
                ProcessorFactory.executePostProcessors(vo);
                ProcessOperationEnum poEnum = ProcessOperationEnum.getEnumByCode(vo.getOperationType());
                switch (Objects.requireNonNull(poEnum)){
                    case BUTTON_TYPE_SUBMIT:

                    case BUTTON_TYPE_AGREE:

                        break;
                    case BUTTON_TYPE_ABANDON:

                        break;
                }
            }

        } catch (Exception e){
            throw e;
        }
        return vo;

    }
}
