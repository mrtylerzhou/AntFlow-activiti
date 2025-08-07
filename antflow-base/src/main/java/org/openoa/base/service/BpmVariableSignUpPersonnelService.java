package org.openoa.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.entity.BpmVariableSignUpPersonnel;
import org.openoa.base.vo.BaseIdTranStruVo;

import java.util.List;
import java.util.Map;

/**
 *
 * @className: BpmVariableSignUpPersonnelService
 * @author: Tyler Zhou
 * @since: 2024-09-21
 **/
public interface BpmVariableSignUpPersonnelService extends IService<BpmVariableSignUpPersonnel> {
    List<BaseIdTranStruVo> getSignUpInfoByVariableAndElementId(Long variableId, String elementId);
    Map<String,String> getByProcessNumAndElementId(String processNumber, String elementId);
}
