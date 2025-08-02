package org.openoa.engine.bpmnconf.service.tagparser;

import org.openoa.base.exception.AFBizException;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.engine.factory.TagParser;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.constant.enums.ProcessOperationEnum;

import java.util.Collection;

/**
 * @Author TylerZhou
 * @Date 2024/7/7 8:05
 * @Version 1.0
 */
public class FormOperationTagParser implements TagParser<ProcessOperationAdaptor,BusinessDataVo> {

    @Override
    public ProcessOperationAdaptor parseTag(BusinessDataVo data) {
        if(data==null){
            throw new AFBizException("provided data to find a processing method is null");
        }
        Integer operationType = data.getOperationType();
        Boolean isOutSideAccessProc = data.getIsOutSideAccessProc();
        if(operationType==null){
            throw new AFBizException("provided data has no property of operationType!");
        }
        ProcessOperationEnum poEnum = ProcessOperationEnum.getEnumByCode(operationType);
        if(poEnum==null){
            throw new AFBizException("can not find a processing method by providing data with your given operationType of"+operationType);
        }
        Collection<ProcessOperationAdaptor> processOperationAdaptors = SpringBeanUtils.getBeans(ProcessOperationAdaptor.class);
        for (ProcessOperationAdaptor processOperationAdaptor : processOperationAdaptors) {

            if(!isOutSideAccessProc){
                if(processOperationAdaptor.isSupportBusinessObject(poEnum)){
                    return processOperationAdaptor;
                }
            }else{
                Integer outSideType = data.getOutSideType();
                String outSideMarker=outSideType==0?ProcessOperationEnum.getOutSideMarker():ProcessOperationEnum.getOutSideAccessmarker();
                if(processOperationAdaptor.isSupportBusinessObject(outSideMarker,poEnum)){
                    return processOperationAdaptor;
                }
            }
        }
        return null;
    }
}
