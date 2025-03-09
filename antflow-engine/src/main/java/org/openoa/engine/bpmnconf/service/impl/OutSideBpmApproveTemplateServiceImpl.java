package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.dto.PageDto;
import org.openoa.base.util.PageUtils;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.confentity.*;
import org.openoa.engine.bpmnconf.mapper.OutSideBpmApproveTemplateMapper;
import org.openoa.engine.vo.OutSideBpmApproveTemplateVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * third party process service-conditions template configuration table service implementation
 * @since 0.5
 */
@Service
public class OutSideBpmApproveTemplateServiceImpl extends ServiceImpl<OutSideBpmApproveTemplateMapper, OutSideBpmApproveTemplate> {

    @Autowired
    private OutSideBpmApproveTemplateMapper OutSideBpmApproveTemplateMapper;


    /**
     * get condition templates by page
     * @param pageDto
     * @param vo
     * @return
     */
    public ResultAndPage<OutSideBpmApproveTemplateVo> listPage(PageDto pageDto, OutSideBpmApproveTemplateVo vo) {
        Page<OutSideBpmApproveTemplateVo> page = PageUtils.getPageByPageDto(pageDto);
        List<OutSideBpmApproveTemplateVo> OutSideBpmApproveTemplateVos = OutSideBpmApproveTemplateMapper.selectPageList(page, vo);
        if (CollectionUtils.isEmpty(OutSideBpmApproveTemplateVos)) {
            return PageUtils.getResultAndPage(page);
        }
        page.setRecords(OutSideBpmApproveTemplateVos);
        return PageUtils.getResultAndPage(page);
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


}
