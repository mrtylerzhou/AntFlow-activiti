package org.openoa.controller;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.openoa.entity.Student;
import org.openoa.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("test")
@RestController
public class TestController {
    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private TaskService taskService;
    @RequestMapping("testit")
    public Object testIt(){

        Student student = studentMapper.selectById(1);
        return student;
    }
}
