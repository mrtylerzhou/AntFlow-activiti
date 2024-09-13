package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.constant.enums.ProcesTypeEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.service.empinfoprovider.BpmnEmployeeInfoProviderService;
import org.openoa.base.util.AntCollectionUtil;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BpmVerifyInfoVo;
import org.openoa.engine.bpmnconf.common.ProcessContans;
import org.openoa.engine.bpmnconf.confentity.BpmBusiness;
import org.openoa.engine.bpmnconf.confentity.BpmFlowrunEntrust;
import org.openoa.engine.bpmnconf.confentity.BpmVerifyInfo;
import org.openoa.engine.bpmnconf.mapper.BpmVerifyInfoMapper;
import org.openoa.engine.bpmnconf.mapper.EmployeeMapper;
import org.openoa.engine.bpmnconf.service.biz.BpmBusinessProcessServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * //todo 员工信息
 */
@Service
public class BpmVerifyInfoServiceImpl extends ServiceImpl<BpmVerifyInfoMapper, BpmVerifyInfo> {


    @Autowired
    private TaskService taskService;
    @Autowired
    private BpmBusinessProcessServiceImpl processService;
    @Autowired
    private ProcessContans processContans;
    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private BpmFlowrunEntrustServiceImpl bpmFlowrunEntrustService;

    @Autowired
    protected BpmBusinessProcessServiceImpl bpmBusinessProcessService;
    @Autowired
    private BpmnEmployeeInfoProviderService bpmnEmployeeInfoProviderService;;

    public void addVerifyInfo(String businessId, String remark, Integer businessType, String taskName, Integer verifyStatuss) {
        BpmVerifyInfo verifyInfo = new BpmVerifyInfo();
        verifyInfo.setBusinessId(businessId);
        verifyInfo.setId(null);
        verifyInfo.setBusinessType(businessType);
        verifyInfo.setVerifyDate(new Date());
        verifyInfo.setVerifyUserName(SecurityUtils.getLogInEmpName());
        verifyInfo.setTaskName(taskName);
        verifyInfo.setVerifyUserId(SecurityUtils.getLogInEmpIdStr());
        verifyInfo.setVerifyDesc(Strings.isNullOrEmpty(remark) ? "同意" : remark);
        verifyInfo.setVerifyStatus(verifyStatuss);
        this.getBaseMapper().insert(verifyInfo);
    }

    public void addVerifyInfo(BpmVerifyInfo verifyInfo) {
        BpmFlowrunEntrust entrustByTaskId = bpmFlowrunEntrustService.getBaseMapper().getEntrustByTaskId(SecurityUtils.getLogInEmpIdStr(), verifyInfo.getRunInfoId(), verifyInfo.getTaskId());
        if(entrustByTaskId!=null){
            verifyInfo.setOriginalId(entrustByTaskId.getOriginal());
        }
        this.getBaseMapper().insert(verifyInfo);
    }

    /***
     * add verify info
     * @param businessId business id
     * @param taskId    task id
     * @param remark     verify desc
     * @param businessType process business type
     */
    public void addVerifyInfo(String businessId, String taskId, String remark, Integer businessType, Integer status) {
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();

        BpmVerifyInfo verifyInfo = new BpmVerifyInfo();
        verifyInfo.setBusinessId(businessId);
        verifyInfo.setId(null);
        verifyInfo.setBusinessType(businessType);
        verifyInfo.setVerifyDate(new Date());
        verifyInfo.setTaskName(task == null ? "" : task.getName());
        verifyInfo.setVerifyUserName(SecurityUtils.getLogInEmpName());
        verifyInfo.setVerifyUserId(SecurityUtils.getLogInEmpIdStr());
        verifyInfo.setVerifyDesc(Strings.isNullOrEmpty(remark) ? "同意" : remark);
        verifyInfo.setVerifyStatus(status);
        this.getBaseMapper().insert(verifyInfo);
    }



