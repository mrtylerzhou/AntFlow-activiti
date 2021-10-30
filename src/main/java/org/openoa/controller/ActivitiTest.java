package org.openoa.controller;

import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.openoa.bpmnconf.TraditionalActivitiServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("activiti")
public class ActivitiTest {
    @Autowired
    private TraditionalActivitiServiceImpl traditionalActivitiService;
    @RequestMapping("deploy")
    public Object deployProcess(String res){
        if(StringUtils.isBlank(res)){
            res="test01.bpmn20.xml";
        }
        traditionalActivitiService.createDeployment(res);
        return "ok";
    }
    @RequestMapping("start")
    public Object startProcessInstance(String key){
        traditionalActivitiService.startActivityDemo("reimbursement-9");
        return "ok";
    }
    @RequestMapping("getTask")
    public Object getTask(){
        List<Task> currentTask = traditionalActivitiService.getCurrentTask();
        return currentTask;
    }
}
