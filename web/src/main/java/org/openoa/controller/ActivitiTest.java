package org.openoa.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.base.util.MailUtils;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.MailInfo;
import org.openoa.engine.bpmnconf.common.TaskMgmtServiceImpl;
import org.openoa.engine.conf.engineconfig.CustomTenantInfoHolder;
import org.openoa.engine.factory.TagParser;
import org.openoa.common.adaptor.bpmnelementadp.BpmnElementAdaptor;
import org.openoa.engine.bpmnconf.service.biz.TraditionalActivitiServiceImpl;
import org.openoa.common.adaptor.bpmnelementadp.AbstractOrderedSignNodeAdp;
import org.openoa.base.constant.enums.OrderNodeTypeEnum;
import org.openoa.base.interf.anno.IgnoreLog;
import org.openoa.base.dto.PageDto;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.engine.factory.AutoParseProxyFactory;
import org.openoa.engine.factory.IAdaptorFactory;
import org.openoa.base.entity.Result;
import org.openoa.base.interf.ActivitiService;
import org.openoa.entity.Student;
import org.openoa.mapper.PersonMapper;
import org.openoa.mapper.StudentMapper;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.engine.bpmnconf.service.biz.BpmBusinessProcessServiceImpl;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.service.biz.MybisService;
import org.openoa.service.impl.PersonServiceImpl;
import org.openoa.base.util.PageUtils;
import org.openoa.base.util.ThreadLocalContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("activiti")
public class ActivitiTest {
    @Autowired
    private TraditionalActivitiServiceImpl traditionalActivitiService;
    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private MybisService mybisService;
    @Autowired
    private IAdaptorFactory adaptorFactory;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private BpmBusinessProcessServiceImpl bpmBusinessProcessService;
    @Autowired
    private PersonServiceImpl personService;
    @Autowired
    private PersonMapper personMapper;
    @Autowired
    private IAdaptorFactory iAdaptorFactory;
    @Autowired
    private MailUtils mailUtils;
    @Autowired
    private TaskMgmtServiceImpl taskMgmtService;

    @RequestMapping("/getModel")
    public Result getModel(String processNumber) throws Exception {
       /* personService.userOpTransactional();
        log.info("you request me");
        ThreadLocalContainer.set("hello","hehe");
        personService.asyncDemo();
        Object hello = ThreadLocalContainer.get("hello");
        try {

            personService.studentOPTrans();
       }catch (Exception e){
            log.info("you request me with error",e);
       }
        //personService.opTranstionally();
       if(3==3){
           return Result.newSuccessResult(null);
       }*/
        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(processNumber);
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(bpmBusinessProcess.getProcInstId()).list();
        List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery().processInstanceId(bpmBusinessProcess.getProcInstId()).list();
        HistoricActivityInstance historicActivityInstance = historicActivityInstances.get(0);
        String processDefinitionId = historicActivityInstance.getProcessDefinitionId();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        Collection<FlowElement> flowElements = bpmnModel.getMainProcess().getFlowElements();
        for (FlowElement flowElement : flowElements) {
            if(flowElement instanceof UserTask){
                UserTask userTask=(UserTask) flowElement;
                String assignee = userTask.getAssignee();
                String s = "hello";
            }
        }
        return Result.newSuccessResult(null);
    }
    @IgnoreLog
    @RequestMapping("convertToModel")
    public Result xmlToModel(String processNumber)throws Exception{
        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(processNumber);
        HistoricActivityInstance historicActivityInstance = historyService.createHistoricActivityInstanceQuery().processInstanceId(bpmBusinessProcess.getProcInstId()).list().get(0);
        String processDefinitionId = historicActivityInstance.getProcessDefinitionId();
        ProcessDefinition processDefinition =repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
        //获取流程资源的名称
        String sourceName = processDefinition.getResourceName();
        //获取流程资源
        InputStream inputStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(),sourceName);
        //创建转换对象
        BpmnXMLConverter converter = new BpmnXMLConverter();
        //读取xml文件
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(inputStream);
        //将xml文件转换成BpmnModel
        BpmnModel bpmnModel = converter.convertToBpmnModel((XMLStreamReader) reader);

