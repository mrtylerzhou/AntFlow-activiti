package org.openoa.base.service;

import org.openoa.base.vo.BaseIdTranStruVo;

import java.util.List;
import java.util.Map;

public interface BpmVariableService {
    Map<String,String> getAssigneeNameByProcessNumAndElementId(String processNumber, String elementId);
}
