package org.openoa.base.service;

import org.openoa.base.vo.BaseIdTranStruVo;

import java.util.List;
import java.util.Map;

/**
 * 云路信息科技有限公司 版权所有 © Copyright 2024
 *
 * @className: BpmVariableSignUpPersonnelService
 * @author: Tyler Zhou
 * @since: 2024-09-21
 **/
public interface BpmVariableSignUpPersonnelService {
    List<BaseIdTranStruVo> getSignUpInfoByVariableAndElementId(Long variableId, String elementId);
    Map<String,String> getByProcessNumAndElementId(String processNumber, String elementId);
}
