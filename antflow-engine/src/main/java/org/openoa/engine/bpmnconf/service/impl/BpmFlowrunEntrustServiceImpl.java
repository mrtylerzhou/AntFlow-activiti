package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import org.activiti.engine.TaskService;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BpmFlowrunEntrustVo;
import org.openoa.engine.bpmnconf.confentity.BpmFlowrunEntrust;
import org.openoa.engine.bpmnconf.confentity.UserEntrust;
import org.openoa.engine.bpmnconf.mapper.BpmFlowrunEntrustMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BpmFlowrunEntrustServiceImpl extends ServiceImpl<BpmFlowrunEntrustMapper, BpmFlowrunEntrust> {


    @Autowired
    private BpmFlowrunEntrustMapper mapper;
    @Autowired
    private TaskService taskService;

    /**
     * add flow entrust
     *
     * @param actual    current entrust user
     * @param runtaskid task id
     * @param type      0 entrust 1:circulate
     */
    public void addFlowrunEntrust(Integer actual, Integer original, String runtaskid, Integer type, String ProcessInstanceId, String processKey) {
        BpmFlowrunEntrust entrust = new BpmFlowrunEntrust();
        entrust.setType(type);
        entrust.setRuntaskid(runtaskid);
        entrust.setActual(actual);
        entrust.setOriginal(original);
        entrust.setIsRead(2);
        entrust.setProcDefId(processKey);
        entrust.setRuninfoid(ProcessInstanceId);
        mapper.insert(entrust);
    }

    public boolean addFlowrunEntrust(BpmFlowrunEntrust flowrunEntrust) {
        mapper.insert(flowrunEntrust);
        return true;
    }

    /**
     * check whether current process has entrust
     *
     * @param receiverId
     * @param processKey
     * @return
     * @throws Exception
     */
    public UserEntrust getBpmEntrust(Integer receiverId, String processKey) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = df.parse(df.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String key = "";
        if (processKey.contains(":")) {
            key = processKey.split(":")[0].toString();
        }
        UserEntrust entrust = mapper.getBpmEntrust(receiverId, !Strings.isNullOrEmpty(key) ? key : processKey);
        UserEntrust userEntrust = null;
        if (!ObjectUtils.isEmpty(entrust) && date != null) {
            if (entrust.getBeginTime() == null && entrust.getEndTime() == null) {
                userEntrust = entrust;
            } else if (entrust.getBeginTime() != null && entrust.getEndTime() != null) {
                if (date.getTime() >= entrust.getBeginTime().getTime() && date.getTime() <= entrust.getEndTime().getTime()) {
                    userEntrust = entrust;
                }
            } else if (entrust.getBeginTime() == null && entrust.getEndTime() != null) {
                if (entrust.getEndTime().getTime() >= date.getTime()) {
                    userEntrust = entrust;
                }
            } else if (entrust.getBeginTime() != null && entrust.getEndTime() == null) {
                if (entrust.getBeginTime().getTime() <= date.getTime()) {
                    userEntrust = entrust;
                }
            }
        }
        return userEntrust;
    }

    /**
     * @param receiverId entrusted user id
     * @param processKey process key
     * @param taskId     task id
     * @return
     */
    public Integer getUserId(Integer receiverId, String processKey, String taskId, String ProcessInstanceId) {

        //query to check whether current process has been entrusted
        UserEntrust entrust = this.getBpmEntrust(receiverId, processKey);
        if (!ObjectUtils.isEmpty(entrust)) {
            //add entrust record
            this.addFlowrunEntrust(entrust.getReceiverId(), receiverId, taskId, 1, ProcessInstanceId, processKey);
            return entrust.getReceiverId();
        } else {
            return null;
        }
    }

    public Boolean updateBpmFlowrunEntrust(String processInstanceId, Integer loginUserId) {
        mapper.updateBpmFlowrunEntrust(processInstanceId, loginUserId);
        return true;
    }

    /**
     * update entrusted flow status as viewed
     */
    public boolean editFlowrunEntrustState(String processInstanceId) {
        QueryWrapper<BpmFlowrunEntrust> wrapper = new QueryWrapper<>();
        wrapper.eq("runinfoid", processInstanceId);
        wrapper.eq("original",  SecurityUtils.getLogInEmpIdSafe());
        mapper.selectList(wrapper).forEach(o -> {
            o.setIsView(1);
            mapper.updateById(o);
        });
        return true;
    }

    /**
     * query entrust and circulate data by process instance id
     *
     * @param vo
     * @return
     */
    public List<BpmFlowrunEntrust> findFlowrunEntrustByProcessInstanceId(BpmFlowrunEntrustVo vo) {
        return Optional.ofNullable(this.mapper.selectList(new QueryWrapper<BpmFlowrunEntrust>().eq("type", vo.getType()).eq("runinfoid", vo.getRuninfoid()))).orElse(Arrays.asList());
    }

}
