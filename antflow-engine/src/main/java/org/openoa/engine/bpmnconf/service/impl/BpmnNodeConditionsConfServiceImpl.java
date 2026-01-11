package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.entity.BpmnNodeConditionsConf;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.mapper.BpmnNodeConditionsConfMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeConditionsConfService;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BpmnNodeConditionsConfServiceImpl extends ServiceImpl<BpmnNodeConditionsConfMapper, BpmnNodeConditionsConf> implements BpmnNodeConditionsConfService {

    @Override
    public List<String> queryConditionParamNameByProcessNumber(BusinessDataVo businessDataVo) {
        String processNumber=businessDataVo.getProcessNumber();//流程查看页预览真使用流程编号
        List<String> result=null;
        if(!StringUtils.isEmpty(processNumber)){
           result= baseMapper.queryConditionParamNameByProcessNumber(processNumber);
        }else{//流程发起和在发起页预览时还没有流程编号，使用表单编号查询
            result=baseMapper.queryConditionParamByFormCode(businessDataVo.getFormCode());
        }
        return result;
    }
}
