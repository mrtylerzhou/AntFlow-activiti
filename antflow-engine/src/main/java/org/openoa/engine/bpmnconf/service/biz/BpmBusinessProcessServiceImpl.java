package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.constant.enums.ProcessEnum;
import org.openoa.base.constant.enums.ProcessStateEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.interf.BpmBusinessProcessService;
import org.openoa.engine.bpmnconf.mapper.BpmBusinessProcessMapper;
import org.springframework.beans.factory.annotation.Autowired;
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


    @Autowired
    private BpmBusinessProcessMapper mapper;

    /**
     * find bpmBusinessProcess by business id and business number
     *
     * @param
     * @param businessId business id
     * @return
     */
    public BpmBusinessProcess findBpmBusinessProcess(String businessId, String businessNumber) {
        return mapper.findBpmBusinessProcess(BpmBusinessProcess.builder().businessNumber(businessNumber).businessId(businessId).build());
    }

    /**
     * save business and process relation data
     *
     * @param businessProcess
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addBusinessProcess(BpmBusinessProcess businessProcess) {
        mapper.insert(businessProcess);
    }


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
        mapper.insert(bpmBusinessProcess);
    }

    /**
     * update business process state
     *
     * @param
     * @return
     */
    public boolean updateBusinessProcess(BpmBusinessProcess bpmBusinessProcess) {
        QueryWrapper<BpmBusinessProcess> wrapper = new QueryWrapper<>();

        wrapper.eq("BUSINESS_NUMBER", bpmBusinessProcess.getBusinessNumber());
        mapper.selectList(wrapper).forEach(o -> {
            o.setProcessState(bpmBusinessProcess.getProcessState());
            if (!ObjectUtils.isEmpty(bpmBusinessProcess)) {
                o.setDescription(bpmBusinessProcess.getDescription());
            }
            mapper.updateById(o);
        });
        return true;
    }

    /**
     * query bpmBusinessProcess by process number
     */
    public BpmBusinessProcess getBpmBusinessProcess(String processCode) {
        QueryWrapper<BpmBusinessProcess> wrapper = new QueryWrapper<>();
        wrapper.eq("BUSINESS_NUMBER", processCode);
        BpmBusinessProcess bpmBusinessProcess = this.getOne(wrapper);

        return bpmBusinessProcess;
    }


    /**
     * get bpmBusinessProcess by bpmBusinessProcess Entity
     */
    public BpmBusinessProcess getBpmBusinessProcess(BpmBusinessProcess bpmBusinessProcess) {
        return mapper.findBpmBusinessProcess(bpmBusinessProcess);
    }

    /**
     * update bpmBusinessProcess by entry id
     *
     * @param entryId
     * @return
     */
    public boolean updateBpmBusinessProcess(String entryId) {
        QueryWrapper<BpmBusinessProcess> wrapper = new QueryWrapper<>();
        wrapper.eq("ENTRY_ID", entryId);
        mapper.selectList(wrapper).stream().forEach(o -> {
            o.setProcessState(ProcessStateEnum.END_STATE.getCode());
            mapper.updateById(o);
        });
        return true;
    }

    /**
     * get process tittles by process numbers
     * @param processNumbers
     * @return
     */
    public List<BpmBusinessProcess> listBpmBusinessProcess(List<String> processNumbers) {
        List<BpmBusinessProcess> result = new ArrayList<>();
        if (ObjectUtils.isEmpty(processNumbers)) {
            return result;
        }
        QueryWrapper<BpmBusinessProcess> wrapper = new QueryWrapper<>();

        wrapper.select("BUSINESS_NUMBER", "description");
        wrapper.in("BUSINESS_NUMBER", processNumbers);
        result = this.mapper.selectList(wrapper);
        return result;
    }

    /**
     * check whether there is duplicate data
     */
    public boolean checkData(String processNumber) {
        long number = this.mapper.selectCount(new QueryWrapper<BpmBusinessProcess>().eq("BUSINESS_NUMBER", processNumber));
        return number <= 0;
    }

    /**
     * check whether there is duplicate data by entry id
     */
    public boolean checkProcessData(String entryId) {
        long number = this.mapper.selectCount(new QueryWrapper<BpmBusinessProcess>().eq("ENTRY_ID", entryId));
        return number <= 0;
    }

    /**
     * update process's isDel field
     */
    public void updateProcessIsDel(String processNumber) {
        BpmBusinessProcess bpmBusinessProcess = this.mapper.selectOne(new QueryWrapper<BpmBusinessProcess>().eq("BUSINESS_NUMBER", processNumber));
        bpmBusinessProcess.setIsDel(1);
        this.updateById(bpmBusinessProcess);
    }
}