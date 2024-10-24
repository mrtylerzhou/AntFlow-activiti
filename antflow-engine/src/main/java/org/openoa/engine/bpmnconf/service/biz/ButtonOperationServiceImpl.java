package org.openoa.engine.bpmnconf.service.biz;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.interf.ProcessOperationAdaptor;

import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.factory.IAdaptorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
    @Autowired
    private ThirdPartyCallBackServiceImpl thirdPartyCallBackService;
    @Transactional
    public BusinessDataVo buttonsOperationTransactional(BusinessDataVo vo) {

        //Do button operations
        ProcessOperationAdaptor processOperation = adaptorFactory.getProcessOperation(vo);
        try {
            processOperation.doProcessButton(vo);
            if (vo.getIsOutSideAccessProc()) {

                String verifyUserName = StringUtils.EMPTY;

                String verifyUserId = StringUtils.EMPTY;
                Map<String, Object> objectMap = vo.getObjectMap();
                if (!CollectionUtils.isEmpty(objectMap)) {
                    verifyUserName = Optional.ofNullable(objectMap.get("employeeName")).map(String::valueOf).orElse(StringUtils.EMPTY);
                    verifyUserId = Optional.ofNullable(objectMap.get("employeeId")).map(Object::toString).orElse("");
                }
                ProcessOperationEnum poEnum = ProcessOperationEnum.getEnumByCode(vo.getOperationType());
                switch (Objects.requireNonNull(poEnum)){
                    case BUTTON_TYPE_SUBMIT:
                        thirdPartyCallBackService.doCallback(vo.getBpmFlowCallbackUrl(), PROC_STARTED_CALL_BACK, vo.getBpmnConfVo(),
                                vo.getProcessNumber(), vo.getBusinessId(),verifyUserName);
                    case BUTTON_TYPE_AGREE:
                        thirdPartyCallBackService.doCallback(vo.getBpmFlowCallbackUrl(), PROC_COMMIT_CALL_BACK, vo.getBpmnConfVo(),
                                vo.getProcessNumber(), vo.getBusinessId(),verifyUserName);
                        break;
                    case BUTTON_TYPE_ABANDON:
                        thirdPartyCallBackService.doCallback(vo.getBpmFlowCallbackUrl(), PROC_END_CALL_BACK, vo.getBpmnConfVo(),
                                vo.getProcessNumber(), vo.getBusinessId(),verifyUserName);
                        break;
                }
            }

        } catch (Exception e){
            throw e;
        }
        return vo;

    }
}
