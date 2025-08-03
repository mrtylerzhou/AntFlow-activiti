package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.constant.enums.ProcessEnum;
import org.openoa.base.constant.enums.ProcessStateEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.interf.BpmBusinessProcessService;
import org.openoa.base.util.MultiTenantUtil;
import org.openoa.engine.bpmnconf.mapper.BpmBusinessProcessMapper;
import org.openoa.engine.utils.AFWrappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * this is the core service for connecting bpmn and business
 */
@Slf4j
@Service
public class BpmBusinessProcessServiceImpl extends ServiceImpl<BpmBusinessProcessMapper, BpmBusinessProcess> implements BpmBusinessProcessService {



    /**
     * find bpmBusinessProcess by business id and business number
     *
     * @param
     * @param businessId business id
     * @return
     */
    @Override
    public BpmBusinessProcess findBpmBusinessProcess(String businessId, String businessNumber) {
        return getBaseMapper().findBpmBusinessProcess(BpmBusinessProcess.builder().businessNumber(businessNumber).businessId(businessId).build());
    }

    /**
     * save business and process relation data
     *
     * @param businessProcess
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void addBusinessProcess(BpmBusinessProcess businessProcess) {
        getBaseMapper().insert(businessProcess);
    }


    @Override
    public void addBusinessProcess(String businessId, String key, String entryId, String processNum,String bpmnCode, String description) {
        BpmBusinessProcess bpmBusinessProcess = new BpmBusinessProcess();

        bpmBusinessProcess.setVersion(bpmnCode);
        Date nowDate = new Date();
        bpmBusinessProcess.setCreateTime(nowDate);
        bpmBusinessProcess.setUpdateTime(nowDate);
        bpmBusinessProcess.setBusinessId(businessId);
        bpmBusinessProcess.setProcessinessKey(key);
        bpmBusinessProcess.setEntryId(entryId);
        bpmBusinessProcess.setDescription(description);
        bpmBusinessProcess.setBusinessNumber(processNum);
        bpmBusinessProcess.setProcessState(ProcessEnum.COMLETE_STATE.getCode());
        bpmBusinessProcess.setTenantId(MultiTenantUtil.getCurrentTenantId());
        getBaseMapper().insert(bpmBusinessProcess);
    }

    /**
     * update business process state
     *
     * @param
     * @return
     */
    @Override
    public BpmBusinessProcess updateBusinessProcess(BpmBusinessProcess bpmBusinessProcess) {

        List<BpmBusinessProcess> bpmBusinessProcesses = getBaseMapper().selectList(AFWrappers.<BpmBusinessProcess>lambdaTenantQuery()
                .eq(BpmBusinessProcess::getBusinessNumber,bpmBusinessProcess.getBusinessNumber()));
        bpmBusinessProcesses.forEach(o -> {
            o.setProcessState(bpmBusinessProcess.getProcessState());
            if (!ObjectUtils.isEmpty(bpmBusinessProcess)) {
                o.setDescription(bpmBusinessProcess.getDescription());
            }
            getBaseMapper().updateById(o);
        });
        return bpmBusinessProcesses.get(0);
    }

    /**
     * query bpmBusinessProcess by process number
     */
    @Override
    public BpmBusinessProcess getBpmBusinessProcess(String processCode) {
        BpmBusinessProcess bpmBusinessProcess = this.getOne(AFWrappers.<BpmBusinessProcess>lambdaTenantQuery()
                .eq(BpmBusinessProcess::getBusinessNumber,processCode));

        return bpmBusinessProcess;
    }


    /**
     * get bpmBusinessProcess by bpmBusinessProcess Entity
     */
    @Override
    public BpmBusinessProcess getBpmBusinessProcess(BpmBusinessProcess bpmBusinessProcess) {
        return getBaseMapper().findBpmBusinessProcess(bpmBusinessProcess);
    }

    /**
     * update bpmBusinessProcess by entry id
     *
     * @param procInstId
     * @return
     */
    @Override
    public boolean updateBpmBusinessProcess(String procInstId) {

        getBaseMapper().selectList(AFWrappers.<BpmBusinessProcess>lambdaTenantQuery()
                .eq(BpmBusinessProcess::getProcInstId,procInstId)).forEach(o -> {
            o.setProcessState(ProcessStateEnum.END_STATE.getCode());
            getBaseMapper().updateById(o);
        });
        return true;
    }

    /**
     * get process tittles by process numbers
     * @param processNumbers
     * @return
     */
    @Override
    public List<BpmBusinessProcess> listBpmBusinessProcess(List<String> processNumbers) {
        List<BpmBusinessProcess> result = new ArrayList<>();
        if (ObjectUtils.isEmpty(processNumbers)) {
            return result;
        }

        result = this.getBaseMapper().selectList(AFWrappers.<BpmBusinessProcess>lambdaTenantQuery()
                        .select(BpmBusinessProcess::getBusinessNumber,BpmBusinessProcess::getDescription)
                .in(BpmBusinessProcess::getBusinessNumber,processNumbers));
        return result;
    }

    /**
     * check whether there is duplicate data
     */
    @Override
    public boolean checkData(String processNumber) {
        long number = this.getBaseMapper().selectCount(Wrappers.<BpmBusinessProcess>lambdaQuery().eq(BpmBusinessProcess::getBusinessNumber, processNumber));
        return number <= 0;
    }

    /**
     * check whether there is duplicate data by entry id
     */
    @Override
    public boolean checkProcessData(String entryId) {
        long number = this.getBaseMapper().selectCount(AFWrappers.<BpmBusinessProcess>lambdaTenantQuery().eq(BpmBusinessProcess::getEntryId, entryId));
        return number <= 0;
    }

    /**
     * update process's isDel field
     */
    @Override
    public void updateProcessIsDel(String processNumber) {
        BpmBusinessProcess bpmBusinessProcess = this.getBaseMapper().selectOne(Wrappers.<BpmBusinessProcess>lambdaQuery().eq(BpmBusinessProcess::getBusinessNumber, processNumber));
        bpmBusinessProcess.setIsDel(1);
        this.updateById(bpmBusinessProcess);
    }
}