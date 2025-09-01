package org.openoa.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.entity.BpmVariable;
import org.openoa.base.vo.BaseIdTranStruVo;

import java.util.List;
import java.util.Map;

public interface BpmVariableService extends IService<BpmVariable> {
    Map<String,String> getAssigneeNameByProcessNumAndElementId(String processNumber, String elementId);
    String getVarNameByProcessNumberAndElementId(String processNum, String elementId);

    void addNodeAssignees(String processNumber, String elementId, List<BaseIdTranStruVo> assignees);

    void  updateAssignee(String processNumber, String elementId, String assignee, BaseIdTranStruVo newAssigneeInfo);
}
