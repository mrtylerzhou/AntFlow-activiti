package org.openoa.engine.bpmnconf.common;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.BpmnConfFlagsEnum;
import org.openoa.base.constant.enums.ProcessNoticeEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.interf.ActivitiServiceAnno;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BaseNumIdStruVo;
import org.openoa.base.vo.DIYProcessInfoDTO;
import org.openoa.base.vo.TaskMgmtVO;
import org.openoa.engine.bpmnconf.confentity.BpmProcessNotice;
import org.openoa.engine.bpmnconf.confentity.BpmnConf;
import org.openoa.engine.bpmnconf.mapper.BpmBusinessProcessMapper;
import org.openoa.engine.bpmnconf.mapper.TaskMgmtMapper;
import org.openoa.engine.bpmnconf.service.biz.BpmBusinessProcessServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmProcessNoticeServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmnConfServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author AntFlow
 * @since 0.5
 */
@Slf4j
@Service
public class TaskMgmtServiceImpl extends ServiceImpl<TaskMgmtMapper, TaskMgmtVO> {
    @Autowired
    private TaskMgmtMapper taskMgmtMapper;
    @Autowired
    private TaskService taskService;
    @Autowired
    private BpmBusinessProcessServiceImpl processService;
    @Autowired
    protected BpmBusinessProcessMapper bpmBusinessProcessMapper;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired(required = false)
    private Map<String, FormOperationAdaptor> formOperationAdaptorMap;
    @Autowired
    private BpmnConfServiceImpl bpmnConfService;
    @Autowired
    private BpmProcessNoticeServiceImpl bpmProcessNoticeService;


    /**
     * find task by its id
     *
     * @param taskId
     * @return
     * @throws JiMuBizException
     */
    public TaskMgmtVO findTask(String taskId) throws JiMuBizException {
        return taskMgmtMapper.findTask(taskId);
    }

    /**
     * get task vo by its id
     *
     * @param taskId
     * @return
     * @throws JiMuBizException
     */
    public TaskMgmtVO getAgencyList(String taskId) throws JiMuBizException {
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();
        TaskMgmtVO mgmtVO = new TaskMgmtVO();
        if (!ObjectUtils.isEmpty(task)) {
            mgmtVO.setTaskStype(2);
        } else {
            mgmtVO.setTaskStype(1);
        }
        return mgmtVO;
    }

    /**
     * change task assignee by taskid
     */
    public void updateTask(TaskMgmtVO taskMgmtVO) {
        if (ObjectUtils.isEmpty(taskMgmtVO.getTaskIds())) {
            throw new JiMuBizException("please select the task ids to modify ！！");
        }
        if (!ObjectUtils.isEmpty(taskMgmtVO.getTaskIds())) {
            taskMgmtVO.getTaskIds().forEach(o -> {
                taskMgmtMapper.updateaActinst(TaskMgmtVO.builder()
                        .applyUser(taskMgmtVO.getApplyUser())
                        .applyUserName(taskMgmtVO.getApplyUserName())
                        .taskId(o)
                        .build());
                taskMgmtMapper.updateaTaskinst(TaskMgmtVO.builder()
                        .applyUser(taskMgmtVO.getApplyUser())
                        .applyUserName(taskMgmtVO.getApplyUserName())
                        .taskId(o)
                        .build());
                taskMgmtMapper.updateTask(TaskMgmtVO.builder()
                        .applyUser(taskMgmtVO.getApplyUser())
                        .applyUserName(taskMgmtVO.getApplyUserName())
                        .taskId(o)
                        .build());
            });

        }
    }


