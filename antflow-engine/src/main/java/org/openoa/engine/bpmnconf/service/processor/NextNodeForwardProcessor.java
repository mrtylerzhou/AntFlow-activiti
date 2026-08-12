package org.openoa.engine.bpmnconf.service.processor;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.BpmnConfFlagsEnum;
import org.openoa.base.dto.BpmNextTaskDto;
import org.openoa.base.entity.BpmFlowrunEntrust;
import org.openoa.base.entity.BpmnConf;
import org.openoa.base.service.AfUserService;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.UserAvailableVo;
import org.openoa.engine.bpmnconf.service.impl.BpmFlowrunEntrustServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.UserEntrustServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnConfService;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Service
public class NextNodeForwardProcessor  implements AntFlowNextNodeBeforeWriteProcessor{
    @Resource
    private UserEntrustServiceImpl userEntrustService;
    @Resource
    private BpmFlowrunEntrustServiceImpl bpmFlowrunEntrustService;
    @Resource
    private AfUserService afUserService;
    @Resource
    private BpmnConfService bpmnConfService;

    @Override
    public void postProcess(BpmNextTaskDto bpmNextTaskDto) {
        DelegateTask delegateTask = bpmNextTaskDto.getDelegateTask();
        String formCode = bpmNextTaskDto.getFormCode();
        //set process entrust info
        String oldUserId = delegateTask.getAssignee();
        String oldUserName="";
        if(delegateTask instanceof TaskEntity){
            oldUserName=((TaskEntity)delegateTask).getAssigneeName();
        }
        BaseIdTranStruVo entrustEmployee = userEntrustService.getEntrustEmployee(oldUserId,oldUserName, formCode);
        String userId =entrustEmployee.getId();
        String userName=entrustEmployee.getName();

        //if userId is not null and valid then set user task delegate
        if (!StringUtils.isEmpty(userId)) {
            delegateTask.setAssignee(userId);
            if(delegateTask instanceof  TaskEntity){
                ((TaskEntity)delegateTask).setAssigneeName(userName);
            }
        }
        String assignee = delegateTask.getAssignee();
        BaseIdTranStruVo offDutyDelegate = tryOffDutyDelegate(bpmNextTaskDto, assignee);
        if (offDutyDelegate != null && !StringUtils.isEmpty(offDutyDelegate.getId())) {
            userId = offDutyDelegate.getId();
            userName = offDutyDelegate.getName();
            delegateTask.setAssignee(userId);
            if(delegateTask instanceof  TaskEntity){
                ((TaskEntity)delegateTask).setAssigneeName(userName);
            }
        }


        //如果委托生效 则在我的委托列表中加一条数据
        if (!oldUserId.equals(userId)) {
            BpmFlowrunEntrust entrust = new BpmFlowrunEntrust();
            entrust.setType(1);
            entrust.setRuntaskid(delegateTask.getId());
            entrust.setActual(userId);
            entrust.setActualName(userName);
            entrust.setOriginal(oldUserId);
            entrust.setOriginalName(oldUserName);
            entrust.setIsRead(2);
            entrust.setProcDefId(formCode);
            entrust.setRuninfoid(delegateTask.getProcessInstanceId());
            bpmFlowrunEntrustService.addFlowrunEntrust(entrust);
            log.info("委托生效，委托前：{}，委托后；{}", oldUserId, userId);
        }
    }

    /**
     * 审批人非办公状态自动转办门禁:
     * 1. 流程未配置 AUTO_DELEGATE_OFF_DUTY → 不转办
     * 2. 审批人处于办公状态(可用)→ 不转办
     * 3. 不可用但时间窗口未命中 → 不转办
     * 4. 不可用且时间窗口命中 → 返回接口给定的转办目标人(delegateUser)
     */
    private BaseIdTranStruVo tryOffDutyDelegate(BpmNextTaskDto bpmNextTaskDto, String oldUserId) {
        if (StringUtils.isEmpty(oldUserId)) {
            return null;
        }
        //1. 流程配置门禁
        BpmnConf bpmnConf = getBpmnConf(bpmNextTaskDto);
        if (bpmnConf == null || !BpmnConfFlagsEnum.AUTO_DELEGATE_OFF_DUTY.flagsContainsCurrent(bpmnConf.getExtraFlags())) {
            return null;
        }
        //2. 审批人可用性(办公状态)
        UserAvailableVo availableVo = afUserService.checkEmployeeEffective(oldUserId);
        if (availableVo == null || !Boolean.FALSE.equals(availableVo.getAvailable())) {
            return null;
        }
        //3. 不可用时间窗口判断
        if (!isUnavailableTimeWindowHit(availableVo)) {
            return null;
        }
        //4. 返回转办目标人
        return availableVo.getDelegateUser();
    }

    /**
     * 不可用时间窗口四象限判断(当前时间):
     * - 无开始无结束 → 永久不可用,直接生效
     * - 只有开始时间,开始早于当前 → 生效
     * - 只有结束时间,结束晚于当前 → 生效
     * - 同时有开始和结束,当前在区间内 → 生效
     */
    private boolean isUnavailableTimeWindowHit(UserAvailableVo availableVo) {
        Date now = new Date();
        Date begin = availableVo.getUnavailableBeginTime();
        Date end = availableVo.getUnavailableEndTime();
        long nowTime = now.getTime();
        if (begin == null && end == null) {
            return true;
        } else if (begin != null && end == null) {
            return nowTime >= begin.getTime();
        } else if (begin == null && end != null) {
            return nowTime <= end.getTime();
        } else {
            return nowTime >= begin.getTime() && nowTime <= end.getTime();
        }
    }

    /**
     * 按流程 bpmnCode 查询流程配置
     */
    private BpmnConf getBpmnConf(BpmNextTaskDto bpmNextTaskDto) {
        if (ObjectUtils.isEmpty(bpmNextTaskDto.getBpmnCode())) {
            return null;
        }
        return bpmnConfService.getOne(Wrappers.<BpmnConf>lambdaQuery()
                .eq(BpmnConf::getBpmnCode, bpmNextTaskDto.getBpmnCode()));
    }

    @Override
    public int order() {
        return 1;
    }
}
