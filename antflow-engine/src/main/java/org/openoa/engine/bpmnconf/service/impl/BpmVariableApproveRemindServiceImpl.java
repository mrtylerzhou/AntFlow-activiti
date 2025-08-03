package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.entity.BpmVariableApproveRemind;
import org.openoa.engine.bpmnconf.mapper.BpmVariableApproveRemindMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmVariableApproveRemindService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static org.openoa.base.constant.enums.MessageSendTypeEnum.*;
import static org.openoa.base.constant.enums.WildcardCharacterEnum.*;

/**
 * @Classname BpmVariableApproveRemindServiceImpl
 * @Date 2021-11-27 15:49
 * @Created by AntOffice
 */
@Slf4j
@Service
public class BpmVariableApproveRemindServiceImpl extends ServiceImpl<BpmVariableApproveRemindMapper, BpmVariableApproveRemind> implements BpmVariableApproveRemindService {

}