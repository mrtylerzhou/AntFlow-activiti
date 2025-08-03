package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.entity.BpmVariable;
import org.openoa.base.entity.BpmVariableSignUpPersonnel;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.common.entity.BpmVariableMultiplayer;
import org.openoa.common.entity.BpmVariableMultiplayerPersonnel;
import org.openoa.common.entity.BpmVariableSingle;
import org.openoa.common.service.BpmVariableMultiplayerPersonnelServiceImpl;
import org.openoa.common.service.BpmVariableMultiplayerServiceImpl;
import org.openoa.common.service.BpmVariableSingleServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmVariableSignUpPersonnelServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmVariableBizService;
import org.openoa.engine.utils.AFWrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BpmVariableBizServiceImpl implements BpmVariableBizService {
    @Autowired
    private BpmVariableSingleServiceImpl bpmVariableSingleService;

    @Autowired
    private BpmVariableMultiplayerServiceImpl bpmVariableMultiplayerService;

    @Autowired
    private BpmVariableMultiplayerPersonnelServiceImpl bpmVariableMultiplayerPersonnelService;

    @Autowired
    private BpmVariableSignUpPersonnelServiceImpl bpmVariableSignUpPersonnelService;

    /**
     * to check whether login employee is in process(whether he/she is related to a specified process)
     *
     * @param formCode
     * @param businessId
     * @param loginEmplId
     * @return
     */
    @Override
    public Boolean checkIsInProcess(String formCode, Integer businessId, Integer loginEmplId, String loginUsername) {

        if (!Strings.isNullOrEmpty(formCode) || ObjectUtils.isEmpty(businessId)
                || ObjectUtils.isEmpty(loginEmplId) || ObjectUtils.isEmpty(loginUsername)) {
            return false;
        }

        String processNum = StringUtils.join(formCode, "_", businessId);

        BpmVariable bpmVariable = this.getMapper().selectOne(new QueryWrapper<BpmVariable>().eq("process_num", processNum));

        if (ObjectUtils.isEmpty(bpmVariable)) {
            return false;
        }

        Long variableId = bpmVariable.getId();

        List<String> assignees = Lists.newArrayList();


        //query to check whether single variable has value, if yes, query and set Map
        if (bpmVariableSingleService.getBaseMapper().selectCount(new QueryWrapper<BpmVariableSingle>()
                .eq("variable_id", variableId)) > 0) {
            for (BpmVariableSingle bpmVariableSingle : bpmVariableSingleService.getBaseMapper().selectList(new QueryWrapper<BpmVariableSingle>()
                    .eq("variable_id", variableId))) {
                assignees.add(bpmVariableSingle.getAssignee());
            }
        }


        //query to check whether multiplayer variable has value, if yes, query and set Map
        if (bpmVariableMultiplayerService.getBaseMapper().selectCount(new QueryWrapper<BpmVariableMultiplayer>()
                .eq("variable_id", variableId)) > 0) {
            List<BpmVariableMultiplayer> bpmVariableMultiplayers = bpmVariableMultiplayerService.getBaseMapper().selectList(new QueryWrapper<BpmVariableMultiplayer>()
                    .eq("variable_id", variableId));
            for (BpmVariableMultiplayer bpmVariableMultiplayer : bpmVariableMultiplayers) {
                List<BpmVariableMultiplayerPersonnel> bpmVariableMultiplayerPersonnels = bpmVariableMultiplayerPersonnelService.getBaseMapper().selectList(new QueryWrapper<BpmVariableMultiplayerPersonnel>()
                        .eq("variable_multiplayer_id", bpmVariableMultiplayer.getId()));
                if (!ObjectUtils.isEmpty(bpmVariableMultiplayerPersonnels)) {
                    for (BpmVariableMultiplayerPersonnel bpmVariableMultiplayerPersonnel : bpmVariableMultiplayerPersonnels) {
                        assignees.add(bpmVariableMultiplayerPersonnel.getAssignee());
                    }
                }
            }
        }


        //query to check whether signUp variable has value, if yes, query and set Map
        if (bpmVariableSignUpPersonnelService.getBaseMapper().selectCount(new QueryWrapper<BpmVariableSignUpPersonnel>()
                .eq("variable_id", variableId)) > 0) {
            Multimap<String, String> listMultimap = ArrayListMultimap.create();
            for (BpmVariableSignUpPersonnel bpmVariableSignUpPersonnel : bpmVariableSignUpPersonnelService.getBaseMapper().selectList(new QueryWrapper<BpmVariableSignUpPersonnel>()
                    .eq("variable_id", variableId))) {
                listMultimap.put(bpmVariableSignUpPersonnel.getElementId(), bpmVariableSignUpPersonnel.getAssignee());
            }
            for (String key : listMultimap.keySet()) {
                List<String> signUpAssignees = listMultimap.get(key).stream().collect(Collectors.toList());
                assignees.addAll(signUpAssignees);
            }
        }

        if (assignees.contains(loginEmplId.toString()) || assignees.contains(loginUsername)) {
            return true;
        }

        return false;
    }

    @Override
    public List<BaseIdTranStruVo> getApproversByNodeId(String processNumber, String nodeId){
        List<BaseIdTranStruVo> currentNodeAssignees = bpmVariableMultiplayerService.getBaseMapper().getAssigneeByNodeId(processNumber, nodeId);
        return currentNodeAssignees;
    }
    /**
     * 根据流程节点ID查询当前节点所有审批人
     * @param elementId 流程节点ID
     * @return 审批人列表
     */
    @Override
    public List<BaseIdTranStruVo> getApproversByElementId(String processNumber, String elementId) {
        List<BaseIdTranStruVo> currentNodeAssignees = bpmVariableMultiplayerService.getBaseMapper().getAssigneeByElementId(processNumber, elementId);
        return currentNodeAssignees;
    }



    @Override
    public void deleteByProcessNumber(String processNumber){
        LambdaQueryWrapper<BpmVariable> variableQry = AFWrappers.<BpmVariable>lambdaTenantQuery()
                .eq(BpmVariable::getProcessNum, processNumber);
        BpmVariable bpmVariable = this.getMapper().selectOne(variableQry);
        Long variableId = bpmVariable.getId();
        this.getMapper().deleteById(variableId);

        // 删除t_bpm_variable_single表数据
        LambdaQueryWrapper<BpmVariableSingle> singleQry = AFWrappers.<BpmVariableSingle>lambdaTenantQuery()
                .eq(BpmVariableSingle::getVariableId, variableId);
        bpmVariableSingleService.getBaseMapper().delete(singleQry);

        // 删除t_bpm_variable_multiplayer表数据
        LambdaQueryWrapper<BpmVariableMultiplayer> multiplayerQry = AFWrappers.<BpmVariableMultiplayer>lambdaTenantQuery()
                .eq(BpmVariableMultiplayer::getVariableId, variableId);
        bpmVariableMultiplayerService.getBaseMapper().delete(multiplayerQry);
    }
}