    /**
     * get current node and taskId by businessId and processCode
     *
     * @param taskMgmtVO
     * @return
     */
    public TaskMgmtVO findByTask(TaskMgmtVO taskMgmtVO) {
        try {

            taskMgmtVO.setApplyUser( SecurityUtils.getLogInEmpIdSafe().toString());
            BpmBusinessProcess bpmBusinessProcess = processService.findBpmBusinessProcess(taskMgmtVO.getBusinessId(), taskMgmtVO.getCode());
            if (!ObjectUtils.isEmpty(bpmBusinessProcess)) {
                taskMgmtVO.setEntryId(bpmBusinessProcess.getEntryId());
                TaskMgmtVO mgmtVO = taskMgmtMapper.findByTask(taskMgmtVO);
                return mgmtVO;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new JiMuBizException("根据业务ID:[" + taskMgmtVO.getBusinessId() + "]无法查询代办数据");
        }
    }

    public void  changeFutureAssignees(String executionId, String variableName, List<String> assignees){
        Map<String,Object> assigneeMap=new HashMap<>();
        assigneeMap.put(variableName,assignees);
        runtimeService.setVariables(executionId,assigneeMap);
    }

    public List<DIYProcessInfoDTO> viewProcessInfo(String desc){
        List<DIYProcessInfoDTO> diyProcessInfoDTOS = baseFormInfo(desc);
        if(CollectionUtils.isEmpty(diyProcessInfoDTOS)){
            return diyProcessInfoDTOS;
        }
        List<String> formCodes = diyProcessInfoDTOS.stream().map(DIYProcessInfoDTO::getKey).collect(Collectors.toList());
        LambdaQueryWrapper<BpmnConf> queryWrapper = Wrappers.<BpmnConf>lambdaQuery()
                .select(BpmnConf::getFormCode, BpmnConf::getExtraFlags)
                .in(BpmnConf::getFormCode, formCodes)
                .eq(BpmnConf::getEffectiveStatus, 1);
        List<BpmnConf> bpmnConfs = bpmnConfService.list(queryWrapper);
        if(!CollectionUtils.isEmpty(bpmnConfs)){
            Map<String, Integer> formCode2Flags = bpmnConfs
                    .stream()
                    .filter(a->a.getExtraFlags()!=null)
                    .collect(Collectors.toMap(BpmnConf::getFormCode, BpmnConf::getExtraFlags, (v1, v2) -> v1));
            Map<String, List<BpmProcessNotice>> processNoticeMap = bpmProcessNoticeService.processNoticeMap(formCodes);
            for (DIYProcessInfoDTO diyProcessInfoDTO : diyProcessInfoDTOS) {
                Integer flags = formCode2Flags.get(diyProcessInfoDTO.getKey());
                if(flags!=null){
                    boolean hasStartUserChooseModules = BpmnConfFlagsEnum.hasFlag(flags, BpmnConfFlagsEnum.HAS_STARTUSER_CHOOSE_MODULES);
                    diyProcessInfoDTO.setHasStarUserChooseModule(hasStartUserChooseModules);
                }
            }
        }
        return diyProcessInfoDTOS;
    }
    /**私有方法 */
    private List<DIYProcessInfoDTO> baseFormInfo(String desc){
        List<DIYProcessInfoDTO> results=new ArrayList<>();
        for (Map.Entry<String, FormOperationAdaptor> stringFormOperationAdaptorEntry : formOperationAdaptorMap.entrySet()) {
            String key=stringFormOperationAdaptorEntry.getKey();
            ActivitiServiceAnno annotation = stringFormOperationAdaptorEntry.getValue().getClass().getAnnotation(ActivitiServiceAnno.class);
            if (StringUtils.isEmpty(annotation.desc())){
                continue;
            }
            if(!StringUtils.isEmpty(desc)){
                if(annotation.desc().contains(desc)){
                    results.add(
                            DIYProcessInfoDTO
                                    .builder()
                                    .key(key)
                                    .value(annotation.desc())
                                    .type("DIY")
                                    .build()
                    );
                }
            }
            else{
                results.add(
                        DIYProcessInfoDTO
                                .builder()
                                .key(key)
                                .value(annotation.desc())
                                .type("DIY")
                                .build()
                );
            }
        }
        return results;
    }
}