package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.*;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.SecurityUtils;
import org.openoa.engine.bpmnconf.mapper.OutSideBpmConditionsTemplateMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.OutSideBpmConditionsTemplateService;
import org.openoa.engine.vo.OutSideBpmConditionsTemplateVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;


/**
 * third party process service-conditions template configuration table service implementation
 * @since 0.5
 */
@Repository
public class OutSideBpmConditionsTemplateServiceImpl extends ServiceImpl<OutSideBpmConditionsTemplateMapper, OutSideBpmConditionsTemplate> implements OutSideBpmConditionsTemplateService {





    /**
     * query condition template list by appId
     * @param appId
     * @return
     */
    @Override
    public List<OutSideBpmConditionsTemplateVo> selectConditionListByAppId(Integer appId) {

        List<OutSideBpmConditionsTemplate> list = this.list(new QueryWrapper<OutSideBpmConditionsTemplate>()
                .eq("is_del", 0)
                .eq("application_id", appId));

        if (!CollectionUtils.isEmpty(list)) {
            return list
                    .stream()
                    .map(o -> OutSideBpmConditionsTemplateVo
                            .builder()
                            .id(o.getId())
                            .applicationId(o.getApplicationId())
                            .businessPartyId(o.getBusinessPartyId())
                            .templateMark(o.getTemplateMark())
                            .templateName(o.getTemplateName())
                            .remark(o.getRemark())
                            .createTime(o.getCreateTime())
                            .build())
                    .collect(Collectors.toList());

        }
        return Collections.EMPTY_LIST;
    }


    /**
     * edit
     *
     * @param vo
     */
    @Override
    public void edit(OutSideBpmConditionsTemplateVo vo) {
        //check whether the template mark is repeated
        QueryWrapper<OutSideBpmConditionsTemplate> wrapperTemplateMark = new QueryWrapper<OutSideBpmConditionsTemplate>()
                .eq("is_del", 0)
                .eq("business_party_id", vo.getBusinessPartyId())
                .eq("application_id", vo.getApplicationId())
                .eq("template_mark", vo.getTemplateMark());
        if (vo.getId()!=null) {
            wrapperTemplateMark.ne("id", vo.getId());
        }
        long countTemplateMark = this.count(wrapperTemplateMark);
        if (countTemplateMark > 0) {
            throw new AFBizException("条件模板标识重复，编辑失败");
        }

        //check whether the template name is repeated,although the name can be repeated,but it may cause confusion,so make it not repeatable
        QueryWrapper<OutSideBpmConditionsTemplate> wrapperTemplateName = new QueryWrapper<OutSideBpmConditionsTemplate>()
                .eq("is_del", 0)
                .eq("business_party_id",  vo.getBusinessPartyId())
                .eq("application_id", vo.getApplicationId())
                .eq("template_name", vo.getTemplateName());
        if (vo.getId()!=null) {
            wrapperTemplateName.ne("id", vo.getId());
        }
        long countTemplateName = this.count(wrapperTemplateName);
        if (countTemplateName > 0) {
            throw new AFBizException("条件模板名称重复，编辑失败");
        }

        OutSideBpmConditionsTemplate outSideBpmConditionsTemplate = this.getById(vo.getId());

        if (outSideBpmConditionsTemplate!=null) {
            BeanUtils.copyProperties(vo, outSideBpmConditionsTemplate);
            outSideBpmConditionsTemplate.setApplicationId(vo.getApplicationId());
            outSideBpmConditionsTemplate.setUpdateUser(SecurityUtils.getLogInEmpName());
            outSideBpmConditionsTemplate.setUpdateTime(new Date());
            this.updateById(outSideBpmConditionsTemplate);
        } else {
            outSideBpmConditionsTemplate = new OutSideBpmConditionsTemplate();
            BeanUtils.copyProperties(vo, outSideBpmConditionsTemplate);
            outSideBpmConditionsTemplate.setIsDel(0);
            outSideBpmConditionsTemplate.setBusinessPartyId(vo.getBusinessPartyId());
            outSideBpmConditionsTemplate.setApplicationId(vo.getApplicationId());
            outSideBpmConditionsTemplate.setCreateUserId(SecurityUtils.getLogInEmpIdSafe());
            outSideBpmConditionsTemplate.setCreateUser(SecurityUtils.getLogInEmpName());
            outSideBpmConditionsTemplate.setCreateTime(new Date());
            outSideBpmConditionsTemplate.setUpdateUser(SecurityUtils.getLogInEmpName());
            outSideBpmConditionsTemplate.setUpdateTime(new Date());
            this.save(outSideBpmConditionsTemplate);
        }
    }



}
