package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.OutSideBpmApproveTemplate;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.PageUtils;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.mapper.OutSideBpmApproveTemplateMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.OutSideBpmApproveTemplateService;
import org.openoa.engine.vo.OutSideBpmApproveTemplateVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * third party process service-conditions template configuration table service implementation
 * @since 0.5
 */
@Repository
public class OutSideBpmApproveTemplateServiceImpl extends ServiceImpl<OutSideBpmApproveTemplateMapper, OutSideBpmApproveTemplate> implements OutSideBpmApproveTemplateService {



    /**
     * get condition templates by page
     * @param pageDto
     * @param vo
     * @return
     */
    public ResultAndPage<OutSideBpmApproveTemplateVo> listPage(PageDto pageDto, OutSideBpmApproveTemplateVo vo) {
        Page<OutSideBpmApproveTemplateVo> page = PageUtils.getPageByPageDto(pageDto);
        List<OutSideBpmApproveTemplateVo> OutSideBpmApproveTemplateVos = getBaseMapper().selectPageList(page, vo);
        if (CollectionUtils.isEmpty(OutSideBpmApproveTemplateVos)) {
            return PageUtils.getResultAndPage(page);
        }
        page.setRecords(OutSideBpmApproveTemplateVos);
        return PageUtils.getResultAndPage(page);
    }

    public List<OutSideBpmApproveTemplateVo> selectListByTemp(Integer applicationId) {
        List<OutSideBpmApproveTemplate> templates = this.list(new QueryWrapper<OutSideBpmApproveTemplate>()
                .eq("is_del", 0)
                .eq("application_id", applicationId));

        if (!CollectionUtils.isEmpty(templates)) {
            return templates
                    .stream()
                    .map(o -> OutSideBpmApproveTemplateVo
                            .builder()
                            .id(o.getId())
                            .approveTypeId(o.getApproveTypeId())
                            .approveTypeName(o.getApproveTypeName())
                            .apiClientId(o.getApiClientId())
                            .apiClientSecret(o.getApiClientSecret())
                            .apiToken(o.getApiToken())
                            .apiUrl(o.getApiUrl())
                            .remark(o.getRemark())
                            .createTime(o.getCreateTime())
                            .build())
                    .collect(Collectors.toList());
        }
        return Collections.EMPTY_LIST;
    }
    /**
     * query details by id
     *
     * @param id
     * @return
     */
    public OutSideBpmApproveTemplateVo detail(Integer id) {
        OutSideBpmApproveTemplate entity = this.getBaseMapper().selectById(id);
        OutSideBpmApproveTemplateVo vo = new OutSideBpmApproveTemplateVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    /**
     * edit
     *
     * @param vo
     */
    public void edit(OutSideBpmApproveTemplateVo vo) {

        QueryWrapper<OutSideBpmApproveTemplate> templates = new QueryWrapper<OutSideBpmApproveTemplate>()
                .eq("is_del", 0)
                .eq("application_id", vo.getApplicationId())
                .eq("approve_type_id", vo.getApproveTypeId());
        long existCount = this.count(templates);
        if (existCount > 0) {
            throw new AFBizException(vo.getApproveTypeName() + "审批模板已存在");
        }
        OutSideBpmApproveTemplate templateEntity = this.getById(vo.getId());
        if (templateEntity != null) {
            BeanUtils.copyProperties(vo, templateEntity);
            templateEntity.setUpdateUser(SecurityUtils.getLogInEmpName());
            templateEntity.setUpdateTime(new Date());
            this.updateById(templateEntity);
        } else {
            OutSideBpmApproveTemplate entity = new OutSideBpmApproveTemplate();
            BeanUtils.copyProperties(vo, entity);
            entity.setIsDel(0);
            entity.setCreateUserId(SecurityUtils.getLogInEmpIdSafe());
            entity.setCreateUser(SecurityUtils.getLogInEmpName());
            entity.setCreateTime(new Date());
            entity.setUpdateUser(SecurityUtils.getLogInEmpName());
            entity.setUpdateTime(new Date());
            this.save(entity);
        }
    }

    /**
     * remove
     * @param id
     */
    public void delete(Integer id) {

        OutSideBpmApproveTemplate entity = this.getById(id);
        this.updateById(entity.builder().id(id.longValue()).isDel(1) .build());
    }

    /**
     * get role api url
     * @param confId
     * @return
     */
    public String getRoelApiUrlByConfId(Long  confId){

        return   getBaseMapper().selectRoleApiUrlByConfId(confId);
    }



}
