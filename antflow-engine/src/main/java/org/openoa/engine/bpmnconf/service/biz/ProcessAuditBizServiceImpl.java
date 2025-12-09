package org.openoa.engine.bpmnconf.service.biz;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.javers.core.Changes;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;
import org.openoa.base.entity.BpmProcessAudit;
import org.openoa.base.interf.BpmBusinessProcessService;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.service.interf.biz.ProcessAuditBizService;
import org.openoa.engine.factory.FormFactory;
import org.openoa.base.vo.UDLFApplyVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class ProcessAuditBizServiceImpl implements ProcessAuditBizService {
    @Autowired
    private FormFactory formFactory;
    @Autowired
    private TaskService taskService;
    @Autowired
    private BpmBusinessProcessService bpmBusinessProcessService;

    private static Javers javers = JaversBuilder.javers().build();

    public void saveChanges(BusinessDataVo vo,Class<?> entityClass){
        FormOperationAdaptor formAdapter = formFactory.getFormAdaptor(vo);
        String formCode=vo.getFormCode();
        String processNumber = vo.getProcessNumber();
        String taskId=vo.getTaskId();
        List<BpmProcessAudit> audits=new ArrayList<>();
        try {
            String createUser= SecurityUtils.getLogInEmpIdStr();
            Object currentValue=null;
            Object oldValue=null;
            if(Objects.equals(vo.getIsLowCodeFlow(),1)){
                Map<String, Object> currentMap = ((UDLFApplyVo) vo).getLfFields();
                formAdapter.queryData(vo);
                Map<String, Object> oldMap = ((UDLFApplyVo) vo).getLfFields();
                if(currentMap.size()>=oldMap.size()){
                    for (Map.Entry<String, Object> stringObjectEntry : currentMap.entrySet()) {
                        String key=stringObjectEntry.getKey();
                        Object value=stringObjectEntry.getValue();
                        Object oldV=oldMap.get(key);
                        if(!Objects.equals(value,oldV)){
                            BpmProcessAudit audit=new BpmProcessAudit();
                            audit.setFormCode(formCode);
                            audit.setProcessNumber(processNumber);
                            audit.setFieldName(key);
                            audit.setOldValue(oldV.toString());
                            audit.setNewValue(value.toString());
                            audit.setCreateUser(createUser);
                            audits.add(audit);
                        }
                    }
                }
            }else{
                currentValue = JSON.to(entityClass, vo);
                formAdapter.queryData(vo);
                oldValue= JSON.to(entityClass, vo);
                Diff diff = javers.compare(oldValue, currentValue);
                Changes changes = diff.getChanges();
                for (Change change : changes) {
                    ValueChange valueChange = (ValueChange) change;
                    Object left = valueChange.getLeft();
                    Object right = valueChange.getRight();
                    String propertyName = valueChange.getPropertyName();
                    BpmProcessAudit audit=new BpmProcessAudit();
                    audit.setFormCode(formCode);
                    audit.setProcessNumber(processNumber);
                    audit.setFieldName(propertyName);
                    audit.setOldValue(left.toString());
                    audit.setNewValue(right.toString());
                    audit.setCreateUser(createUser);
                    audits.add(audit);
                }
            }

            if(!CollectionUtils.isEmpty(audits)){
                List<Task> tasks = taskService.createTaskQuery().taskId(taskId).list();
                Task task = tasks.get(0);
                for (BpmProcessAudit audit : audits) {
                    audit.setTaskDefKey(task.getTaskDefinitionKey());
                    audit.setTaskName(task.getName());
                }
                this.getService().saveBatch(audits);
            }
        }catch (Exception e){
            log.error("save audit info error,processNumber:{}",processNumber,e);
        }
    }
}