    /**
     * get process verify info list
     *
     * @param bpmBusinessProcess
     * @return
     */
    public List<BpmVerifyInfoVo> verifyInfoList(BpmBusinessProcess bpmBusinessProcess) {
        List<BpmVerifyInfoVo> bpmVerifyInfoVo = Optional.ofNullable(this.findTaskInfo(bpmBusinessProcess)).orElse(Arrays.asList());
        List<BpmVerifyInfoVo> list = getBpmVerifyInfoVoList(Optional.ofNullable(getBaseMapper().getVerifyInfo(
                BpmVerifyInfoVo.builder()
                        .processCode(bpmBusinessProcess.getBusinessNumber())
                        .build()
        )).orElse(Arrays.asList()));
        if (!ObjectUtils.isEmpty(bpmVerifyInfoVo)) {
            bpmVerifyInfoVo.addAll(list);
            return bpmVerifyInfoVo;
        }
        return list;
    }


    /**
     * get process verify info list(not include entrust flows)
     *
     * @param processNumber
     * @return
     */
    public List<BpmVerifyInfoVo> verifyInfoList(String processNumber) {
       return verifyInfoList(processNumber,null);
    }
    /**
     * get process verify info list(not include entrust flows)
     *
     * @param processNumber
     * @return
     */
    public List<BpmVerifyInfoVo> verifyInfoList(String processNumber,String procInstId) {
        return getBpmVerifyInfoVoList(Optional.ofNullable(this.getBaseMapper().getVerifyInfo(
                BpmVerifyInfoVo.builder()
                        .processCode(processNumber)
                        .build()
        )).orElse(Arrays.asList()),procInstId);
    }

    /**
     * get verifyinfo by business id
     *
     * @return
     */
    public List<BpmVerifyInfoVo> findVerifyInfo(BpmBusinessProcess bpmBusinessProcess) {
        Integer business_type = ProcesTypeEnum.getCodeByDesc(bpmBusinessProcess.getBusinessNumber().split("\\_")[0].toString());
        List<BpmVerifyInfoVo> bpmVerifyInfoVo = this.findTaskInfo(bpmBusinessProcess);
        List<BpmVerifyInfoVo> list = getBpmVerifyInfoVoList(this.getBaseMapper().getVerifyInfo(
                BpmVerifyInfoVo.builder()
                        .businessId(bpmBusinessProcess.getBusinessId().toString())
                        .businessType(business_type)
                        .build()
        ));
        if (!ObjectUtils.isEmpty(bpmVerifyInfoVo)) {
            bpmVerifyInfoVo.addAll(list);
            return bpmVerifyInfoVo;
        } else {
            return list;
        }
    }

