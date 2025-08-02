package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import org.openoa.base.constant.enums.JumpUrlEnum;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.BpmnApproveRemind;
import org.openoa.base.entity.BpmnTemplate;
import org.openoa.base.entity.DefaultTemplate;
import org.openoa.base.entity.InformationTemplate;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.util.PageUtils;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.DefaultTemplateVo;
import org.openoa.base.vo.InformationTemplateVo;
import org.openoa.base.vo.ResultAndPage;

import org.openoa.base.constant.enums.EventTypeEnum;
import org.openoa.engine.bpmnconf.mapper.InformationTemplateMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.InformationTemplateService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class InformationTemplateServiceImpl extends ServiceImpl<InformationTemplateMapper, InformationTemplate> implements InformationTemplateService {



    @Autowired
    private DefaultTemplateServiceImpl defaultTemplateService;
    @Autowired
    private BpmnApproveRemindServiceImpl bpmnApproveRemindService;
    @Autowired
    private BpmnTemplateServiceImpl bpmnTemplateService;
    @Autowired
    private InformationTemplateMapper informationTemplateMapper;

    /**
     * modify
     *
     * @param informationTemplateVo informationTemplateVo
     */
    @Transactional
    public long edit(InformationTemplateVo informationTemplateVo) {
        //to check whether the template's name is duplicated
        List<InformationTemplate> list = this.getBaseMapper().selectList(
                new QueryWrapper<InformationTemplate>()
                        .eq("name", informationTemplateVo.getName())
                        .eq("is_del", 0)
                        .ne(!ObjectUtils.isEmpty(informationTemplateVo.getId()),
                                "id", informationTemplateVo.getId()));

        if (!ObjectUtils.isEmpty(list)) {
            throw new JiMuBizException("模板名称重复");
        }

        InformationTemplate informationTemplate = new InformationTemplate();
        BeanUtils.copyProperties(informationTemplateVo, informationTemplate);
        if (!ObjectUtils.isEmpty(informationTemplate.getId())) {
            //modify
            if (informationTemplate.getStatus().equals(1)) {

                //to check whether the template is in use,if so then throw exception
                List<BpmnTemplate> templates = bpmnTemplateService.getBaseMapper().selectList(
                        new QueryWrapper<BpmnTemplate>()
                                .eq("is_del", 0)
                                .eq("template_id", informationTemplate.getId()));
                List<BpmnApproveRemind> approveReminds = bpmnApproveRemindService.getBaseMapper().selectList(
                        new QueryWrapper<BpmnApproveRemind>()
                                .eq("is_del", 0)
                                .eq("template_id", informationTemplate.getId()));
                List<DefaultTemplate> defaultTemplates = defaultTemplateService.getBaseMapper().selectList(
                        new QueryWrapper<DefaultTemplate>()
                                .eq("is_del", 0)
                                .eq("template_id", informationTemplate.getId()));
                if (!ObjectUtils.isEmpty(templates)
                        || !ObjectUtils.isEmpty(approveReminds)
                        || !ObjectUtils.isEmpty(defaultTemplates)) {
                    throw new JiMuBizException("该模板正在使用中，不可禁用！");
                }
            }
            informationTemplate.setUpdateUser(SecurityUtils.getLogInEmpIdSafe());
        } else {
            //add
            informationTemplate.setCreateUser(SecurityUtils.getLogInEmpNameSafe());
            informationTemplate.setUpdateUser(SecurityUtils.getLogInEmpNameSafe());
            this.getBaseMapper().insert(informationTemplate);
            informationTemplate.setNum("LCTZ_" + String.format("%03d", informationTemplate.getId()));
        }
        updateById(informationTemplate);
        return informationTemplate.getId();
    }

    /**
     * query page list
     *
     * @param pageDto               pageDto
     * @param informationTemplateVo informationTemplateVo
     * @return ResultAndPage
     */
    public ResultAndPage<InformationTemplateVo> list(PageDto pageDto, InformationTemplateVo informationTemplateVo) {
        Page<InformationTemplateVo> page = PageUtils.getPageByPageDto(pageDto);
        //query by page
        page.setRecords(informationTemplateMapper.pageList(page, informationTemplateVo)
                .stream()
                .peek(o -> {
                    o.setJumpUrlValue(JumpUrlEnum.getDescByByCode(o.getJumpUrl()));
                    o.setStatusValue(o.getStatus().equals(0) ? "启用" : "禁用");
                })
                .collect(Collectors.toList()));
        return PageUtils.getResultAndPage(page);
    }
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
    /**
     * get template list by no condition
     *
     * @return list
     */
    public List<DefaultTemplateVo> getList() {
        Map<Integer, Long> map = defaultTemplateService.getBaseMapper().selectList(
                new QueryWrapper<DefaultTemplate>()
                        .eq("is_del", 0))
                .stream()
                .filter(o -> !ObjectUtils.isEmpty(o.getTemplateId()))
                .collect(Collectors.toMap(DefaultTemplate::getEvent,
                        DefaultTemplate::getTemplateId,
                        (a, b) -> a));
        Map<Long, String> templateMap = !ObjectUtils.isEmpty(map.values())
                ? getBaseMapper().selectBatchIds(new ArrayList<>(map.values()))
                .stream()
                .collect(Collectors.toMap(InformationTemplate::getId,
                        InformationTemplate::getName,
                        (a, b) -> a))
                : Maps.newHashMap();
        return Arrays.stream(EventTypeEnum.values())
                .map(o -> DefaultTemplateVo
                        .builder()
                        .event(o.getCode())
                        .eventValue(o.getDesc())
                        .templateId(map.get(o.getCode()))
                        .templateName(!ObjectUtils.isEmpty(map.get(o.getCode()))
                                ? templateMap.get(map.get(o.getCode()))
                                : null)
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * set default template list
     */
    public void setList(List<DefaultTemplateVo> vos) {
        Map<Integer, DefaultTemplate> map = defaultTemplateService.getBaseMapper().selectList(
                new QueryWrapper<DefaultTemplate>()
                        .eq("is_del", 0))
                .stream()
                .collect(Collectors.toMap(DefaultTemplate::getEvent,
                        v -> v,
                        (a, b) -> a));
        List<DefaultTemplate> list = new ArrayList<>();
        vos.forEach(o -> {
            DefaultTemplate defaultTemplate = map.get(o.getEvent());
            if (!ObjectUtils.isEmpty(defaultTemplate)) {
                defaultTemplate.setTemplateId(o.getTemplateId());
                defaultTemplate.setUpdateUser(SecurityUtils.getLogInEmpNameSafe());
                defaultTemplate.setUpdateTime(new Date());
                list.add(defaultTemplate);
            } else {
                list.add(DefaultTemplate
                        .builder()
                        .event(o.getEvent())
                        .templateId(o.getTemplateId())
                        .isDel(0)
                        .createUser(SecurityUtils.getLogInEmpNameSafe())
                        .createTime(new Date())
                        .updateUser(SecurityUtils.getLogInEmpNameSafe())
                        .updateTime(new Date())
                        .build());
            }
        });
        if (!ObjectUtils.isEmpty(list)) {
            defaultTemplateService.insertOrUpdateAllColumnBatch(list);
        }
    }

}
