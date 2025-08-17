package org.openoa.engine.bpmnconf.service.biz.personnelinfoprovider;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.exception.BusinessErrorEnum;
import org.openoa.base.vo.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
//这里只是demo,用户可以重写这个类,如果是以jar包形式引入的,自定义一个service,继承自此类,并且名字为udrPersonnelProvider1,重写getAssigneeList方法
@Slf4j
@Service("udrPersonnelProvider")
public class UDRPersonnelProvider extends AbstractMissingAssignNodeAssigneeVoProvider{

    @Override
    public List<BpmnNodeParamsAssigneeVo> getAssigneeList(BpmnNodeVo bpmnNodeVo, BpmnStartConditionsVo startConditionsVo) {
        Long id = bpmnNodeVo.getId();
        List<BaseIdTranStruVo> assignees=new ArrayList<>();
        BusinessDataVo businessDataVo = startConditionsVo.getBusinessDataVo();
        String udrAssigneeProperty = bpmnNodeVo.getProperty().getUdrAssigneeProperty();
        if(StringUtils.isEmpty(udrAssigneeProperty)){
            throw new AFBizException(BusinessErrorEnum.PARAMS_NOT_COMPLETE.getCodeStr(),"udrAssigneeProperty missing");
        }
        if(udrAssigneeProperty.equalsIgnoreCase("zdysp1")){
            assignees= Lists.newArrayList(BaseIdTranStruVo.builder().id("1").name("张三").build());
        }else if(udrAssigneeProperty.equalsIgnoreCase("zdysp2")){
            assignees= Lists.newArrayList(BaseIdTranStruVo.builder().id("1").name("张三").build());
        }else{//可以继续增加逻辑
            assignees= Lists.newArrayList(BaseIdTranStruVo.builder().id("1").name("张三").build());
        }
        return  super.provideAssigneeList(bpmnNodeVo,assignees);
    }
}