    /**
     * map verify info
     */
    public List<BpmVerifyInfoVo> getBpmVerifyInfoVoList(List<BpmVerifyInfoVo> list) {
      return getBpmVerifyInfoVoList(list,null);
    }
    /**
     * map verify info
     */
    public List<BpmVerifyInfoVo> getBpmVerifyInfoVoList(List<BpmVerifyInfoVo> list,String procInstId) {
        List<BpmVerifyInfoVo> infoVoList = new ArrayList<>();
        infoVoList.addAll(list.stream()
                .map(o -> {
                    if (!ObjectUtils.isEmpty(o.getOriginalId())) {
                        if(!StringUtils.isEmpty(procInstId)){
                            List<BpmFlowrunEntrust> bpmFlowrunEntrusts = bpmFlowrunEntrustService.list(
                                    new QueryWrapper<BpmFlowrunEntrust>()
                                            .eq("original", o.getOriginalId())
                                            .eq("runinfoid", procInstId)

                            );
                            if(!CollectionUtils.isEmpty(bpmFlowrunEntrusts)){
                                o.setOriginalName(bpmFlowrunEntrusts.get(0).getOriginalName());
                                o.setVerifyUserName(o.getVerifyUserName() + " 代 " +o.getOriginalName()  + " 审批");
                            }
                        }else{
                            Map<String, String> stringStringMap = bpmnEmployeeInfoProviderService.provideEmployeeInfo(Lists.newArrayList(o.getOriginalId()));
                            o.setOriginalName(stringStringMap.get(o.getOriginalId()));
                            o.setVerifyUserName(o.getVerifyUserName() + " 代 " +o.getOriginalName()  + " 审批");
                        }
                    }
                    return o;
                }).collect(Collectors.toList()));
        return infoVoList;
    }
    /**
     * get to do task info
     *
     * @param bpmBusinessProcess
     * @return
     */
    public List<BpmVerifyInfoVo> findTaskInfo(BpmBusinessProcess bpmBusinessProcess) {
        String procInstId = bpmBusinessProcess.getProcInstId();
        List<BpmVerifyInfoVo> tasks = Optional.ofNullable(this.getBaseMapper().findTaskInfor(procInstId)).orElse(Collections.emptyList());
        if (ObjectUtils.isEmpty(tasks)) {
            return Lists.newArrayList();
        }

        List<String> verifyUserIds = tasks.stream().map(BpmVerifyInfoVo::getVerifyUserId).collect(Collectors.toList());
        Map<String, String> stringStringMap = bpmnEmployeeInfoProviderService.provideEmployeeInfo(verifyUserIds);
        for (BpmVerifyInfoVo task : tasks) {
            String verifyUidStr = task.getVerifyUserId();
            String verifyUserName = stringStringMap.get(verifyUidStr);
            task.setVerifyUserName(verifyUserName);
        }
        List<BpmVerifyInfoVo> taskInfors = Lists.newArrayList();

        if (tasks.size() > 1) {
            String verifyUserName = StringUtils.join(tasks.stream().map(BpmVerifyInfoVo::getVerifyUserName).collect(Collectors.toList()), ",");
            String taskName = StringUtils.EMPTY;
            List<String> strs = tasks.stream().map(BpmVerifyInfoVo::getTaskName).filter(Objects::nonNull).collect(Collectors.toList());
            if (!org.springframework.util.ObjectUtils.isEmpty(strs)) {
                taskName = strs.get(0);
            }
            String elementId = tasks.stream().map(BpmVerifyInfoVo::getElementId).findFirst().orElse(StringUtils.EMPTY);
            taskInfors.add(BpmVerifyInfoVo.builder()
                            .verifyUserIds(verifyUserIds)
                    .verifyUserName(verifyUserName)
                    .taskName(taskName)
                    .elementId(elementId)
                    .build());
        } else {
            tasks.get(0).setVerifyUserIds(verifyUserIds);
            taskInfors.add(tasks.get(0));
        }

        return taskInfors;
    }

    /**
     * get verify info by a list of process codes
     *
     * @param processCodes
     * @return
     */
    public Map<String, List<BpmVerifyInfoVo>> getBpmVerifyInfoBatch(List<String> processCodes) {
        List<BpmVerifyInfoVo> vos = this.getBaseMapper().verifyInfoList(BpmVerifyInfoVo.builder().processCodeList(processCodes)
                .build());
        if (!ObjectUtils.isEmpty(vos)) {
            return vos.stream().collect(Collectors.groupingBy(BpmVerifyInfoVo::getProcessCode));
        }
        return new HashMap<>();
    }

    /**
     * list verify info by a process code
     *
     * @param processCodes
     * @return
     */
    public Map<String, String> listBpmVerifyInfoVo(List<String> processCodes) {
        Map<String, String> map = Maps.newHashMap();
        Map<String, List<BpmVerifyInfoVo>> listMap = this.getBpmVerifyInfoBatch(processCodes);
        if (!ObjectUtils.isEmpty(listMap)) {
            listMap.forEach((key, value) -> {
                List<BpmVerifyInfoVo> list = listMap.get(key);
                StringBuffer buf = new StringBuffer();
                for (BpmVerifyInfoVo vo : list) {
                    String join = StringUtils.join(vo.getTaskName(), ":", vo.getVerifyUserName(), "--");
                    //buf.append("\r\n");
                    buf.append(join);
                }
                String verify = buf.toString().substring(0, buf.toString().length() - 2);
                map.put(key, verify);
            });
        }
        return map;
    }
}
