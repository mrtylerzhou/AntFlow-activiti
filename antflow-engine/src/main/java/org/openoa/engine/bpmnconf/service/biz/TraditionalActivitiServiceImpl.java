package org.openoa.engine.bpmnconf.service.biz;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 传统流程部署,获取信息测试类
 */
@Service
public class TraditionalActivitiServiceImpl {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;

    public boolean createDeployment(String resourceName) {
        DeploymentBuilder builder = repositoryService.createDeployment();
        builder.addClasspathResource("processdiagram/" + resourceName);
        builder.deploy();
        return true;
    }

    public boolean startActivityDemo(String key) {
        Map<String, Object> map = new HashMap<>();
        map.put("fee", 800);
        ProcessInstance test01 = runtimeService.startProcessInstanceById("reimbursement-9:1:4", map);

        String id = test01.getId();
        System.out.println("流程id=" + id);
        return true;
    }

    public List<Task> getCurrentTask() {
        List<Task> list = taskService.createTaskQuery().processInstanceId("2509").list();
        return list;
    }
}
