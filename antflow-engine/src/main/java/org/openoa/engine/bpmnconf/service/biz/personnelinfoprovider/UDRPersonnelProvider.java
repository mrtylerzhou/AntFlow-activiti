package org.openoa.engine.bpmnconf.service.biz.personnelinfoprovider;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
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
        String startUserId = startConditionsVo.getStartUserId();
        //这个是自定义里面的类型,比如zdysp1代表指定人员
        BaseIdTranStruVo udrAssigneeProperty = bpmnNodeVo.getProperty().getUdrAssigneeProperty();
        //udrValueJson值不是必须的,比如自定义的规则是发起人的领导,发起人是流程发起以后才确定的,不能在设计时就确定,就可以不存值,从startConditionsVo.getStartUserId()里面取
        String udrValueJson = bpmnNodeVo.getProperty().getUdrValueJson();
        if(udrAssigneeProperty==null){
            throw new AFBizException(BusinessErrorEnum.PARAMS_NOT_COMPLETE.getCodeStr(),"udrAssigneeProperty missing");
        }
        if(udrAssigneeProperty.getId().equalsIgnoreCase("zdysp1")){
            //比如zdysp1代表指定人员,udrValueJson里面存的是用户的id,值是用户自己存的,用户知道它是什么类型,将其反序列化,比如存的是用户的id
            //则反序列化为List<String>,然后提供一个方法根据保存的指定人员的id集合查找人员
            assignees= Lists.newArrayList(BaseIdTranStruVo.builder().id("1").name("张三").build());
        }else if(udrAssigneeProperty.getId().equalsIgnoreCase("zdysp2")){
            assignees= Lists.newArrayList(BaseIdTranStruVo.builder().id("1").name("张三").build());
        }else{//可以继续增加逻辑
            assignees= Lists.newArrayList(BaseIdTranStruVo.builder().id("1").name("张三").build());
        }
        return  super.provideAssigneeList(bpmnNodeVo,assignees);
    }
}
