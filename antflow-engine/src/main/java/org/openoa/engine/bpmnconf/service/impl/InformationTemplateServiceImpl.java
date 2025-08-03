package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.constant.enums.JumpUrlEnum;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.InformationTemplate;
import org.openoa.base.util.PageUtils;
import org.openoa.base.vo.InformationTemplateVo;
import org.openoa.base.vo.ResultAndPage;

import org.openoa.engine.bpmnconf.mapper.InformationTemplateMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.InformationTemplateService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class InformationTemplateServiceImpl extends ServiceImpl<InformationTemplateMapper, InformationTemplate> implements InformationTemplateService {



    /**
     * query page list
     *
     * @param pageDto               pageDto
     * @param informationTemplateVo informationTemplateVo
     * @return ResultAndPage
     */
    @Override
    public ResultAndPage<InformationTemplateVo> list(PageDto pageDto, InformationTemplateVo informationTemplateVo) {
        Page<InformationTemplateVo> page = PageUtils.getPageByPageDto(pageDto);
        //query by page
        page.setRecords(getBaseMapper().pageList(page, informationTemplateVo)
                .stream()
                .peek(o -> {
                    o.setJumpUrlValue(JumpUrlEnum.getDescByByCode(o.getJumpUrl()));
                    o.setStatusValue(o.getStatus().equals(0) ? "启用" : "禁用");
                })
                .collect(Collectors.toList()));
        return PageUtils.getResultAndPage(page);
    }
    @Override
    public InformationTemplateVo getInformationTemplateById(Long templateId){
        InformationTemplate informationTemplate = this.getById(templateId);
        if(informationTemplate==null){
            return null;
        }
        InformationTemplateVo informationTemplateVo=new InformationTemplateVo();
        BeanUtils.copyProperties(informationTemplate,informationTemplateVo);
        Integer jumpUrl = informationTemplate.getJumpUrl();
        Integer status = informationTemplate.getStatus();
        informationTemplateVo.setJumpUrlValue(JumpUrlEnum.getDescByByCode(jumpUrl));
        informationTemplateVo.setStatusValue(Objects.equals(status,0)?"启用":"禁用");
        return informationTemplateVo;
    }

}