        org.activiti.bpmn.model.Process process = bpmnModel.getMainProcess();
        Collection<FlowElement> flowElements = process.getFlowElements();
        for (FlowElement flowElement : flowElements) {
            if(flowElement instanceof UserTask){
                UserTask userTask=(UserTask) flowElement;
                String assignee = userTask.getAssignee();
                String s = "hello";
            }
        }
        return Result.newSuccessResult(null);
    }
    @RequestMapping("getStudent")
    public Result getStudentPageList(@RequestBody PageDto pageDto){
        List<BaseIdTranStruVo> baseIdTranStruVos = studentMapper.selectAllStudent();
        Page<Student> page= PageUtils.getPageByPageDto(pageDto);
        List<Student> studentList = studentMapper.getStudentList(page);

        page.setRecords(studentList);
        page.setTotal(studentList.size());
        return Result.newSuccessResult(page);
    }
    @RequestMapping("deploy")
    public Object deployProcess(String res) {
        if (StringUtils.isBlank(res)) {
            res = "test01.bpmn20.xml";
        }

        traditionalActivitiService.createDeployment(res);
        return "ok";
    }

    @RequestMapping("start")
    public Object startProcessInstance(String key) {
        traditionalActivitiService.startActivityDemo("reimbursement-9");
        return "ok";
    }

    @RequestMapping("getTask")
    public Object getTask() {
        Student student = studentMapper.selectOneStudent();
        List<Student> students = studentMapper.selectList(null);
        List<Task> currentTask = traditionalActivitiService.getCurrentTask();
        return currentTask;
    }

    @RequestMapping("stud")
    public Object insertStud() throws Exception {
        ActivitiService activitiService = adaptorFactory.getActivitiService(new BusinessDataVo());
        Student stud = new Student();
        stud.getAge();
        Field name = Student.class.getDeclaredField("name");
        name.setAccessible(true);
        name.set(stud, "baidu");
        mybisService.getit();
        return "ok";

    }
    @RequestMapping("/test")
    public  Result testResult(){
        TagParser proxyInstance1 = AutoParseProxyFactory.getProxyInstance(TagParser.class, "org.openoa.base.constant.enums.NodePropertyEnum", "org.openoa.common.adaptor.bpmnelementadp.BpmnElementAdaptor");
        BpmnElementAdaptor bpmnElementAdaptor = iAdaptorFactory.getBpmnElementAdaptor(NodePropertyEnum.NODE_PROPERTY_BUSINESSTABLE);
        Object o = proxyInstance1.parseTag(NodePropertyEnum.NODE_PROPERTY_BUSINESSTABLE);
        BusinessDataVo vo=new BusinessDataVo();
        vo.setOperationType(ProcessOperationEnum.BUTTON_TYPE_SUBMIT.getCode());
        ProcessOperationAdaptor processOperation = iAdaptorFactory.getProcessOperation(vo);
        AbstractOrderedSignNodeAdp orderedSignNodeAdp = iAdaptorFactory.getOrderedSignNodeAdp(OrderNodeTypeEnum.TEST_ORDERED_SIGN);
        return Result.newSuccessResult("ok");
    }
    @PostMapping("/testSendEmail")
    public Result testSendEmail(){
        MailInfo mailInfo = MailInfo
                .builder()
                .receiver("zypqqgc@qq.com")
                .title("邮件测试" + new Date())
                .content("keep refrain from sending test email to us")
                .build();
        mailUtils.sendMail(mailInfo);
        return Result.success();
    }
    @PostMapping("/changefutureAssignees")
    public Result changeFutureAssignee(String executionId,String variableName,String assignees){
        List<String> assigneeList = Arrays.asList(assignees.split(","));
        taskMgmtService.changeFutureAssignees(executionId,variableName,assigneeList);
        return Result.success();
    }
}
