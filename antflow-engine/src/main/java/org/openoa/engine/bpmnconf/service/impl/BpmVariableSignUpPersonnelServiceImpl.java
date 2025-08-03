package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.BpmVariableSignUpPersonnel;
import org.openoa.base.service.BpmVariableSignUpPersonnelService;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.engine.bpmnconf.mapper.BpmVariableSignUpPersonnelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.openoa.common.constant.enus.ElementPropertyEnum.ELEMENT_PROPERTY_SIGN_UP_SERIAL;

@Service
public class BpmVariableSignUpPersonnelServiceImpl extends ServiceImpl<BpmVariableSignUpPersonnelMapper, BpmVariableSignUpPersonnel> implements BpmVariableSignUpPersonnelService {



    @Override
    public List<BaseIdTranStruVo> getSignUpInfoByVariableAndElementId(Long variableId,String elementId){
        List<BaseIdTranStruVo> byVariableIdAndElementId = this.getBaseMapper().getByVariableIdAndElementId(variableId, elementId);
        return byVariableIdAndElementId;
    }

    @Override
    public Map<String, String> getByProcessNumAndElementId(String processNumber, String elementId) {
        List<BaseIdTranStruVo> assigneeList = this.getBaseMapper().getByProcessNumAndElementId(processNumber, elementId);
        Map<String, String> assigneeMap = assigneeList.stream().collect(Collectors.toMap(BaseIdTranStruVo::getId, BaseIdTranStruVo::getName, (k1, k2) -> k1));
        return assigneeMap;
    }
}
