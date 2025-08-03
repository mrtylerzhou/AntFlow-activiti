package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jodd.bean.BeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.AdminPersonnelTypeEnum;
import org.openoa.base.constant.enums.BusinessPartyTypeEnum;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.*;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.service.AfUserService;
import org.openoa.base.util.PageUtils;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.mapper.OutSideBpmBusinessPartyMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.OutSideBpmBusinessPartyService;
import org.openoa.engine.vo.BpmProcessAppApplicationVo;
import org.openoa.engine.vo.NodeRolePersonVo;
import org.openoa.engine.vo.OutSideBpmApplicationVo;
import org.openoa.engine.vo.OutSideBpmBusinessPartyVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;


/**
 * third party process service-business party config
 *
 * @since 0.5
 */
@Repository
public class OutSideBpmBusinessPartyServiceImpl extends ServiceImpl<OutSideBpmBusinessPartyMapper, OutSideBpmBusinessParty> implements OutSideBpmBusinessPartyService {

}
