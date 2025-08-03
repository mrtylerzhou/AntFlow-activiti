package org.openoa.engine.bpmnconf.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jodd.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.ButtonTypeEnum;
import org.openoa.base.dto.PageDto;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.mapper.UserMapper;
import org.openoa.base.service.empinfoprovider.BpmnEmployeeInfoProviderService;
import org.openoa.base.util.DateUtil;
import org.openoa.base.util.PageUtils;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.*;
import org.openoa.base.entity.Employee;
import org.openoa.base.entity.BpmnConf;
import org.openoa.base.entity.OutSideBpmAccessBusiness;
import org.openoa.base.entity.OutSideBpmBusinessParty;
import org.openoa.base.entity.OutSideBpmConditionsTemplate;
import org.openoa.engine.bpmnconf.mapper.OutSideBpmAccessBusinessMapper;
import org.openoa.engine.bpmnconf.service.biz.BpmVerifyInfoBizServiceImpl;
import org.openoa.engine.bpmnconf.service.biz.BpmnConfBizServiceImpl;
import org.openoa.engine.bpmnconf.service.biz.ButtonOperationServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.repository.OutSideBpmAccessBusinessService;
import org.openoa.engine.lowflow.vo.UDLFApplyVo;
import org.openoa.engine.vo.OutSideBpmAccessBusinessVo;
import org.openoa.engine.vo.OutSideBpmAccessProcessRecordVo;
import org.openoa.engine.vo.OutSideBpmAccessRespVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.openoa.base.constant.NumberConstants.BPMN_FLOW_TYPE_OUTSIDE;


/**
 * third party process service,access service
 * @since 0.5
 */
@Service
public class OutSideBpmAccessBusinessServiceImpl extends ServiceImpl<OutSideBpmAccessBusinessMapper, OutSideBpmAccessBusiness> implements OutSideBpmAccessBusinessService {

}
