package org.openoa.engine.bpmnconf.service.flowcontrol;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.RuntimeServiceImpl;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.base.constant.enums.ProcessStateEnum;
import org.openoa.base.entity.ActHiTaskinst;
import org.openoa.base.entity.SimpleRuntimeActivityDefinitionEntity;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.exception.BusinessErrorEnum;
import org.openoa.base.interf.BpmBusinessProcessService;
import org.openoa.base.service.RuntimeActivityDefinitionEntityIntepreter;
import org.openoa.base.util.ProcessDefinitionUtils;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.common.service.BpmVariableMultiplayerServiceImpl;
import org.openoa.engine.bpmnconf.adp.processoperation.AddAssigneeProcessImpl;
import org.openoa.engine.bpmnconf.mapper.TaskMgmtMapper;
import org.openoa.engine.bpmnconf.service.cmd.CreateAndTakeTransitionCmd;
import org.openoa.engine.bpmnconf.service.cmd.DeleteRunningTaskCmd;
import org.openoa.engine.bpmnconf.service.cmd.StartActivityCmd;
import org.openoa.engine.bpmnconf.service.impl.ActHiTaskinstServiceImpl;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class DefaultTaskFlowControlService implements TaskFlowControlService
{

	ProcessDefinitionEntity _processDefinition;

	ProcessEngine _processEngine;

	private String _processInstanceId;
	private final BpmVariableMultiplayerServiceImpl _bpmVariableMultiplayerService;


	public DefaultTaskFlowControlService(
			ProcessEngine processEngine, String processId, BpmVariableMultiplayerServiceImpl bpmVariableMultiplayerService)
	{

		_processEngine = processEngine;
		_processInstanceId = processId;
		this._bpmVariableMultiplayerService = bpmVariableMultiplayerService;

		String processDefId = _processEngine.getRuntimeService().createProcessInstanceQuery()
				.processInstanceId(_processInstanceId).singleResult().getProcessDefinitionId();

		_processDefinition = ProcessDefinitionUtils.getProcessDefinition(_processEngine, processDefId);

	}



	private void executeCommand(Command<java.lang.Void> command)
	{
		((RuntimeServiceImpl) _processEngine.getRuntimeService()).getCommandExecutor().execute(command);
	}

	private List<Task> getCurrentTasks()
	{
		return  _processEngine.getTaskService().createTaskQuery().processInstanceId(_processInstanceId)
				.active().list();
	}

	private List<Task> getTaskById(String taskId)
	{
		List<Task> tasks = _processEngine.getTaskService().createTaskQuery().taskId(taskId).list();
		return tasks;
	}




	/**
	 * 跳转（包括回退和向前）至指定活动节点
	 * 
	 * @param targetTaskDefinitionKey
	 * @throws Exception
	 */
	@Override
	public List<String> moveTo(String currentTaskDefKey,String targetTaskDefinitionKey) throws Exception
	{
		List<Task> currentTasks = getCurrentTasks();
		moveTo(currentTasks,currentTaskDefKey, targetTaskDefinitionKey);
		List<String> otherParallTaskDefKeys = currentTasks.stream().map(TaskInfo::getTaskDefinitionKey).filter(a -> !a.equals(currentTaskDefKey)).collect(Collectors.toList());
		return otherParallTaskDefKeys;
	}

	@Override
	public void  moveOneStepForward(String processNumber) throws Exception{
		List<Task> currentTasks = getCurrentTasks();
		TaskEntity currentTaskEntity =(TaskEntity) currentTasks.get(0);
		ActivityImpl activity = (ActivityImpl) ProcessDefinitionUtils
				.getActivity(_processEngine, currentTaskEntity.getProcessDefinitionId(),
						currentTaskEntity.getTaskDefinitionKey()).getOutgoingTransitions().get(0).getDestination();
		moveBackwardOrForward(processNumber,activity.getId(),currentTasks);
	}
	@Override
	public void  moveOneStepBack(String processNumber) throws Exception {
		List<Task> currentTasks = getCurrentTasks();
		TaskEntity currentTaskEntity =(TaskEntity) currentTasks.get(0);
		ActivityImpl activity = (ActivityImpl) ProcessDefinitionUtils
				.getActivity(_processEngine, currentTaskEntity.getProcessDefinitionId(),
						currentTaskEntity.getTaskDefinitionKey()).getIncomingTransitions().get(0).getSource();
		moveBackwardOrForward(processNumber,activity.getId(),currentTasks);
	}
	private void moveBackwardOrForward(String processNumber,String moveToActivityId,List<Task> currentTasks)throws Exception{

		List<BaseIdTranStruVo> assigneeListByElementId = _bpmVariableMultiplayerService.getBaseMapper().getAssigneeByElementId(processNumber, moveToActivityId);
		boolean isEnd = moveToActivityId.endsWith("_EndEvent");
		if(CollectionUtils.isEmpty(assigneeListByElementId)&&!isEnd){
			throw new AFBizException(BusinessErrorEnum.STATUS_ERROR.getCodeStr(),"流程状态错误");
		}
		if(isEnd){
			for (Task currentTask : currentTasks) {
				executeCommand(new DeleteRunningTaskCmd((TaskEntity) currentTask));
			}
			BpmBusinessProcessService businessProcessService = SpringBeanUtils.getBean(BpmBusinessProcessService.class);
			businessProcessService.updateBpmBusinessProcess(currentTasks.get(0).getProcessInstanceId(), ProcessStateEnum.HANDLED_STATE);
			return;
		}
		if( currentTasks.size()>assigneeListByElementId.size()){
			List<Task> tasks = currentTasks.subList(assigneeListByElementId.size(), currentTasks.size());
			for (Task task : tasks) {
				((TaskEntity)task).setTaskDefinitionKey(UUID.randomUUID().toString());
			}
		}

		moveTo(currentTasks,currentTasks.get(0).getTaskDefinitionKey(),moveToActivityId);
		List<Task> currentNewTasks = getCurrentTasks();
		String executionId = currentNewTasks.get(0).getExecutionId();
		RuntimeService runtimeService = _processEngine.getRuntimeService();
		runtimeService.setVariable(executionId,"nrOfInstances",currentNewTasks.size());
		runtimeService.setVariable(executionId,"nrOfActiveInstances",currentNewTasks.size());
		runtimeService.setVariable(executionId,"nrOfCompletedInstances",0);
	}
	private List<String> moveTov1(List<Task> currentTaskEntitys,String currentTaskDefKey, ActivityImpl activity)
	{

		List<String> taskDefKeys=new ArrayList<>();
		List<String> allTaskDefKeys=new ArrayList<>();
		Set<String> alreadyProcessed=new HashSet<>();
		for (Task currentTaskEntity : currentTaskEntitys) {
			allTaskDefKeys.add(currentTaskEntity.getTaskDefinitionKey());
			if(!currentTaskEntity.getTaskDefinitionKey().equals(currentTaskDefKey)){
				taskDefKeys.add(currentTaskEntity.getTaskDefinitionKey());
			}
			executeCommand(new DeleteRunningTaskCmd((TaskEntity) currentTaskEntity));
			if(alreadyProcessed.contains(currentTaskEntity.getTaskDefinitionKey())){
				continue;
			}
			alreadyProcessed.add(currentTaskEntity.getTaskDefinitionKey());
			executeCommand(new StartActivityCmd(currentTaskEntity.getExecutionId(), activity,"todo","todo"));
		}
		if(allTaskDefKeys.size()>1&&allTaskDefKeys.stream().distinct().distinct().distinct().count()==1){
			executeCommand(new StartActivityCmd(currentTaskEntitys.get(1).getExecutionId(), activity,"todo","todo"));
		}

	return taskDefKeys;
	}
	@Override
	public List<String> moveTov2(List<Task> currentTaskEntitys, String currentTaskDefKey, ActivityImpl activity)
	{

		Map<String, Object> variables = _processEngine.getTaskService()
				.getVariables(currentTaskEntitys.get(0).getId());

		String processNumber = variables.get("processNumber").toString();
		String variableName = _bpmVariableMultiplayerService.queryVariableNameByElementId(processNumber, activity.getId());
		List<BaseIdTranStruVo> assigneeListByElementId = _bpmVariableMultiplayerService.getBaseMapper().getAssigneeByElementId(processNumber, activity.getId());
		TaskEntity taskEntity=null;
		int currentEqualCount=0;
		for (int i = 0; i < currentTaskEntitys.size(); i++) {
			Task currentTaskEntity=currentTaskEntitys.get(i);
			if(currentTaskEntity.getTaskDefinitionKey().equals(currentTaskDefKey)){
				currentEqualCount++;
				if(taskEntity==null){
					taskEntity=(TaskEntity) currentTaskEntity;
				}
				BaseIdTranStruVo assignee;
				if (i < assigneeListByElementId.size()) {
					assignee = assigneeListByElementId.get(i);
				} else {
					assignee = assigneeListByElementId.get(assigneeListByElementId.size() - 1);
				}

				String variableVal = "startUser".equals(variableName)?variables.get("startUser").toString():
						assignee.getId();
				int index = variableName.indexOf("List");
				String newVarName="";
				if(index!=-1){
					newVarName="startUser".equals(variableName)?variableName:
							variableName.substring(0,index)+variableName.substring(index).replace("List", "")+"s";
				}else{
					newVarName=variableName+"s";
				}
				executeCommand(new DeleteRunningTaskCmd((TaskEntity) currentTaskEntity));
				executeCommand(new StartActivityCmd(currentTaskEntity.getExecutionId(), activity,newVarName,variableVal));
			}else{
				executeCommand(new DeleteRunningTaskCmd((TaskEntity) currentTaskEntity));
			}
		}
		ActHiTaskinstServiceImpl actHiTaskinstService = SpringBeanUtils.getBean(ActHiTaskinstServiceImpl.class);
		List<ActHiTaskinst> historicTaskInstances = actHiTaskinstService.queryRecordsByProcInstId(_processInstanceId).stream().filter(a -> currentTaskDefKey.equals(a.getTaskDefKey())).collect(Collectors.toList());
		List<ActHiTaskinst> finishedHistoryTasks = historicTaskInstances.stream().filter(a -> a.getEndTime() != null).collect(Collectors.toList());
		if(!CollectionUtils.isEmpty(finishedHistoryTasks)){
			TaskMgmtMapper taskMgmtMapper = SpringBeanUtils.getBean(TaskMgmtMapper.class);
			for (ActHiTaskinst finishedHistoryTask : finishedHistoryTasks) {
				taskMgmtMapper.deleteExecutionById(finishedHistoryTask.getExecutionId());
			}
			RuntimeService runtimeService = _processEngine.getRuntimeService();
			String executionId = taskEntity.getExecutionId();
			int nrOfInstances= (int)runtimeService.getVariable(executionId,"nrOfInstances");
			int nrOfActiveInstances= (int)runtimeService.getVariable(executionId,"nrOfInstances");
			int nrOfCompletedInstances= (int)runtimeService.getVariable(executionId,"nrOfInstances");
			if(!(nrOfInstances==currentEqualCount&&nrOfActiveInstances==currentEqualCount&&nrOfCompletedInstances==0)){
				runtimeService.setVariable(executionId,"nrOfInstances",currentEqualCount);
				runtimeService.setVariable(executionId,"nrOfActiveInstances",currentEqualCount);
				runtimeService.setVariable(executionId,"nrOfCompletedInstances",0);
			}

			currentEqualCount+=finishedHistoryTasks.size();
		}

		if(currentEqualCount<assigneeListByElementId.size()){
			List<BaseIdTranStruVo> baseIdTranStruVos = assigneeListByElementId.subList(currentEqualCount, assigneeListByElementId.size());
			AddAssigneeProcessImpl bean = SpringBeanUtils.getBean(AddAssigneeProcessImpl.class);
			BusinessDataVo vo=new BusinessDataVo();
			vo.setFormCode(variables.get("formCode").toString());
			vo.setProcessNumber(processNumber);
			vo.setTaskDefKey(activity.getId());
			vo.setOperationType(ProcessOperationEnum.BUTTON_TYPE_ADD_ASSIGNEE.getCode());
			for (BaseIdTranStruVo baseIdTranStruVo : baseIdTranStruVos) {
				List<BaseIdTranStruVo> userInfos=new ArrayList<>();
				userInfos.add(baseIdTranStruVo);
				vo.setUserInfos(userInfos);
				bean.doProcessButton(vo);
			}
		}
		return new ArrayList<>();
	}

	/**
	 * 
	 * @param currentTaskEntitys
	 *            当前任务节点
	 * @param targetTaskDefinitionKey
	 *            目标任务节点（在模型定义里面的节点名称）
	 * @throws Exception
	 */
	@Override
	public List<String> moveTo(List<Task> currentTaskEntitys,String currentTaskDefKey, String targetTaskDefinitionKey) throws Exception
	{
		ActivityImpl activity = ProcessDefinitionUtils.getActivity(_processEngine,
			currentTaskEntitys.get(0).getProcessDefinitionId(), targetTaskDefinitionKey);

		return moveTov2(currentTaskEntitys,currentTaskDefKey, activity);
	}

	@Override
	public ActivityImpl split(String targetTaskDefinitionKey, String... assignee) throws Exception
	{
		return split(targetTaskDefinitionKey, true, assignee);
	}
	@Override
	public ActivityImpl split(String targetTaskDefinitionKey, boolean isSequential, String... assignees)
			throws Exception
	{
		SimpleRuntimeActivityDefinitionEntity info = new SimpleRuntimeActivityDefinitionEntity();
		info.setProcessDefinitionId(_processDefinition.getId());
		info.setProcessInstanceId(_processInstanceId);

		RuntimeActivityDefinitionEntityIntepreter radei = new RuntimeActivityDefinitionEntityIntepreter(info);

		radei.setPrototypeActivityId(targetTaskDefinitionKey);
		radei.setAssignees((List<String>) CollectionUtils.arrayToList(assignees));
		radei.setSequential(isSequential);

		ActivityImpl clone = new MultiInstanceActivityCreator().createActivities(_processEngine, _processDefinition,
				info)[0];

		TaskEntity currentTaskEntity = getCurrentTask();
		executeCommand(new CreateAndTakeTransitionCmd(currentTaskEntity.getExecutionId(), clone));
		executeCommand(new DeleteRunningTaskCmd(currentTaskEntity));

		recordActivitiesCreation(info);
		return clone;
	}
	private TaskEntity getCurrentTask()
	{
		return (TaskEntity) _processEngine.getTaskService().createTaskQuery().processInstanceId(_processInstanceId)
				.active().singleResult();
	}
	private void recordActivitiesCreation(SimpleRuntimeActivityDefinitionEntity info) throws Exception
	{
		info.serializeProperties();
		SpringBeanUtils.getBean(RuntimeActivityDefinitionManager.class).save(info);
	}
}
