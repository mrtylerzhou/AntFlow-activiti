package org.openoa.engine.bpmnconf.service.biz;


import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.constant.enums.NodeTypeEnum;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.exception.JiMuBizException;

import org.openoa.base.constant.enums.ProcessStateEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.entity.BpmProcessName;

import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.confentity.BpmnNode;
import org.openoa.engine.bpmnconf.confentity.BpmnNodePersonnelConf;
import org.openoa.engine.bpmnconf.confentity.BpmnNodePersonnelEmplConf;
import org.openoa.engine.bpmnconf.service.impl.BpmnNodePersonnelConfServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmnNodePersonnelEmplConfServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmnNodeServiceImpl;
import org.openoa.engine.factory.FormFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * process submit
 */
@Service
@Slf4j
public class SubmitProcessImpl implements ProcessOperationAdaptor {
    @Autowired
    private FormFactory formFactory;
    @Autowired
    private BpmnConfCommonServiceImpl bpmnConfCommonService;
    @Autowired
    protected BpmBusinessProcessServiceImpl bpmBusinessProcessService;

    @Autowired
    private BpmProcessNameServiceImpl bpmProcessNameService;
	@Autowired
	private BpmnNodeServiceImpl bpmnNodeService;
	@Autowired
	private BpmnNodePersonnelConfServiceImpl bpmnNodePersonnelConfService;
	@Autowired
	private BpmnNodePersonnelEmplConfServiceImpl bpmnNodePersonnelEmplConfService;

    @Override
    public void doProcessButton(BusinessDataVo businessDataVo) {
        log.info("Start submit process. param:{}", businessDataVo);
        FormOperationAdaptor formAdapter = formFactory.getFormAdaptor(businessDataVo);

        BusinessDataVo vo = businessDataVo;
        if(!Boolean.TRUE.equals(businessDataVo.getIsMigration())){
            vo=formAdapter.submitData(businessDataVo);
        }
        // call the process's launch method to get launch parameters
        BpmnStartConditionsVo bpmnStartConditionsVo = formAdapter.launchParameters(vo);
        bpmnStartConditionsVo.setBusinessDataVo(vo);
        bpmnStartConditionsVo.setApproversList(Optional.ofNullable(businessDataVo.getApproversList()).orElse(Maps.newHashMap()));
        String processNumber=businessDataVo.getFormCode() + "_" + vo.getBusinessId();
        if(Boolean.TRUE.equals(businessDataVo.getIsMigration())){
            processNumber=businessDataVo.getProcessNumber();
        }
        bpmnStartConditionsVo.setProcessNum(processNumber);
        bpmnStartConditionsVo.setEntryId(vo.getEntityName() + ":" + vo.getBusinessId());
        bpmnStartConditionsVo.setBusinessId(vo.getBusinessId());
        if(Boolean.TRUE.equals(businessDataVo.getIsMigration())){
            bpmnStartConditionsVo.setIsMigration(vo.getIsMigration());
        }else{
            String entryId = vo.getEntityName() + ":" + vo.getBusinessId();
            if (!bpmBusinessProcessService.checkProcessData(entryId)) {
                throw new JiMuBizException("the process has already been submitted！");
            }
        }

        //process's name
        String processName = Optional
                .ofNullable(bpmProcessNameService.getBpmProcessName(businessDataVo.getFormCode()))
                .orElse(new BpmProcessName()).getProcessName();
        //apply user info
        String applyName = SecurityUtils.getLogInEmpName();
        //save business and process information
        if(!Boolean.TRUE.equals(businessDataVo.getIsMigration())){
            bpmBusinessProcessService.addBusinessProcess(BpmBusinessProcess.builder()
                    .businessId(vo.getBusinessId())
                    .processinessKey(businessDataVo.getFormCode())
                    .businessNumber(processNumber)
                    .isLowCodeFlow(vo.getIsLowCodeFlow())
                    .createUser(businessDataVo.getStartUserId())
                    .userName(businessDataVo.getStartUserName())
                    .createTime(new Date())
                    .processDigest(vo.getProcessDigest())
                    .processState(ProcessStateEnum.HANDLING_STATE.getCode())
                    .entryId(vo.getEntityName() + ":" + vo.getBusinessId())
                    .description(applyName + "-" + processName)
                    .dataSourceId(vo.getDataSourceId())
                    .version(businessDataVo.getBpmnCode())
                    .build());
	        // 提前将自选抄送人存到t_bpmn_node_personnel_empl_conf表, 下面的bpmnConfCommonService.startProcess里的bpmnConfService.detail代码会从t_bpmn_node_personnel_empl_conf里获取人员id和姓名,在bpmnRemoveConfFormatFactory.removeBpmnConf里写入empToForwardList，然后保存到bpm_process_forward实现抄送功能
	        saveCcSelfFlag(businessDataVo.getApproversList(), applyName);
            //the process number is predictable
            businessDataVo.setProcessNumber(businessDataVo.getFormCode() + "_" + vo.getBusinessId());
        }
        bpmnConfCommonService.startProcess(businessDataVo.getBpmnCode(), bpmnStartConditionsVo);
    }

	/**
	 * 保存自选抄送人
	 * @param nodeIdApproversMap 用户自选人信息 key：node_id value：自选人信息
	 * @param userName 用户名
	 */
	private void saveCcSelfFlag(Map<String, List<BaseIdTranStruVo>> nodeIdApproversMap, String userName) {
		if (!CollectionUtils.isEmpty(nodeIdApproversMap)) {
			for (Map.Entry<String, List<BaseIdTranStruVo>> entry : nodeIdApproversMap.entrySet()) {
				String nodeId = entry.getKey();
				List<BaseIdTranStruVo> empIds = entry.getValue();
				BpmnNode ccSelfSelectNode = bpmnNodeService.lambdaQuery()
					.eq(BpmnNode::getNodeType, NodeTypeEnum.NODE_TYPE_COPY.getCode()).eq(BpmnNode::getId, nodeId).one();
				if (Objects.isNull(ccSelfSelectNode)) {
					continue;
				}
				Integer personnelConfId = Optional.ofNullable(
						bpmnNodePersonnelConfService.lambdaQuery().select(BpmnNodePersonnelConf::getId).eq(BpmnNodePersonnelConf::getBpmnNodeId, ccSelfSelectNode.getId()).one())
					.map(BpmnNodePersonnelConf::getId).orElse(null);
				if (Objects.nonNull(personnelConfId) && !CollectionUtils.isEmpty(empIds)) {
					List<BpmnNodePersonnelEmplConf> pceEntityList = new ArrayList<>();
					empIds.forEach(empId -> {
						BpmnNodePersonnelEmplConf bpmnNodePersonnelEmplConf = new BpmnNodePersonnelEmplConf();
						bpmnNodePersonnelEmplConf.setBpmnNodePersonneId(personnelConfId);
						bpmnNodePersonnelEmplConf.setEmplId(empId.getId());
						bpmnNodePersonnelEmplConf.setEmplName(empId.getName());
						bpmnNodePersonnelEmplConf.setIsDel(0);
						bpmnNodePersonnelEmplConf.setCreateUser(userName);
						bpmnNodePersonnelEmplConf.setUpdateUser(userName);
						pceEntityList.add(bpmnNodePersonnelEmplConf);
					});
					bpmnNodePersonnelEmplConfService.saveBatch(pceEntityList);
				}
			}
		}
	}

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(ProcessOperationEnum.BUTTON_TYPE_SUBMIT);
    }
}
