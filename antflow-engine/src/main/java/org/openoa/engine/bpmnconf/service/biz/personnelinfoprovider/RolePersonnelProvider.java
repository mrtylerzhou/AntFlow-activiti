package org.openoa.engine.bpmnconf.service.biz.personnelinfoprovider;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.entity.Result;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.service.empinfoprovider.BpmnRoleInfoProvider;
import org.openoa.base.util.ThreadLocalContainer;
import org.openoa.base.vo.*;
import org.openoa.common.util.AssigneeVoBuildUtils;
import org.openoa.engine.bpmnconf.service.impl.OutSideBpmApproveTemplateServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author TylerZhou
 * @Date 2024/7/18 6:42
 * @Version 1.0
 */
@Slf4j
@Component
public class RolePersonnelProvider extends AbstractMissingAssignNodeAssigneeVoProvider{
    @Autowired
    private BpmnRoleInfoProvider roleInfoProvider;

    @Autowired
    private AssigneeVoBuildUtils assigneeVoBuildUtils;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OutSideBpmApproveTemplateServiceImpl  outSideBpmApproveTemplateService;


    @Override
    public List<BpmnNodeParamsAssigneeVo> getAssigneeList(BpmnNodeVo bpmnNodeVo, BpmnStartConditionsVo startConditionsVo) {
        BpmnNodePropertysVo propertysVo = bpmnNodeVo.getProperty();
        if (propertysVo==null || CollectionUtils.isEmpty(propertysVo.getRoleIds())) {
            throw new AFBizException("指定角色找人条件不全，无法找人！");
        }
        if(bpmnNodeVo.getIsOutSideProcess()!=null&&bpmnNodeVo.getIsOutSideProcess().equals(1)){
            // 发起人
            if("Gb2".equals(bpmnNodeVo.getNodeId())){
                Object currentuser = ThreadLocalContainer.get("currentuser");
                if (currentuser!=null){
                    BaseIdTranStruVo user = (BaseIdTranStruVo)currentuser;
                    return assigneeVoBuildUtils.buildVOs(Lists.newArrayList(user), bpmnNodeVo.getNodeName(), false);
                }
            }
           // outSideBpmApproveTemplateService.getBaseMapper().select;
            String roelApiUrl = outSideBpmApproveTemplateService.getRoelApiUrlByConfId(bpmnNodeVo.getConfId());
            if(roelApiUrl==null){
                throw new AFBizException("can not find specified out side process template user info url  via confId:"+bpmnNodeVo.getConfId());
            }
            ParameterizedTypeReference<Result<List<BaseIdTranStruVo>>> typeRef =
                    new ParameterizedTypeReference<Result<List<BaseIdTranStruVo>>>() {};
            ResponseEntity<Result<List<BaseIdTranStruVo>>> responseEntity = restTemplate.exchange(
                    roelApiUrl + "/" + bpmnNodeVo.getProperty().getRoleIds().get(0),
                    HttpMethod.GET,
                    null,
                    typeRef
            );
            Result<List<BaseIdTranStruVo>> result = responseEntity.getBody();
            if (result == null || !result.isSuccess()) {
                throw new AFBizException("can not find specified out side process template user info url via confId:"+bpmnNodeVo.getConfId());
            }

            List<BaseIdTranStruVo> userList = result.getData();
            if (CollectionUtils.isEmpty(userList)) {
                throw new AFBizException("can not find specified out side process template user info url via confId:"+bpmnNodeVo.getConfId());
            }

            return assigneeVoBuildUtils.buildVOs(userList, bpmnNodeVo.getNodeName(), false);
        }
        List<String> roleIds = propertysVo.getRoleIds();
        Map<String, String> roleEmployeeInfo = roleInfoProvider.provideRoleEmployeeInfo(roleIds);
        List<BaseIdTranStruVo> baseIdTranStruVoList = CollectionUtils.isEmpty(roleEmployeeInfo) ? new ArrayList<>() :
                roleEmployeeInfo.entrySet().stream().map(a -> BaseIdTranStruVo.builder().id(a.getKey()).name(a.getValue()).build()).collect(Collectors.toList());

        return  super.provideAssigneeList(bpmnNodeVo,baseIdTranStruVoList);
    }
}
