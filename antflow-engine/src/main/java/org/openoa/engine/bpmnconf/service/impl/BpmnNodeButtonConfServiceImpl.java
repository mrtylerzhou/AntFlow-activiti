package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import org.openoa.base.constant.enums.ButtonPageTypeEnum;
import org.openoa.base.constant.enums.ButtonTypeEnum;
import org.openoa.base.entity.BpmnNodeButtonConf;
import org.openoa.base.util.MultiTenantUtil;
import org.openoa.base.vo.BpmnNodeButtonConfBaseVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.engine.bpmnconf.mapper.BpmnNodeButtonConfMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

import static org.openoa.base.constant.enums.ButtonPageTypeEnum.AUDIT;
import static org.openoa.base.constant.enums.ButtonPageTypeEnum.INITIATE;
import static org.openoa.base.constant.enums.ButtonTypeEnum.BUTTON_TYPE_RESUBMIT;
import static org.openoa.base.constant.enums.NodeTypeEnum.NODE_TYPE_START;

@Repository
public class BpmnNodeButtonConfServiceImpl extends ServiceImpl<BpmnNodeButtonConfMapper, BpmnNodeButtonConf> {


    public void editButtons(BpmnNodeVo bpmnNodeVo, Long bpmnNodeId) {
        //delete the old configs
        this.getBaseMapper().delete(new QueryWrapper<BpmnNodeButtonConf>()
                .eq("bpmn_node_id", bpmnNodeId));


        //to check whether the start node is started by resubmitting the process
        boolean isHaveCxtjButton = false;

        BpmnNodeButtonConfBaseVo buttons = bpmnNodeVo.getButtons();
        if (ObjectUtils.isEmpty(buttons)) {
            BpmnNodeButtonConfBaseVo buttonConfBaseVo=new BpmnNodeButtonConfBaseVo();
            buttonConfBaseVo.setStartPage(Lists.newArrayList());
            buttonConfBaseVo.setApprovalPage(Lists.newArrayList(2));
            buttons=buttonConfBaseVo;
            //return; todo for easy testing purposes
        }

        //start page buttons
        List<Integer> startPageButtons = buttons.getStartPage();
        if (!ObjectUtils.isEmpty(startPageButtons)) {
            this.saveBatch(getBpmnNodeButtonConfs(bpmnNodeId, buttons.getStartPage(), INITIATE));
        }

        //approval page
        List<Integer> approvalPageButtons = buttons.getApprovalPage();
        if (!ObjectUtils.isEmpty(approvalPageButtons)) {
            this.saveBatch(getBpmnNodeButtonConfs(bpmnNodeId, buttons.getApprovalPage(), AUDIT));
            //check whether the approval page buttons contains the resubmit button, if yes, set isHaveCxtjButton to true
            if (buttons.getApprovalPage().contains(BUTTON_TYPE_RESUBMIT.getCode())) {
                isHaveCxtjButton = true;
            }
        }

        //if the initiator node and the approval page buttons do not contain the resubmit button, then configure the default resubmit button
        if (bpmnNodeVo.getNodeType().equals(NODE_TYPE_START.getCode()) && !isHaveCxtjButton) {


            //resubmit button on the approval page
            this.getBaseMapper().insert(BpmnNodeButtonConf
                    .builder()
                    .bpmnNodeId(bpmnNodeId)
                    .buttonPageType(AUDIT.getCode())
                    .buttonType(BUTTON_TYPE_RESUBMIT.getCode())
                    .buttonName(ButtonTypeEnum.getDescByCode(BUTTON_TYPE_RESUBMIT.getCode()))
                    .tenantId(MultiTenantUtil.getCurrentTenantId())
                    .build());
        }
    }

    private List<BpmnNodeButtonConf> getBpmnNodeButtonConfs(Long bpmnNodeId, List<Integer> buttons, ButtonPageTypeEnum buttonPageTypeEnum) {
        return buttons
                .stream()
                .distinct()
                .map(o -> BpmnNodeButtonConf
                        .builder()
                        .bpmnNodeId(bpmnNodeId)
                        .buttonPageType(buttonPageTypeEnum.getCode())
                        .buttonType(o)
                        .buttonName(ButtonTypeEnum.getDescByCode(o))
                        .tenantId(MultiTenantUtil.getCurrentTenantId())
                        .build())
                .collect(Collectors.toList());
    }
}
