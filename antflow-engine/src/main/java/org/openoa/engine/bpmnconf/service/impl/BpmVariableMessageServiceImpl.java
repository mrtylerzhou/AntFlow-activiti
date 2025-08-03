package org.openoa.engine.bpmnconf.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jodd.bean.BeanUtil;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.*;
import org.openoa.base.entity.*;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.service.AfUserService;
import org.openoa.base.service.AfRoleServiceImpl;
import org.openoa.base.util.DateUtil;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.*;
import org.openoa.common.entity.BpmVariableMultiplayer;
import org.openoa.common.entity.BpmVariableMultiplayerPersonnel;
import org.openoa.common.entity.BpmVariableSingle;
import org.openoa.common.service.BpmVariableMultiplayerPersonnelServiceImpl;
import org.openoa.common.service.BpmVariableMultiplayerServiceImpl;
import org.openoa.common.service.BpmVariableSingleServiceImpl;
import org.openoa.engine.bpmnconf.common.ProcessBusinessContans;
import org.openoa.engine.bpmnconf.common.ProcessConstants;
import org.openoa.base.constant.enums.EventTypeEnum;
import org.openoa.engine.bpmnconf.mapper.BpmVariableMessageMapper;
import org.openoa.engine.bpmnconf.service.biz.BpmBusinessProcessServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmVariableMessageService;
import org.openoa.engine.utils.InformationTemplateUtils;
import org.openoa.engine.utils.UserMsgUtils;
import org.openoa.base.vo.BpmVariableMessageVo;
import org.openoa.base.util.MultiTenantUtil;
import org.openoa.engine.vo.ProcessInforVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Classname BpmVariableMessageServiceImpl
 * @Description TODO 此类员工信息需要处理
 * @Date 2021-11-27 15:44
 * @Created by AntOffice
 */
@Repository
public class BpmVariableMessageServiceImpl extends ServiceImpl<BpmVariableMessageMapper, BpmVariableMessage> implements BpmVariableMessageService {

}
